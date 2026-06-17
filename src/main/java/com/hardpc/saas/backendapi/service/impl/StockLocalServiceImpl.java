package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.dto.*;
import com.hardpc.saas.backendapi.entity.ItemSerial;
import com.hardpc.saas.backendapi.entity.Producto;
import com.hardpc.saas.backendapi.entity.StockLocal;
import com.hardpc.saas.backendapi.enums.TipoMovimiento;
import com.hardpc.saas.backendapi.exception.custom.BusinessException;
import com.hardpc.saas.backendapi.mapper.StockLocalMapper;
import com.hardpc.saas.backendapi.repository.ItemSerialRepository;
import com.hardpc.saas.backendapi.repository.LocalRepository;
import com.hardpc.saas.backendapi.repository.ProductoRepository;
import com.hardpc.saas.backendapi.repository.StockLocalRepository;
import com.hardpc.saas.backendapi.service.StockLocalService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockLocalServiceImpl implements StockLocalService {

    private final ItemSerialRepository itemSerialRepository;
    private final StockLocalRepository repository;
    private final LocalRepository localRepository;
    private final ProductoRepository productoRepository;
    private final StockLocalMapper mapper;


    @Override
    @Transactional(readOnly = true)
    public Page<StockLocalResponseDTO> listarPaginado(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public StockLocalResponseDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Registro de stock no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public StockLocalResponseDTO crear(StockLocalRequestDTO dto) {
        validarMaestrasYSerializacion(dto);
        validarUnicidad(dto, null);

        StockLocal entidad = mapper.toEntity(dto);
        entidad.setIdStockLocal(null); // Seguridad contra inyección

        return mapper.toResponseDTO(repository.save(entidad));
    }

    @Override
    @Transactional
    public StockLocalResponseDTO actualizar(Long id, StockLocalRequestDTO dto) {
        StockLocal existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registro de stock no encontrado con ID: " + id));

        validarMaestrasYSerializacion(dto);
        validarUnicidad(dto, id);

        mapper.updateEntity(dto, existente);

        return mapper.toResponseDTO(repository.save(existente));
    }

    // --- ABSORCIONES Y REPORTES ---

    /**
     * Absorción 1: Obtiene todos los productos cuyo stock actual haya caído
     * por debajo o igual al stock mínimo configurado.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<StockLocalResponseDTO> listarAlertasStockMinimo(Pageable pageable) {
        return repository.findAlertasStockMinimo(pageable).map(mapper::toResponseDTO);
    }

    /**
     * Absorción 2: Reporte financiero de cuánto capital está inmovilizado en
     * mercadería por cada local comercial.
     */
    @Override
    @Transactional(readOnly = true)
    public List<InversionStockDTO> obtenerReporteInversion() {
        return repository.obtenerInversionPorLocal();
    }

    /**
     * Absorción 3: Búsqueda granular para el punto de venta (POS) o inventaristas.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<StockLocalResponseDTO> buscarEnLocalPaginado(Long idLocal, String buscar, Pageable pageable) {
        if (!localRepository.existsById(idLocal)) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "ERR_LOCAL_NOT_FOUND", "El local especificado no existe.");
        }

        String termino = (buscar == null) ? "" : buscar.trim();
        return repository.buscarEnLocalPaginado(idLocal, termino, pageable)
                .map(mapper::toResponseDTO);
    }

    @Override
    @Transactional
    public void actualizarStock(Long idProducto, Long idLocal, Integer cantidad, TipoMovimiento tipoMovimiento) {

        // 1. Buscamos el stock bloqueando la fila en la BD para otras transacciones
        Optional<StockLocal> optStock = repository.findByProducto_IdProductoAndLocal_IdLocal(idProducto, idLocal);

        if (optStock.isEmpty()) {
            // Regla: No puedes sacar lo que no existe
            if (tipoMovimiento == TipoMovimiento.SALIDA) { // O AJUSTE_NEGATIVO si lo tuvieras
                throw new BusinessException(HttpStatus.CONFLICT, "ERR_STOCK_INCONSISTENTE",
                        "No se puede realizar una SALIDA. No existe un registro previo de stock para este producto en el local destino.");
            }

            // Si no existe y es ENTRADA: Lo creamos inyectando PROXIES
            StockLocal nuevoStock = new StockLocal();
            // getReferenceById no hace SELECT a la BD, solo crea un cascarón con el ID para la llave foránea
            nuevoStock.setProducto(productoRepository.getReferenceById(idProducto));
            nuevoStock.setLocal(localRepository.getReferenceById(idLocal));
            nuevoStock.setCantidadActual(cantidad);
            nuevoStock.setStockMinimo(0); // Valor inicial por defecto prudente

            repository.save(nuevoStock);

        } else {
            StockLocal stockExistente = optStock.get();

            // Matemática según el tipo de movimiento
            if (tipoMovimiento == TipoMovimiento.ENTRADA) { // O AJUSTE_POSITIVO
                stockExistente.setCantidadActual(stockExistente.getCantidadActual() + cantidad);
            } else if (tipoMovimiento == TipoMovimiento.SALIDA) { // O AJUSTE_NEGATIVO
                if (stockExistente.getCantidadActual() < cantidad) {
                    throw new BusinessException(HttpStatus.CONFLICT, "ERR_STOCK_INSUFICIENTE",
                            "Stock físico insuficiente en el local. Tienes " + stockExistente.getCantidadActual() +
                                    " pero intentas sacar " + cantidad + ".");
                }
                stockExistente.setCantidadActual(stockExistente.getCantidadActual() - cantidad);
            }

            repository.save(stockExistente);
        }
    }

    // --- VALIDACIONES DE NEGOCIO PRIVADAS ---

    /**
     * CRÍTICO: El stock de los productos serializados (Laptops, Gráficas) NO se gestiona aquí.
     * Su stock se calcula haciendo COUNT a la tabla de Items Seriales. Si permitimos que el
     * inventarista edite el StockLocal de una Laptop a "5", pero solo hay 2 seriales en la BBDD,
     * el sistema colapsará en auditoría.
     */
    private void validarMaestrasYSerializacion(StockLocalRequestDTO dto) {
        // 1. Validar existencia de Local
        if (!localRepository.existsById(dto.getIdLocal())) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "ERR_LOCAL_NOT_FOUND", "El local especificado no existe.");
        }

        // 2. Extraer Producto para validar existencia y serialización
        Producto producto = productoRepository.findById(dto.getIdProducto())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "ERR_PRODUCTO_NOT_FOUND", "El producto especificado no existe."));

        if (Boolean.TRUE.equals(producto.getEsSerializado())) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "ERR_PRODUCTO_SERIALIZADO",
                    "El stock de productos serializados no se puede manipular manualmente en Stock Local, debe gestionarse mediante Items Seriales."
            );
        }
    }

    private void validarUnicidad(StockLocalRequestDTO dto, Long idExistente) {
        boolean registroDuplicado = (idExistente == null)
                ? repository.existsByProducto_IdProductoAndLocal_IdLocal(dto.getIdProducto(), dto.getIdLocal())
                : repository.existsByProducto_IdProductoAndLocal_IdLocalAndIdStockLocalNot(dto.getIdProducto(), dto.getIdLocal(), idExistente);

        if (registroDuplicado) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    "ERR_DUPLICATE_STOCK",
                    "Este producto ya tiene un registro de stock en el local seleccionado."
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockLocalDetalleDTO> listarDetallePorLocal(Long idLocal, String buscar) {
        if (!localRepository.existsById(idLocal)) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "ERR_LOCAL_NOT_FOUND", "El local especificado no existe.");
        }

        List<StockLocalDetalleDTO> respuestaDetalle = new ArrayList<>();
        String terminoQuery = (buscar == null) ? "" : buscar.trim().toLowerCase();

        // --- FLUJO 1: PRODUCTOS NO SERIALIZADOS ---
        List<StockLocal> stocksBase = repository.buscarTodoPorLocal(idLocal, terminoQuery);

        for (StockLocal stock : stocksBase) {
            Producto producto = stock.getProducto();

            if (producto == null || Boolean.TRUE.equals(producto.getEsSerializado())) {
                continue;
            }

            StockLocalDetalleDTO dto = new StockLocalDetalleDTO();
            dto.setIdProducto(producto.getIdProducto());
            dto.setCodigoSku(producto.getCodigoSku());
            dto.setDescripcionProducto(producto.getDescripcion());
            dto.setCantidadActual(stock.getCantidadActual());
            dto.setStockMinimo(stock.getStockMinimo());
            dto.setEsSerializado(false);
            dto.setSeriales(new ArrayList<>());

            respuestaDetalle.add(dto);
        }

        // --- FLUJO 2: PRODUCTOS SERIALIZADOS (A PRUEBA DE BALAS) ---
        com.hardpc.saas.backendapi.enums.EstadoDisponibilidad estadoEnum =
                com.hardpc.saas.backendapi.enums.EstadoDisponibilidad.DISPONIBLE;

        // 1. Traemos las entidades de series físicas directo de la BD (2 argumentos)
        List<ItemSerial> todasLasSeriesEntidad =
                itemSerialRepository.findByLocal_IdLocalAndEstadoDisponibilidad(idLocal, estadoEnum);

        // 2. Agrupamos por producto en memoria aplicando el filtro de búsqueda del usuario
        java.util.Map<Producto, List<ItemSerial>> agrupadoPorProducto = todasLasSeriesEntidad.stream()
                .filter(item -> item.getProducto() != null)
                .filter(item -> {
                    if (terminoQuery.isEmpty()) return true;
                    String desc = item.getProducto().getDescripcion() != null ? item.getProducto().getDescripcion().toLowerCase() : "";
                    String sku = item.getProducto().getCodigoSku() != null ? item.getProducto().getCodigoSku().toLowerCase() : "";
                    return desc.contains(terminoQuery) || sku.contains(terminoQuery);
                })
                .collect(java.util.stream.Collectors.groupingBy(ItemSerial::getProducto));

        // 3. Construimos los DTOs finales cortando la recursividad de Hibernate
        for (java.util.Map.Entry<Producto, List<ItemSerial>> entry : agrupadoPorProducto.entrySet()) {
            Producto prod = entry.getKey();
            List<ItemSerial> listaSeries = entry.getValue();

            StockLocalDetalleDTO dto = new StockLocalDetalleDTO();
            dto.setIdProducto(prod.getIdProducto());
            dto.setCodigoSku(prod.getCodigoSku());
            dto.setDescripcionProducto(prod.getDescripcion());
            dto.setCantidadActual(listaSeries.size()); // Conteo de las series físicas disponibles
            dto.setStockMinimo(0);
            dto.setEsSerializado(true);

            // Convertimos la lista pesada de entidades a DTOs limpios para el dropdown de Angular
            List<SerialDisponibleDTO> subSerialesDto = listaSeries.stream()
                    .map(item -> {
                        SerialDisponibleDTO sDto = new SerialDisponibleDTO();
                        sDto.setIdItemSerial(item.getIdItemSerial());
                        sDto.setNumeroSerie(item.getNumeroSerie() != null ? item.getNumeroSerie().trim() : "SIN_SERIE");
                        sDto.setIdProducto(prod.getIdProducto());
                        return sDto;
                    })
                    .toList();

            dto.setSeriales(subSerialesDto);
            respuestaDetalle.add(dto);
        }

        return respuestaDetalle;
    }
}