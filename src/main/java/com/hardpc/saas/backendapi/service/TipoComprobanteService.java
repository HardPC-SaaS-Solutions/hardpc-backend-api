package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.dto.TipoComprobanteDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TipoComprobanteService {
    Page<TipoComprobanteDTO> listarPaginado(String buscar, Pageable pageable);
    List<TipoComprobanteDTO> listarActivosParaCombo();
    TipoComprobanteDTO buscarPorId(Long id);
    TipoComprobanteDTO buscarPorCodigoSunat(String codigoSunat); // Búsqueda Exacta
    TipoComprobanteDTO crear(TipoComprobanteDTO dto);
    TipoComprobanteDTO actualizar(Long id, TipoComprobanteDTO dto);
    void eliminarLogico(Long id);
}