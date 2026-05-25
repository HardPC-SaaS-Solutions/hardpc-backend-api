package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.dto.*;
import com.hardpc.saas.backendapi.entity.*;
import com.hardpc.saas.backendapi.enums.EstadoDisponibilidad;
import com.hardpc.saas.backendapi.enums.EstadoVenta;
import com.hardpc.saas.backendapi.enums.TipoMovimiento;
import com.hardpc.saas.backendapi.exception.custom.BusinessException;
import com.hardpc.saas.backendapi.mapper.VentaMapper;
import com.hardpc.saas.backendapi.repository.*;
import com.hardpc.saas.backendapi.service.MovimientoInventarioService;
import com.hardpc.saas.backendapi.service.VentaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.hardpc.saas.backendapi.security.CustomUserDetails;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

    private final VentaRepository repository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoComprobanteRepository tipoComprobanteRepository;
    private final FormaPagoRepository formaPagoRepository;
    private final LocalRepository localRepository;
    private final ProductoRepository productoRepository;
    private final ItemSerialRepository itemSerialRepository;
    private final StockLocalRepository stockLocalRepository; // PROACTIVIDAD: Validar stock a granel

    // Orquestación
    private final MovimientoInventarioService movimientoInventarioService;
    private final VentaMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public VentaResponseDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaResponseDTO> listarPaginadoAvanzado(LocalDateTime inicio, LocalDateTime fin, Long idCliente, Long idLocal, Pageable pageable) {
        return repository.buscarVentasAvanzado(inicio, fin, idCliente, idLocal, pageable)
                .map(mapper::toResponseDTO);
    }

    // --- BLOQUE 1: EL ORQUESTADOR TRANSACCIONAL CORE ---

    @Override
    @Transactional(rollbackFor = Exception.class) // ESTRICTO: Cualquier error interno cancela la venta entera
    public VentaResponseDTO registrarVenta(VentaRequestDTO dto) {

        // --- FIX DE SEGURIDAD: Extraer el cajero autenticado del JWT ---
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Usuario cajeroLogueado = userDetails.getUsuario();
        // ---------------------------------------------------------------

        // 1. Validaciones Maestras y Ligeras
        validarMaestras(dto);
        validarDocumentoUnico(dto);

        // 2. Validaciones Lógicas y Matemáticas Estrictas
        validarMatematicaTributaria(dto);

        // 3. Validaciones Físicas de Inventario y Serialización (CRÍTICO)
        validarYExtraerFisicos(dto);

        // 4. Mapeo y Persistencia de Cabecera y Detalles (Cascade)
        Venta entidad = mapper.toEntity(dto);
        entidad.setIdVenta(null);
        entidad.setEstadoVenta(EstadoVenta.REGISTRADA);
        entidad.setFechaVenta(LocalDateTime.now());

        // --- FIX DE SEGURIDAD: Inyectamos el usuario real ---
        entidad.setUsuario(cajeroLogueado);

        // Inyectar el precio histórico en los detalles
        for (DetalleVenta detalle : entidad.getDetalles()) {
            Producto producto = productoRepository.findById(detalle.getProducto().getIdProducto()).get();
            detalle.setPrecioVentaUnitario(producto.getPrecioUsd());
        }

        // El @AfterMapping del mapper enlaza bidireccionalmente los detalles
        Venta ventaGuardada = repository.save(entidad);

        // 5. Orquestación Colateral (Pasamos el ID del cajero real)
        orquestarEfectosColaterales(dto, ventaGuardada, cajeroLogueado.getIdPersona());

        return mapper.toResponseDTO(ventaGuardada);
    }

    // --- BLOQUE 2: REPORTES DE INTELIGENCIA DE NEGOCIO (BI) ---

    @Override
    @Transactional(readOnly = true)
    public List<IngresoMensualDTO> obtenerReporteIngresoMensual() {
        return repository.obtenerIngresoMensual();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentasPorClienteDTO> obtenerReporteVentasPorCliente() {
        return repository.obtenerVentasPorCliente();
    }

    // =========================================================================================
    // --- MÉTODOS PRIVADOS DE ORQUESTACIÓN Y VALIDACIÓN ---
    // =========================================================================================

    private void validarMaestras(VentaRequestDTO dto) {
        // En lugar de instanciar entidades completas para validar, usamos existsById, lo cual
        // genera un "SELECT 1" ultra-rápido en BBDD ahorrando memoria transaccional.
        if (!clienteRepository.existsById(dto.getIdCliente())) throw new EntityNotFoundException("Cliente no existe.");
        if (!tipoComprobanteRepository.existsById(dto.getIdTipoComprobante())) throw new EntityNotFoundException("Tipo comprobante no existe.");
        if (!formaPagoRepository.existsById(dto.getIdFormaPago())) throw new EntityNotFoundException("Forma de pago no existe.");
        if (!localRepository.existsById(dto.getIdLocal())) throw new EntityNotFoundException("Local no existe.");
    }

    private void validarDocumentoUnico(VentaRequestDTO dto) {
        if (repository.existsByTipoComprobante_IdTipoComprobanteAndSerieComprobanteAndNumeroComprobante(
                dto.getIdTipoComprobante(), dto.getSerieComprobante(), dto.getNumeroComprobante())) {
            throw new BusinessException(HttpStatus.CONFLICT, "ERR_VENTA_DUPLICADA", "Esta factura ya fue emitida.");
        }
    }

    private void validarMatematicaTributaria(VentaRequestDTO dto) {
        BigDecimal subtotal = BigDecimal.ZERO;

        for (VentaRequestDTO.DetalleRequestDTO det : dto.getDetalles()) {

            // 1. Extraemos el producto de la BD para sacar su precio real
            Producto producto = productoRepository.findById(det.getIdProducto())
                    .orElseThrow(() -> new EntityNotFoundException("Producto ID " + det.getIdProducto() + " no existe."));

            BigDecimal precioReal = producto.getPrecioUsd();

            BigDecimal cantidad = BigDecimal.valueOf(det.getCantidad());

            // 2. Multiplicamos la cantidad por el precio blindado del servidor
            BigDecimal subtotalLineaBruto = cantidad.multiply(precioReal);

            if (det.getDescuento().compareTo(subtotalLineaBruto) > 0) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_DESCUENTO_EXCESIVO", "El descuento no puede ser mayor al precio total de la línea.");
            }

            BigDecimal subtotalLineaNeto = subtotalLineaBruto.subtract(det.getDescuento());
            subtotal = subtotal.add(subtotalLineaNeto);
        }

        // Validación 1: Subtotal + Impuesto == Total Venta (A + B = C)
        BigDecimal totalCalculado = subtotal.add(dto.getImpuesto()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalRecibido = dto.getTotalVenta().setScale(2, RoundingMode.HALF_UP);
        if (totalCalculado.compareTo(totalRecibido) != 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_TOTAL_VENTA_MISMATCH",
                    "Error de caja: Subtotal (" + subtotal + ") + IGV (" + dto.getImpuesto() + ") = " + totalCalculado + ". Se recibió: " + totalRecibido);
        }

        // Validación 2: Impuesto Tributario (~18% en Perú, tolerancia 0.15 por redondeos de sistemas externos)
        BigDecimal impuestoEsperado = subtotal.multiply(new BigDecimal("0.18"));
        BigDecimal diferenciaImpuesto = dto.getImpuesto().subtract(impuestoEsperado).abs();
        if (diferenciaImpuesto.compareTo(new BigDecimal("0.15")) > 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_IMPUESTO_INVALIDO",
                    "Inconsistencia Tributaria: El IGV enviado difiere del 18% reglamentario del subtotal.");
        }
    }

    private void validarYExtraerFisicos(VentaRequestDTO dto) {
        for (VentaRequestDTO.DetalleRequestDTO det : dto.getDetalles()) {
            Producto producto = productoRepository.findById(det.getIdProducto())
                    .orElseThrow(() -> new EntityNotFoundException("Producto ID " + det.getIdProducto() + " no existe."));

            if (Boolean.TRUE.equals(producto.getEsSerializado())) {
                if (det.getNumerosSerie() == null || det.getNumerosSerie().size() != det.getCantidad()) {
                    throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_SERIALES_FALTANTES", "Debe proveer exactamente " + det.getCantidad() + " serial(es) para el producto " + producto.getCodigoSku());
                }

                for (String serial : det.getNumerosSerie()) {
                    ItemSerial item = itemSerialRepository.findByNumeroSerieIgnoreCase(serial)
                            .orElseThrow(() -> new EntityNotFoundException("El número de serie '" + serial + "' no existe en BD."));

                    if (!item.getProducto().getIdProducto().equals(producto.getIdProducto())) {
                        throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_SERIAL_INCORRECTO", "El serial " + serial + " no pertenece al producto seleccionado.");
                    }
                    if (!item.getLocal().getIdLocal().equals(dto.getIdLocal())) {
                        throw new BusinessException(HttpStatus.CONFLICT, "ERR_SERIAL_OTRO_LOCAL", "El serial " + serial + " pertenece a otro local y no puede venderse desde aquí.");
                    }
                    if (item.getEstadoDisponibilidad() != EstadoDisponibilidad.DISPONIBLE) {
                        throw new BusinessException(HttpStatus.CONFLICT, "ERR_SERIAL_NO_DISPONIBLE", "El serial " + serial + " se encuentra en estado " + item.getEstadoDisponibilidad() + " y no puede ser vendido.");
                    }
                }
            } else {
                if (det.getNumerosSerie() != null && !det.getNumerosSerie().isEmpty()) {
                    throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_SERIALES_PROHIBIDOS", "El producto " + producto.getCodigoSku() + " no es serializado.");
                }

                // PROACTIVIDAD ARQUITECTÓNICA: Verificar que exista stock físico suficiente para los productos a granel.
                boolean hayStock = stockLocalRepository.hasStockSuficiente(producto.getIdProducto(), dto.getIdLocal(), det.getCantidad());
                if (!hayStock) {
                    throw new BusinessException(HttpStatus.CONFLICT, "ERR_STOCK_INSUFICIENTE", "El producto " + producto.getCodigoSku() + " no tiene stock suficiente (Solicitado: " + det.getCantidad() + ") en este local.");
                }
            }
        }
    }

    private void orquestarEfectosColaterales(VentaRequestDTO dto, Venta ventaGuardada, Long idUsuarioReal) {
        for (int i = 0; i < dto.getDetalles().size(); i++) {
            VentaRequestDTO.DetalleRequestDTO detDto = dto.getDetalles().get(i);
            DetalleVenta detGuardado = ventaGuardada.getDetalles().get(i);

            Producto producto = productoRepository.findById(detDto.getIdProducto()).get();

            if (Boolean.TRUE.equals(producto.getEsSerializado())) {
                // Registrar cada serial por separado en el Ledger
                for (String serial : detDto.getNumerosSerie()) {
                    ItemSerial item = itemSerialRepository.findByNumeroSerieIgnoreCase(serial).get();

                    // 1. Cambiar estado físico para activar Garantía / Bloquear reventas
                    item.setEstadoDisponibilidad(EstadoDisponibilidad.VENDIDO);
                    itemSerialRepository.save(item);

                    // 2. Registrar en Ledger (SALIDA unitaria)
                    // NOTA ARQUITECTÓNICA: Usaremos ItemSerial en el propio detalle guardado si tu modelo de datos lo permitiese,
                    // pero según tu Entity, DetalleVenta tiene FK a id_item_serial. Como es un modelo agrupado en DTO (List<String>),
                    // dejamos el DetalleVenta.idItemSerial null y registramos la salida física exacta en el Ledger contable.
                    registrarSalidaLedger(dto, detDto.getIdProducto(), 1, item.getIdItemSerial(), idUsuarioReal);
                }
            } else {
                // Registrar salida a granel en el Ledger
                registrarSalidaLedger(dto, detDto.getIdProducto(), detDto.getCantidad(), null, idUsuarioReal);
            }
        }
    }

    private void registrarSalidaLedger(VentaRequestDTO dto, Long idProducto, Integer cantidad, Long idItemSerial, Long idUsuarioReal) {
        String nombreCliente = clienteRepository.obtenerNombreAplanadoPorId(dto.getIdCliente());

        MovimientoInventarioRequestDTO mov = new MovimientoInventarioRequestDTO();
        mov.setTipoMovimiento(TipoMovimiento.SALIDA);
        // --- FIX DE SEGURIDAD: Usamos el ID del servidor, no del DTO ---
        mov.setIdUsuario(idUsuarioReal);
        mov.setIdProducto(idProducto);
        mov.setCantidad(cantidad);
        mov.setIdLocalOrigen(dto.getIdLocal()); // Origen porque es una Salida
        mov.setIdItemSerial(idItemSerial);
        mov.setObservacion(String.format("VENTA DIRECTA. Fac: %s-%s. Cliente: %s",
                dto.getSerieComprobante(), dto.getNumeroComprobante(), nombreCliente.trim()));

        movimientoInventarioService.registrarMovimiento(mov);
    }
}