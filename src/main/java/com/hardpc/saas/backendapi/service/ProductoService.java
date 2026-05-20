package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.dto.ProductoRequestDTO;
import com.hardpc.saas.backendapi.dto.ProductoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductoService {
    Page<ProductoResponseDTO> listarPaginado(String buscar, Boolean esSerializado, Pageable pageable);
    ProductoResponseDTO buscarPorId(Long id);
    ProductoResponseDTO crear(ProductoRequestDTO dto);
    ProductoResponseDTO actualizar(Long id, ProductoRequestDTO dto);
    void eliminarLogico(Long id);
    void reactivar(Long id);

    // Autonomía Arquitectónica: Listados para Módulo POS (Solo activos)
    Page<ProductoResponseDTO> listarActivosPorCategoria(Long idCategoria, Pageable pageable);
    Page<ProductoResponseDTO> listarActivosPorMarca(Long idMarca, Pageable pageable);
}