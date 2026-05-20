package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.dto.ProductoRequestDTO;
import com.hardpc.saas.backendapi.dto.ProductoResponseDTO;
import com.hardpc.saas.backendapi.entity.Producto;
import com.hardpc.saas.backendapi.exception.custom.BusinessException;
import com.hardpc.saas.backendapi.mapper.ProductoMapper;
import com.hardpc.saas.backendapi.repository.CategoriaRepository;
import com.hardpc.saas.backendapi.repository.MarcaRepository;
import com.hardpc.saas.backendapi.repository.ProductoRepository;
import com.hardpc.saas.backendapi.repository.UnidadMedidaRepository;
import com.hardpc.saas.backendapi.service.ProductoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository repository;
    private final ProductoMapper mapper;

    // Dependencias Inyectadas para validación proactiva de maestras (Autonomía Arquitectónica)
    private final MarcaRepository marcaRepository;
    private final CategoriaRepository categoriaRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponseDTO> listarPaginado(String buscar, Boolean esSerializado, Pageable pageable) {
        String termino = (buscar == null) ? "" : buscar.trim();
        return repository.buscarPaginado(termino, esSerializado, pageable)
                .map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con el ID: " + id));
    }

    @Override
    @Transactional
    public ProductoResponseDTO crear(ProductoRequestDTO dto) {
        validarUnicidad(dto, null);
        validarRelacionesMaestras(dto); // Verificamos que las maestras enviadas existan

        Producto entidad = mapper.toEntity(dto);
        entidad.setIdProducto(null); // Prevención contra inyección de ID (Mass Assignment)
        entidad.setEstado(true);

        return mapper.toResponseDTO(repository.save(entidad));
    }

    @Override
    @Transactional
    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO dto) {
        Producto existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con el ID: " + id));

        validarUnicidad(dto, id);
        validarRelacionesMaestras(dto);

        // MapStruct vuelca las propiedades no nulas sobre la entidad ya extraída de Hibernate.
        mapper.updateEntity(dto, existente);

        return mapper.toResponseDTO(repository.save(existente));
    }

    @Override
    @Transactional
    public void eliminarLogico(Long id) {
        Producto existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con el ID: " + id));
        existente.setEstado(false);
        repository.save(existente);
    }

    @Override
    @Transactional
    public void reactivar(Long id) {
        Producto existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con el ID: " + id));
        existente.setEstado(true);
        repository.save(existente);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponseDTO> listarActivosPorCategoria(Long idCategoria, Pageable pageable) {
        return repository.findByCategoria_IdCategoriaAndEstadoTrue(idCategoria, pageable)
                .map(mapper::toResponseDTO); // ¡Aquí aplicamos la magia del aplanamiento!
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponseDTO> listarActivosPorMarca(Long idMarca, Pageable pageable) {
        return repository.findByMarca_IdMarcaAndEstadoTrue(idMarca, pageable)
                .map(mapper::toResponseDTO); // Aplicando el aplanamiento
    }

    // --- Validaciones de Negocio ---

    /**
     * Valida la unicidad del SKU (código de barras/inventario).
     */
    private void validarUnicidad(ProductoRequestDTO dto, Long idExistente) {
        boolean skuDuplicado = (idExistente == null)
                ? repository.existsByCodigoSku(dto.getCodigoSku())
                : repository.existsByCodigoSkuAndIdProductoNot(dto.getCodigoSku(), idExistente);

        if (skuDuplicado) {
            throw new BusinessException(HttpStatus.CONFLICT, "ERR_DUPLICATE_SKU", "El código SKU ya está registrado en otro producto.");
        }
    }

    /**
     * Validación Proactiva: Evita que Hibernate lance un DataIntegrityViolationException
     * encolando una petición fallida a la base de datos por un ID foráneo que no existe.
     */
    private void validarRelacionesMaestras(ProductoRequestDTO dto) {
        if (!marcaRepository.existsById(dto.getIdMarca())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_MARCA_NOT_FOUND", "La marca seleccionada no existe.");
        }
        if (!categoriaRepository.existsById(dto.getIdCategoria())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_CATEGORIA_NOT_FOUND", "La categoría seleccionada no existe.");
        }
        if (!unidadMedidaRepository.existsById(dto.getIdUnidadMedida())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_UNIDAD_MEDIDA_NOT_FOUND", "La unidad de medida seleccionada no existe.");
        }
    }
}