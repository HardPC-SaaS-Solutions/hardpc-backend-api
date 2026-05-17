package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.dto.MarcaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MarcaService {
    Page<MarcaDTO> listarPaginado(String buscar, Pageable pageable);
    List<MarcaDTO> listarActivosParaCombo();
    MarcaDTO buscarPorId(Long id);
    MarcaDTO buscarPorNombre(String nombre);
    MarcaDTO crear(MarcaDTO dto);
    MarcaDTO actualizar(Long id, MarcaDTO dto);
    void eliminarLogico(Long id);
}