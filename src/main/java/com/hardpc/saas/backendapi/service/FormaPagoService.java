package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.FormaPago;
import java.util.List;

public interface FormaPagoService {
    List<FormaPago> listarTodos();
    FormaPago buscarPorId(Long id);
    FormaPago crear(FormaPago formaPago);
    FormaPago actualizar(Long id, FormaPago formaPago);
    void eliminar(Long id);
}