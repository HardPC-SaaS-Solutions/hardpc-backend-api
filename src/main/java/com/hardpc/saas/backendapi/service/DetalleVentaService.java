package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.DetalleVenta;
import java.util.List;

public interface DetalleVentaService {
    List<DetalleVenta> listarTodos();
    DetalleVenta buscarPorId(Long id);
    DetalleVenta crear(DetalleVenta detalleVenta);
    DetalleVenta actualizar(Long id, DetalleVenta detalleVenta);
    void eliminar(Long id);
}