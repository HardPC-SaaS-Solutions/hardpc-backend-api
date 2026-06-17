package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.dto.*;
import com.hardpc.saas.backendapi.entity.*;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.hardpc.saas.backendapi.security.CustomUserDetails;

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
    private final StockLocalRepository stockLocalRepository;
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

        // --- FIX DE SEGURIDAD: Extraer el inventarista autenticado del JWT ---
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Usuario usuarioLogueado = userDetails.getUsuario();
        // ---------------------------------------------------------------------

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

        // --- FIX DE SEGURIDAD: Inyectamos el usuario real ---
        entidad.setUsuario(usuarioLogueado);

        // El @AfterMapping del mapper ya conectó los detalles con este padre.
        IngresoCompra compraGuardada = repository.save(entidad);

        // 6. ORQUESTACIÓN A SERVICIOS EXTERNOS (Efectos Colaterales Físicos)
        orquestarEfectosColaterales(dto, compraGuardada, usuarioLogueado.getIdPersona());

        // NOTA ARQUITECTÓNICA: La actualización del 'StockLocal' no se hace aquí directamente para evitar acoplamiento.
        // La tabla 'MovimientoInventario' y la inserción en 'ItemSerial' dispararán el stock real
        // a través de un Listener/Evento asíncrono en una fase posterior.

        return mapper.toResponseDTO(compraGuardada);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IngresoCompraResponseDTO anularIngresoCompra(Long idIngreso) {

        // 1. Identidad del Servidor (¿Quién está anulando la compra?)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long idSupervisor = userDetails.getUsuario().getIdPersona();

        // 2. Validación Inicial de Existencia y Estado
        IngresoCompra ingreso = repository.findById(idIngreso)
                .orElseThrow(() -> new EntityNotFoundException("Ingreso de compra no encontrado con ID: " + idIngreso));

        // Asumiendo que manejas EstadoIngreso o similar. Si usas EstadoVenta adapta el Enum correspondiente.
        if ("ANULADO".equals(ingreso.getEstadoIngreso().toString())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_COMPRA_YA_ANULADA", "Esta compra ya se encuentra anulada.");
        }

        Long idLocal = ingreso.getLocal().getIdLocal();

        // =========================================================================================
        // FASE 1: VALIDACIÓN FÍSICA Y CONTROL DE SALDOS NEGATIVOS (PRE-CHECK)
        // =========================================================================================
        for (DetalleIngreso detalle : ingreso.getDetalles()) {
            Producto producto = detalle.getProducto();

            if (Boolean.TRUE.equals(producto.getEsSerializado())) {
                // Rastrear los seriales exactos que se crearon con esta línea de compra
                List<ItemSerial> serialesComprados = itemSerialRepository.findByDetalleIngreso_IdDetalleIngreso(detalle.getIdDetalleIngreso());

                for (ItemSerial item : serialesComprados) {
                    // Si una sola de las laptops ya se vendió, se procesó por garantía o no está DISPONIBLE, bloqueamos todo
                    if (item.getEstadoDisponibilidad() != EstadoDisponibilidad.DISPONIBLE) {
                        throw new BusinessException(HttpStatus.CONFLICT, "ERR_REVERSO_COMPRA_INVALIDO",
                                String.format("No se puede anular la compra. El producto serializado con serie '%s' ya cambió de estado (Estado actual: %s).",
                                        item.getNumeroSerie(), item.getEstadoDisponibilidad()));
                    }
                }
            } else {
                // Validar si el local tiene stock suficiente para devolver estos productos a granel
                boolean hayStockSuficiente = stockLocalRepository.hasStockSuficiente(producto.getIdProducto(), idLocal, detalle.getCantidad());
                if (!hayStockSuficiente) {
                    throw new BusinessException(HttpStatus.CONFLICT, "ERR_STOCK_INSUFICIENTE_REVERSO",
                            String.format("No se puede anular la compra. El stock actual del producto '%s' en este local es insuficiente para realizar la devolución al proveedor.",
                                    producto.getCodigoSku()));
                }
            }
        }

        // =========================================================================================
        // FASE 2: EJECUCIÓN DEL REVERSO (Aprobado bajo condiciones físicas seguras)
        // =========================================================================================

        // A. Cambiar estado de la cabecera
        ingreso.setEstadoIngreso(EstadoIngreso.ANULADO); // Adapta a tu Enum de estados de compra
        IngresoCompra ingresoAnulado = repository.save(ingreso);

        // B. Orquestar salida de inventario en el Ledger
        for (DetalleIngreso detalle : ingresoAnulado.getDetalles()) {
            Producto producto = detalle.getProducto();

            // Plantilla base para el Ledger (Las anulaciones de compra restan del almacén origen)
            MovimientoInventarioRequestDTO movReverso = new MovimientoInventarioRequestDTO();
            movReverso.setTipoMovimiento(TipoMovimiento.SALIDA); // Reverso de entrada = Salida
            movReverso.setIdUsuario(idSupervisor);
            movReverso.setIdProducto(producto.getIdProducto());
            movReverso.setIdLocalOrigen(idLocal); // Sale de la tienda donde ingresó originalmente
            movReverso.setObservacion(String.format("REVERSO POR ANULACIÓN. Ref: Ingreso %s-%s",
                    ingresoAnulado.getSerieComprobante(), ingresoAnulado.getNumeroComprobante()));

            if (Boolean.TRUE.equals(producto.getEsSerializado())) {
                List<ItemSerial> serialesComprados = itemSerialRepository.findByDetalleIngreso_IdDetalleIngreso(detalle.getIdDetalleIngreso());

                for (ItemSerial item : serialesComprados) {
                    // 1. Deshabilitar el equipo físico del mercado (Inmutabilidad)
                    item.setEstadoDisponibilidad(EstadoDisponibilidad.DEVUELTO_PROVEEDOR);
                    itemSerialRepository.save(item);

                    // 2. Registrar salida individual en Ledger
                    movReverso.setCantidad(1);
                    movReverso.setIdItemSerial(item.getIdItemSerial());
                    movimientoInventarioService.registrarMovimiento(movReverso);
                }
            } else {
                // Registrar salida grupal a granel en Ledger (Baja el stock local automáticamente)
                movReverso.setCantidad(detalle.getCantidad());
                movReverso.setIdItemSerial(null);
                movimientoInventarioService.registrarMovimiento(movReverso);
            }
        }

        return mapper.toResponseDTO(ingresoAnulado);
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

    private void orquestarEfectosColaterales(IngresoCompraRequestDTO dto, IngresoCompra compraGuardada, Long idUsuarioReal) {
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
                    // 2. Pasamos el ID real
                    registrarMovimientoLedger(idUsuarioReal, dto.getIdLocal(), detDto.getIdProducto(), 1, serialCreado.getId(), compraGuardada);
                }
            } else {
                // Flujo B: Productos a Granel -> Genera 0 físicos, y 1 movimiento en bloque en el Ledger
                // Pasamos el ID real
                registrarMovimientoLedger(idUsuarioReal, dto.getIdLocal(), detDto.getIdProducto(), detDto.getCantidad(), null, compraGuardada);
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