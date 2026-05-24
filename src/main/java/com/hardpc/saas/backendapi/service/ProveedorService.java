package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.dto.ProveedorRequestDTO;
import com.hardpc.saas.backendapi.dto.ProveedorResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProveedorService {
    Page<ProveedorResponseDTO> listarPaginado(String buscar, Pageable pageable);
    ProveedorResponseDTO buscarPorId(Long id);
    ProveedorResponseDTO crear(ProveedorRequestDTO dto);
    ProveedorResponseDTO actualizar(Long id, ProveedorRequestDTO dto);
    void eliminarLogico(Long id);
    void reactivar(Long id);
}