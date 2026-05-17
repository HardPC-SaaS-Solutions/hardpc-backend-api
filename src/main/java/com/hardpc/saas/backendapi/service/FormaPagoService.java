package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.dto.FormaPagoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FormaPagoService {
    Page<FormaPagoDTO> listarPaginado(String buscar, Pageable pageable);
    List<FormaPagoDTO> listarActivosParaCombo();
    FormaPagoDTO buscarPorId(Long id);
    FormaPagoDTO crear(FormaPagoDTO dto);
    FormaPagoDTO actualizar(Long id, FormaPagoDTO dto);
    void eliminarLogico(Long id);
}