package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.DetalleIngreso;
import java.util.List;

public interface DetalleIngresoService {
    List<DetalleIngreso> listarTodos();
    DetalleIngreso buscarPorId(Long id);
    DetalleIngreso crear(DetalleIngreso detalleIngreso);
    DetalleIngreso actualizar(Long id, DetalleIngreso detalleIngreso);
    void eliminar(Long id);
}