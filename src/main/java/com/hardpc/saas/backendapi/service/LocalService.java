package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.dto.LocalDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LocalService {
    Page<LocalDTO> listarPaginado(String buscar, Pageable pageable);
    List<LocalDTO> listarActivosParaCombo();
    LocalDTO buscarPorId(Long id);
    LocalDTO buscarPorNombre(String nombre);
    LocalDTO crear(LocalDTO dto);
    LocalDTO actualizar(Long id, LocalDTO dto);
    void eliminarLogico(Long id);
}