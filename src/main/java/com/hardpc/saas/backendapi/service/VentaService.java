package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.Venta;
import java.util.List;

public interface VentaService {
    List<Venta> listarTodos();
    Venta buscarPorId(Long id);
    Venta crear(Venta venta);
    Venta actualizar(Long id, Venta venta);
    void eliminar(Long id);
}