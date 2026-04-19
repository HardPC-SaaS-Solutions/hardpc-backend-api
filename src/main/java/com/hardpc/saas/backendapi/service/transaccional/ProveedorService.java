package com.hardpc.saas.backendapi.service.transaccional;

import com.hardpc.saas.backendapi.entity.Proveedor;

import java.util.List;

public interface ProveedorService {

    List<Proveedor> listarProveedores();

    Proveedor buscarPorId(Long id);

    Proveedor guardarProveedor(Proveedor proveedor);

    Proveedor actualizarProveedor(Long id,Proveedor proveedor);

    void deleteById(Long id);

}
