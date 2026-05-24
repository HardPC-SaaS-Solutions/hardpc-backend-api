package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.dto.UnidadMedidaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UnidadMedidaService {
    Page<UnidadMedidaDTO> listarPaginado(String buscar, Pageable pageable);
    List<UnidadMedidaDTO> listarActivosParaCombo();
    UnidadMedidaDTO buscarPorId(Long id);
    UnidadMedidaDTO buscarPorDescripcion(String descripcion);
    UnidadMedidaDTO buscarPorAbreviatura(String abreviatura);
    UnidadMedidaDTO crear(UnidadMedidaDTO dto);
    UnidadMedidaDTO actualizar(Long id, UnidadMedidaDTO dto);
    void eliminarLogico(Long id);
}