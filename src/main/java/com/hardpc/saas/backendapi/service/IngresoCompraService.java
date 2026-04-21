package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.IngresoCompra;
import java.util.List;

public interface IngresoCompraService {
    List<IngresoCompra> listarTodos();
    IngresoCompra buscarPorId(Long id);
    IngresoCompra crear(IngresoCompra ingresoCompra);
    IngresoCompra actualizar(Long id, IngresoCompra ingresoCompra);
    void eliminar(Long id);
}