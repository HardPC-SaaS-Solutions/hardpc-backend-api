package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.dto.UsuarioRequestDTO;
import com.hardpc.saas.backendapi.dto.UsuarioResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioService {
    Page<UsuarioResponseDTO> listarPaginado(String buscar, Long idRol, Pageable pageable);
    UsuarioResponseDTO buscarPorId(Long id);
    UsuarioResponseDTO crear(UsuarioRequestDTO dto);
    UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO dto);
    void eliminarLogico(Long id);
    // Plus arquitectónico: reactivar usuarios dados de baja
    void reactivar(Long id);
}