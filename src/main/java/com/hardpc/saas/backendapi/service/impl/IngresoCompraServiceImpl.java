package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.dto.*;
import com.hardpc.saas.backendapi.entity.DetalleIngreso;
import com.hardpc.saas.backendapi.entity.IngresoCompra;
import com.hardpc.saas.backendapi.entity.Producto;
import com.hardpc.saas.backendapi.enums.Condicion;
import com.hardpc.saas.backendapi.enums.EstadoDisponibilidad;
import com.hardpc.saas.backendapi.enums.EstadoIngreso;
import com.hardpc.saas.backendapi.enums.TipoMovimiento;
import com.hardpc.saas.backendapi.exception.custom.BusinessException;
import com.hardpc.saas.backendapi.mapper.IngresoCompraMapper;
import com.hardpc.saas.backendapi.repository.*;
import com.hardpc.saas.backendapi.service.IngresoCompraService;
import com.hardpc.saas.backendapi.service.ItemSerialService;
import com.hardpc.saas.backendapi.service.MovimientoInventarioService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IngresoCompraServiceImpl implements IngresoCompraService {

    private final IngresoCompraRepository repository;
    private final ProveedorRepository proveedorRepository;
    private final TipoComprobanteRepository tipoComprobanteRepository;
    private final LocalRepository localRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final ItemSerialRepository itemSerialRepository; // Solo para validar existencia de seriales

    // --- Servicios Orquestados ---
    private final ItemSerialService itemSerialService;
    private final MovimientoInventarioService movimientoInventarioService;

    private final IngresoCompraMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public IngresoCompraResponseDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Ingreso de compra no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IngresoCompraResponseDTO> listarPaginadoAvanzado(LocalDateTime inicio, LocalDateTime fin, Long idProveedor, Long idLocal, Pageable pageable) {
        // --- FIX: Barrera de nulos eliminada.
        // Delegamos la flexibilidad al JPQL (>= y <=) del repositorio.
        return repository.buscarPaginadoAvanzado(inicio, fin, idProveedor, idLocal, pageable)
                .map(mapper::toResponseDTO);
    }

    // --- BLOQUE 1: EL ORQUESTADOR TRANSACCIONAL CORE ---
    @Override
    @Transactional(rollbackFor = Exception.class)
    public IngresoCompraResponseDTO registrarCompra(IngresoCompraRequestDTO dto) {

        // 1. Validaciones Maestras de Existencia
        validarMaestras(dto);

        // 2. Validación de Unicidad Documental (Evitar registrar la factura 2 veces)
        validarDocumentoUnico(dto);

        // 3. Regla Matemática Financiera (Redondeo SEGURO y Preciso)
        validarTotales(dto);

        // 4. Reglas Estrictas de Serialización
        validarReglasSerializacion(dto);

        // 5. Mapeo y Persistencia de la Cabecera + Detalles (Cascade)
        IngresoCompra entidad = mapper.toEntity(dto);
        entidad.setIdIngreso(null);
        entidad.setEstadoIngreso(EstadoIngreso.REGISTRADO);

        // El @AfterMapping del mapper ya conectó los detalles con este padre.
        IngresoCompra compraGuardada = repository.save(entidad);

        // 6. ORQUESTACIÓN A SERVICIOS EXTERNOS (Efectos Colaterales Físicos)
        orquestarEfectosColaterales(dto, compraGuardada);

        // NOTA ARQUITECTÓNICA: La actualización del 'StockLocal' no se hace aquí directamente para evitar acoplamiento.
        // La tabla 'MovimientoInventario' y la inserción en 'ItemSerial' dispararán el stock real
        // a través de un Listener/Evento asíncrono en una fase posterior.

        return mapper.toResponseDTO(compraGuardada);
    }

    // --- BLOQUE 2: REPORTES DE INTELIGENCIA DE NEGOCIO (BI) ---
    @Override
    @Transactional(readOnly = true)
    public List<GastoMensualDTO> obtenerReporteGastoMensual() {
        return repository.obtenerGastoMensual();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GastoProveedorDTO> obtenerReporteGastoPorProveedor() {
        return repository.obtenerGastoPorProveedor();
    }


    // =========================================================================================
    // --- LÓGICAS PRIVADAS DE ORQUESTACIÓN Y VALIDACIÓN (CLEAN CODE) ---
    // =========================================================================================

    private void validarMaestras(IngresoCompraRequestDTO dto) {
        if (!proveedorRepository.existsById(dto.getIdProveedor())) throw new EntityNotFoundException("El proveedor especificado no existe.");
        if (!tipoComprobanteRepository.existsById(dto.getIdTipoComprobante())) throw new EntityNotFoundException("El tipo de comprobante no existe.");
        if (!localRepository.existsById(dto.getIdLocal())) throw new EntityNotFoundException("El local destino no existe.");
        if (!usuarioRepository.existsById(dto.getIdUsuario())) throw new EntityNotFoundException("El usuario responsable no existe.");
    }

    private void validarDocumentoUnico(IngresoCompraRequestDTO dto) {
        boolean existe = repository.existsByProveedor_IdProveedorAndTipoComprobante_IdTipoComprobanteAndSerieComprobanteAndNumeroComprobante(
                dto.getIdProveedor(), dto.getIdTipoComprobante(), dto.getSerieComprobante(), dto.getNumeroComprobante());
        if (existe) {
            throw new BusinessException(HttpStatus.CONFLICT, "ERR_COMPRA_DUPLICADA", "Este comprobante ya fue registrado anteriormente para este proveedor.");
        }
    }

    private void validarTotales(IngresoCompraRequestDTO dto) {
        BigDecimal subtotalCalculado = BigDecimal.ZERO;

        for (IngresoCompraRequestDTO.DetalleRequestDTO det : dto.getDetalles()) {
            BigDecimal cantidad = BigDecimal.valueOf(det.getCantidad());
            BigDecimal totalLinea = cantidad.multiply(det.getPrecioCompraUnitario());
            subtotalCalculado = subtotalCalculado.add(totalLinea);
        }

        // 1. Validación Lógica Básica: A + B = C
        BigDecimal totalCalculado = subtotalCalculado.add(dto.getImpuesto()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalEnviado = dto.getTotalCompra().setScale(2, RoundingMode.HALF_UP);

        if (totalCalculado.compareTo(totalEnviado) != 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_TOTAL_COMPRA_MISMATCH",
                    "Error financiero: El subtotal (" + subtotalCalculado + ") + impuesto (" + dto.getImpuesto() +
                            ") suma " + totalCalculado + ", pero el total enviado es " + totalEnviado);
        }

        // 2. Validación Tributaria Estricta (IGV 18% en Perú) con Tolerancia
        // Calculamos cuánto DEBERÍA ser el impuesto ideal
        BigDecimal impuestoEsperado = subtotalCalculado.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal impuestoEnviado = dto.getImpuesto().setScale(2, RoundingMode.HALF_UP);

        // Permitimos una diferencia de máximo 0.15 céntimos (por redondeos del sistema del proveedor)
        BigDecimal diferencia = impuestoEsperado.subtract(impuestoEnviado).abs();
        BigDecimal tolerancia = new BigDecimal("0.15");

        if (diferencia.compareTo(tolerancia) > 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_IMPUESTO_INVALIDO",
                    "Alerta de Auditoría: El impuesto enviado (" + impuestoEnviado +
                            ") no corresponde al 18% del subtotal de la compra (" + subtotalCalculado +
                            "). El impuesto esperado es aprox. " + impuestoEsperado);
        }
    }

    private void validarReglasSerializacion(IngresoCompraRequestDTO dto) {
        for (IngresoCompraRequestDTO.DetalleRequestDTO det : dto.getDetalles()) {
            Producto producto = productoRepository.findById(det.getIdProducto())
                    .orElseThrow(() -> new EntityNotFoundException("El producto con ID " + det.getIdProducto() + " no existe."));

            if (Boolean.TRUE.equals(producto.getEsSerializado())) {
                if (det.getNumerosSerie() == null || det.getNumerosSerie().isEmpty()) {
                    throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_SERIALES_FALTANTES", "El producto '" + producto.getCodigoSku() + "' es serializado. Debe enviar la lista de números de serie.");
                }
                if (det.getNumerosSerie().size() != det.getCantidad()) {
                    throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_SERIALES_MISMATCH", "La cantidad de producto (" + det.getCantidad() + ") no coincide con la cantidad de números de serie enviados (" + det.getNumerosSerie().size() + ").");
                }
                // Validar que no estemos ingresando un equipo (ej. Laptop robada/repetida) que ya existe físicamente en DB
                for (String serial : det.getNumerosSerie()) {
                    if (itemSerialRepository.existsByProducto_IdProductoAndNumeroSerieIgnoreCase(producto.getIdProducto(), serial)) {
                        throw new BusinessException(HttpStatus.CONFLICT, "ERR_SERIAL_DUPLICADO", "El número de serie '" + serial + "' ya se encuentra registrado en el sistema para este producto.");
                    }
                }
            } else {
                if (det.getNumerosSerie() != null && !det.getNumerosSerie().isEmpty()) {
                    throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_SERIALES_PROHIBIDOS", "El producto '" + producto.getCodigoSku() + "' NO es serializado. No debe enviar lista de números de serie.");
                }
            }
        }
    }

    private void orquestarEfectosColaterales(IngresoCompraRequestDTO dto, IngresoCompra compraGuardada) {
        // Necesitamos empatar los DTOs de Detalle con las entidades guardadas para obtener los IDs reales (idDetalleIngreso)
        for (int i = 0; i < dto.getDetalles().size(); i++) {
            IngresoCompraRequestDTO.DetalleRequestDTO detDto = dto.getDetalles().get(i);
            DetalleIngreso detGuardado = compraGuardada.getDetalles().get(i); // Mantienen el mismo orden gracias a la List

            Producto producto = productoRepository.findById(detDto.getIdProducto()).get(); // Ya validado

            if (Boolean.TRUE.equals(producto.getEsSerializado())) {
                // Flujo A: Productos Serializados -> Genera N físicos, y N movimientos en el Ledger
                for (String serialStr : detDto.getNumerosSerie()) {
                    // 1. Crear Físico
                    ItemSerialResponseDTO serialCreado = crearItemSerialFisico(detDto.getIdProducto(), dto.getIdLocal(), serialStr, detGuardado.getIdDetalleIngreso());
                    // 2. Crear Ledger Unitario
                    registrarMovimientoLedger(dto.getIdUsuario(), dto.getIdLocal(), detDto.getIdProducto(), 1, serialCreado.getId(), compraGuardada);
                }
            } else {
                // Flujo B: Productos a Granel -> Genera 0 físicos, y 1 movimiento en bloque en el Ledger
                registrarMovimientoLedger(dto.getIdUsuario(), dto.getIdLocal(), detDto.getIdProducto(), detDto.getCantidad(), null, compraGuardada);
            }
        }
    }

    // --- Sub-rutinas de Delegación ---

    private ItemSerialResponseDTO crearItemSerialFisico(Long idProducto, Long idLocal, String numeroSerie, Long idDetalleIngreso) {
        ItemSerialRequestDTO serialReq = new ItemSerialRequestDTO();
        serialReq.setIdProducto(idProducto);
        serialReq.setIdLocal(idLocal);
        serialReq.setNumeroSerie(numeroSerie);
        serialReq.setCondicion(Condicion.NUEVO);
        serialReq.setEstadoDisponibilidad(EstadoDisponibilidad.DISPONIBLE);
        serialReq.setIdDetalleIngreso(idDetalleIngreso); // Trazabilidad estricta

        return itemSerialService.crear(serialReq);
    }

    private void registrarMovimientoLedger(Long idUsuario, Long idLocalDestino, Long idProducto, Integer cantidad, Long idItemSerial, IngresoCompra compra) {
        String razonSocial = proveedorRepository.obtenerRazonSocialPorId(compra.getProveedor().getIdProveedor());

        MovimientoInventarioRequestDTO movReq = new MovimientoInventarioRequestDTO();
        movReq.setTipoMovimiento(TipoMovimiento.ENTRADA);
        movReq.setIdUsuario(idUsuario);
        movReq.setIdLocalDestino(idLocalDestino);
        movReq.setIdProducto(idProducto);
        movReq.setCantidad(cantidad);
        movReq.setIdItemSerial(idItemSerial); // Si es null, el Ledger sabrá que es a granel
        movReq.setObservacion(String.format("ENTRADA AUTOMÁTICA por Compra. Factura: %s-%s. Proveedor: %s",
                compra.getSerieComprobante(), compra.getNumeroComprobante(), razonSocial));

        movimientoInventarioService.registrarMovimiento(movReq);
    }
}