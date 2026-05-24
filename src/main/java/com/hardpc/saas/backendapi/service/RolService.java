package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.dto.RolDTO;
import com.hardpc.saas.backendapi.enums.RolNombre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RolService {
    Page<RolDTO> listarPaginado(String buscar, Pageable pageable);
    List<RolDTO> listarActivosParaCombo();
    RolDTO buscarPorId(Long id);
    RolDTO buscarPorNombre(RolNombre nombre);
    RolDTO crear(RolDTO dto);
    RolDTO actualizar(Long id, RolDTO dto);
    void eliminarLogico(Long id);
}