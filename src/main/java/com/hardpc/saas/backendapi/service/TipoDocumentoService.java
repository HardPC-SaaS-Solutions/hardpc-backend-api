package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.dto.TipoDocumentoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TipoDocumentoService {
    Page<TipoDocumentoDTO> listarPaginado(String buscar, Pageable pageable);
    List<TipoDocumentoDTO> listarActivosParaCombo();
    TipoDocumentoDTO buscarPorId(Long id);
    TipoDocumentoDTO buscarPorAbreviatura(String abreviatura);
    TipoDocumentoDTO crear(TipoDocumentoDTO dto);
    TipoDocumentoDTO actualizar(Long id, TipoDocumentoDTO dto);
    void eliminarLogico(Long id);
}