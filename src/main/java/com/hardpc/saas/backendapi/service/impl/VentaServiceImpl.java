package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.dto.*;
import com.hardpc.saas.backendapi.entity.*;
import com.hardpc.saas.backendapi.enums.EstadoCaja;
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

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

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
    private final CajaSesionRepository cajaSesionRepository;

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
    public Page<VentaResponseDTO> listarPaginadoAvanzado(LocalDateTime inicio, LocalDateTime fin, Long idCliente, Long idLocal, EstadoVenta estado, String comprobante, Pageable pageable) {
        return repository.buscarVentasAvanzado(inicio, fin, idCliente, idLocal, estado, comprobante, pageable)
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

        // ✨ NUEVO BLINDAJE DE SEGURIDAD: Verificar Arqueo de Caja
        CajaSesion cajaAbierta = cajaSesionRepository.findByUsuario_IdPersonaAndEstado(cajeroLogueado.getIdPersona(), EstadoCaja.ABIERTA)
                .orElseThrow(() -> new BusinessException(HttpStatus.FORBIDDEN, "ERR_CAJA_CERRADA", "Acceso denegado: Debe aperturar una caja antes de procesar ventas."));

        // ✨ REGLA ARQUITECTÓNICA: La venta debe ocurrir obligatoriamente en el local donde se abrió la caja
        if (!cajaAbierta.getLocal().getIdLocal().equals(dto.getIdLocal())) {
            throw new BusinessException(HttpStatus.CONFLICT, "ERR_CAJA_LOCAL_MISMATCH", "Inconsistencia: Su caja activa pertenece a otro local. Cierre su caja actual primero.");
        }

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

        // --- FIX ARQUITECTÓNICO: Explosión de líneas para Serializados ---
        entidad.getDetalles().clear(); // Borramos el mapeo agrupado por defecto

        for (VentaRequestDTO.DetalleRequestDTO detDto : dto.getDetalles()) {
            Producto producto = productoRepository.findById(detDto.getIdProducto()).get();
            BigDecimal precioReal = producto.getPrecioUsd();

            if (Boolean.TRUE.equals(producto.getEsSerializado())) {
                // Dividimos el descuento equitativamente entre las unidades
                BigDecimal cantDecimal = BigDecimal.valueOf(detDto.getCantidad());
                BigDecimal descUnitario = detDto.getDescuento().divide(cantDecimal, 2, RoundingMode.HALF_UP);

                for (String serial : detDto.getNumerosSerie()) {
                    ItemSerial item = itemSerialRepository.findByNumeroSerieIgnoreCase(serial).get();

                    DetalleVenta nuevoDetalle = new DetalleVenta();
                    nuevoDetalle.setVenta(entidad);
                    nuevoDetalle.setProducto(producto);
                    nuevoDetalle.setCantidad(1); // Viajan de 1 en 1
                    nuevoDetalle.setPrecioVentaUnitario(precioReal);
                    nuevoDetalle.setDescuento(descUnitario);
                    nuevoDetalle.setItemSerial(item); // Enlace directo a la tabla ItemSerial

                    entidad.getDetalles().add(nuevoDetalle);
                }
            } else {
                DetalleVenta nuevoDetalle = new DetalleVenta();
                nuevoDetalle.setVenta(entidad);
                nuevoDetalle.setProducto(producto);
                nuevoDetalle.setCantidad(detDto.getCantidad());
                nuevoDetalle.setPrecioVentaUnitario(precioReal);
                nuevoDetalle.setDescuento(detDto.getDescuento());
                nuevoDetalle.setItemSerial(null);

                entidad.getDetalles().add(nuevoDetalle);
            }
        }

        Venta ventaGuardada = repository.save(entidad);

        // Pasamos los datos directos en lugar del DTO para asegurar coherencia
        orquestarEfectosColaterales(ventaGuardada, cajeroLogueado.getIdPersona(), dto.getIdLocal(), dto.getIdCliente());

        return mapper.toResponseDTO(ventaGuardada);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VentaResponseDTO anularVenta(Long idVenta) {

        // 1. Identidad del Servidor (¿Quién está anulando?)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long idSupervisor = userDetails.getUsuario().getIdPersona();

        // 2. Validaciones Iniciales
        Venta venta = repository.findById(idVenta)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada con ID: " + idVenta));

        if (venta.getEstadoVenta() == EstadoVenta.ANULADA) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_VENTA_YA_ANULADA", "Esta venta ya se encuentra anulada en el sistema.");
        }

        // 3. Cambio de Estado (Inmutabilidad, no borramos)
        venta.setEstadoVenta(EstadoVenta.ANULADA);
        Venta ventaAnulada = repository.save(venta);

        // 4. El Reverso Físico (Orquestación Inversa)
        for (DetalleVenta detalle : ventaAnulada.getDetalles()) {
            Producto producto = detalle.getProducto();

            // Plantilla base para el Ledger
            MovimientoInventarioRequestDTO movReverso = new MovimientoInventarioRequestDTO();
            movReverso.setTipoMovimiento(TipoMovimiento.ENTRADA);
            movReverso.setIdUsuario(idSupervisor);
            movReverso.setIdProducto(producto.getIdProducto());
            movReverso.setIdLocalDestino(ventaAnulada.getLocal().getIdLocal()); // ENTRADA usa destino
            movReverso.setObservacion("REVERSO POR ANULACIÓN. Ref: Fac " +
                    ventaAnulada.getSerieComprobante() + "-" + ventaAnulada.getNumeroComprobante());

            if (Boolean.TRUE.equals(producto.getEsSerializado())) {
                // A. Liberar el equipo físico
                ItemSerial item = detalle.getItemSerial();
                item.setEstadoDisponibilidad(EstadoDisponibilidad.DISPONIBLE);
                itemSerialRepository.save(item);

                // B. Configurar reverso unitario
                movReverso.setCantidad(1);
                movReverso.setIdItemSerial(item.getIdItemSerial());
            } else {
                // A. Configurar reverso a granel
                movReverso.setCantidad(detalle.getCantidad());
                movReverso.setIdItemSerial(null);
            }

            // Disparar el reverso en el Ledger (Esto automáticamente sumará el StockLocal)
            movimientoInventarioService.registrarMovimiento(movReverso);
        }

        return mapper.toResponseDTO(ventaAnulada);
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

    private void orquestarEfectosColaterales(Venta ventaGuardada, Long idUsuarioReal, Long idLocalOrigen, Long idCliente) {

        for (DetalleVenta detGuardado : ventaGuardada.getDetalles()) {
            Producto producto = detGuardado.getProducto();

            if (Boolean.TRUE.equals(producto.getEsSerializado())) {
                // Extraemos el serial directo de la base de datos (ya lo mapeamos arriba)
                ItemSerial item = detGuardado.getItemSerial();
                item.setEstadoDisponibilidad(EstadoDisponibilidad.VENDIDO);
                itemSerialRepository.save(item);

                registrarSalidaLedger(ventaGuardada, producto.getIdProducto(), 1, item.getIdItemSerial(), idUsuarioReal, idLocalOrigen, idCliente);
            } else {
                // Producto a granel
                registrarSalidaLedger(ventaGuardada, producto.getIdProducto(), detGuardado.getCantidad(), null, idUsuarioReal, idLocalOrigen, idCliente);
            }
        }
    }

    private void registrarSalidaLedger(Venta venta, Long idProducto, Integer cantidad, Long idItemSerial, Long idUsuarioReal, Long idLocalOrigen, Long idCliente) {
        String nombreCliente = clienteRepository.obtenerNombreAplanadoPorId(idCliente);

        MovimientoInventarioRequestDTO mov = new MovimientoInventarioRequestDTO();
        mov.setTipoMovimiento(TipoMovimiento.SALIDA);
        mov.setIdUsuario(idUsuarioReal);
        mov.setIdProducto(idProducto);
        mov.setCantidad(cantidad);
        mov.setIdLocalOrigen(idLocalOrigen);
        mov.setIdItemSerial(idItemSerial);
        mov.setObservacion(String.format("VENTA DIRECTA. Fac: %s-%s. Cliente: %s",
                venta.getSerieComprobante(), venta.getNumeroComprobante(), nombreCliente.trim()));

        movimientoInventarioService.registrarMovimiento(mov);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generarTicketPdf(Long idVenta) {
        Venta venta = repository.findById(idVenta)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada con ID: " + idVenta));

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Configurar tamaño de ticket (Ancho de 80mm ~ 226 puntos, Altura dinámica alta)
        Rectangle ticketSize = new Rectangle(226, 800);
        Document document = new Document(ticketSize, 10, 10, 10, 10);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // ── FUENTES ───────────────────────────────────────────────────────────
            Font fontTitulo    = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font fontSubtitulo = new Font(Font.HELVETICA, 9,  Font.NORMAL);
            Font fontDetalle   = new Font(Font.HELVETICA, 8,  Font.NORMAL);
            Font fontBold      = new Font(Font.HELVETICA, 8,  Font.BOLD);
            Font fontTotal     = new Font(Font.HELVETICA, 10, Font.BOLD);

            Paragraph seccionDivisora = new Paragraph("----------------------------------------", fontSubtitulo);
            seccionDivisora.setAlignment(Element.ALIGN_CENTER);

            // ── 1. CABECERA DEL LOCAL ─────────────────────────────────────────────
            Paragraph titulo = new Paragraph("HARDPC SOLUTIONS", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            Paragraph local = new Paragraph("Local: " + venta.getLocal().getNombre(), fontSubtitulo);
            local.setAlignment(Element.ALIGN_CENTER);
            document.add(local);

            // RUC de la empresa (ajusta el valor según tu entidad)
            Paragraph ruc = new Paragraph("RUC: 20123456789", fontSubtitulo);
            ruc.setAlignment(Element.ALIGN_CENTER);
            document.add(ruc);

            document.add(seccionDivisora);

            // ── 2. DATOS DEL COMPROBANTE ──────────────────────────────────────────
            document.add(new Paragraph(venta.getTipoComprobante().getDescripcion().toUpperCase(), fontBold));
            document.add(new Paragraph("Nro: " + venta.getSerieComprobante() + "-" + venta.getNumeroComprobante(), fontDetalle));
            document.add(new Paragraph("Fecha: " + venta.getFechaVenta().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), fontDetalle));
            document.add(new Paragraph("Cajero: " + venta.getUsuario().getNombres() + " " + venta.getUsuario().getApellidos(), fontDetalle));

            // Forma de pago — dato clave para el cliente y auditoría
            document.add(new Paragraph("Pago: " + venta.getFormaPago().getDescripcion(), fontDetalle));

            document.add(seccionDivisora);

            // ── 3. DATOS DEL CLIENTE ──────────────────────────────────────────────
            String docCliente    = venta.getCliente().getNumeroDocumento();
            String nombreCliente = venta.getCliente().getTipoCliente().name().equals("EMPRESA")
                    ? venta.getCliente().getRazonSocial()
                    : venta.getCliente().getNombres() + " " + venta.getCliente().getApellidos();

            document.add(new Paragraph("Cliente: " + nombreCliente, fontDetalle));
            document.add(new Paragraph("Doc: " + docCliente, fontDetalle));

            document.add(seccionDivisora);

            // ── 4. TABLA DE DETALLES ──────────────────────────────────────────────
            // Columnas: Cant. | Descripción | P.Unit | Total Línea
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.0f, 3.2f, 1.4f, 1.4f});

            // Cabecera de columnas
            for (String header : new String[]{"Cant.", "Descripción", "P.Unit", "Total"}) {
                PdfPCell hCell = new PdfPCell(new Phrase(header, fontBold));
                hCell.setBorder(Rectangle.BOTTOM);
                hCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(hCell);
            }

            for (DetalleVenta det : venta.getDetalles()) {

                // Cantidad
                PdfPCell c1 = new PdfPCell(new Paragraph(det.getCantidad() + "x", fontDetalle));
                c1.setBorder(Rectangle.NO_BORDER);
                table.addCell(c1);

                // Descripción + N/S si es serializado
                String desc = det.getProducto().getDescripcion();
                if (det.getItemSerial() != null) {
                    desc += "\nS/N: " + det.getItemSerial().getNumeroSerie();
                }
                PdfPCell c2 = new PdfPCell(new Paragraph(desc, fontDetalle));
                c2.setBorder(Rectangle.NO_BORDER);
                table.addCell(c2);

                // Precio unitario
                PdfPCell c3 = new PdfPCell(new Paragraph("S/ " + det.getPrecioVentaUnitario().setScale(2, java.math.RoundingMode.HALF_UP), fontDetalle));
                c3.setBorder(Rectangle.NO_BORDER);
                c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c3);

                // Subtotal de la línea (cantidad × precio − descuento)
                BigDecimal subFila = det.getPrecioVentaUnitario()
                        .multiply(new BigDecimal(det.getCantidad()))
                        .subtract(det.getDescuento());
                PdfPCell c4 = new PdfPCell(new Paragraph("S/ " + subFila.setScale(2, java.math.RoundingMode.HALF_UP), fontDetalle));
                c4.setBorder(Rectangle.NO_BORDER);
                c4.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c4);
            }

            document.add(table);
            document.add(seccionDivisora);

            // ── 5. BLOQUE DE TOTALES DESGLOSADOS ─────────────────────────────────
            // Requerimiento legal peruano: IGV debe mostrarse por separado en el comprobante.
            BigDecimal totalVenta  = venta.getTotalVenta();
            BigDecimal igv         = venta.getImpuesto();
            BigDecimal subtotalNeto = totalVenta.subtract(igv);

            PdfPTable tablaTotales = new PdfPTable(2);
            tablaTotales.setWidthPercentage(100);

            // Fila Subtotal
            agregarFilaTotales(tablaTotales, "Subtotal:", "S/ " + subtotalNeto.setScale(2, java.math.RoundingMode.HALF_UP), fontDetalle);

            // Fila IGV
            agregarFilaTotales(tablaTotales, "IGV (18%):", "S/ " + igv.setScale(2, java.math.RoundingMode.HALF_UP), fontDetalle);

            // Fila Total — destacado visualmente
            agregarFilaTotales(tablaTotales, "TOTAL A COBRAR:", "S/ " + totalVenta.setScale(2, java.math.RoundingMode.HALF_UP), fontTotal);

            document.add(tablaTotales);

            // ── 6. PIE DE PÁGINA ──────────────────────────────────────────────────
            document.add(seccionDivisora);

            Paragraph gracias = new Paragraph("¡Gracias por su preferencia!", fontSubtitulo);
            gracias.setAlignment(Element.ALIGN_CENTER);
            document.add(gracias);

            // Datos de contacto de la empresa
            Paragraph contacto = new Paragraph("Tel: 044-123456  |  hardpc@email.com", fontDetalle);
            contacto.setAlignment(Element.ALIGN_CENTER);
            document.add(contacto);

            Paragraph web = new Paragraph("www.hardpc.com.pe", fontDetalle);
            web.setAlignment(Element.ALIGN_CENTER);
            document.add(web);

            document.close();

        } catch (Exception e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_PDF_GENERACION", "Error al construir el archivo PDF del ticket.");
        }

        return out.toByteArray();
    }

    /**
     * Inserta una fila de dos celdas sin borde en la tabla de totales.
     * Columna izquierda: etiqueta. Columna derecha: monto alineado a la derecha.
     */
    private void agregarFilaTotales(PdfPTable tabla, String etiqueta, String monto, Font font) {
        PdfPCell celdaEtiqueta = new PdfPCell(new Paragraph(etiqueta, font));
        celdaEtiqueta.setBorder(Rectangle.NO_BORDER);
        tabla.addCell(celdaEtiqueta);

        PdfPCell celdaMonto = new PdfPCell(new Paragraph(monto, font));
        celdaMonto.setBorder(Rectangle.NO_BORDER);
        celdaMonto.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tabla.addCell(celdaMonto);
    }
}