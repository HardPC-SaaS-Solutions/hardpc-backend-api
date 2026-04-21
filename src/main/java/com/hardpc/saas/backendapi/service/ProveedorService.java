package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.Proveedor;
import java.util.List;

public interface ProveedorService {
    List<Proveedor> listarTodos();
    Proveedor buscarPorId(Long id);
    Proveedor crear(Proveedor proveedor);
    Proveedor actualizar(Long id, Proveedor proveedor);
    void eliminar(Long id);
}