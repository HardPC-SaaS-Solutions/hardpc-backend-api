package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.dto.InversionStockDTO;
import com.hardpc.saas.backendapi.dto.StockLocalRequestDTO;
import com.hardpc.saas.backendapi.dto.StockLocalResponseDTO;
import com.hardpc.saas.backendapi.entity.Producto;
import com.hardpc.saas.backendapi.entity.StockLocal;
import com.hardpc.saas.backendapi.enums.TipoMovimiento;
import com.hardpc.saas.backendapi.exception.custom.BusinessException;
import com.hardpc.saas.backendapi.mapper.StockLocalMapper;
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

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockLocalServiceImpl implements StockLocalService {

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
            }
            else if (tipoMovimiento == TipoMovimiento.SALIDA) { // O AJUSTE_NEGATIVO
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
}