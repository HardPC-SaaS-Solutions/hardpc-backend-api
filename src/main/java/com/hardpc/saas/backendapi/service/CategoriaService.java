package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.dto.CategoriaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoriaService {
    Page<CategoriaDTO> listarPaginado(String buscar, Pageable pageable);
    List<CategoriaDTO> listarActivosParaCombo();
    CategoriaDTO buscarPorId(Long id);
    CategoriaDTO crear(CategoriaDTO dto);
    CategoriaDTO actualizar(Long id, CategoriaDTO dto);
    void eliminarLogico(Long id);
}