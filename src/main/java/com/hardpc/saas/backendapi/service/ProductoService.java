package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.Producto;
import java.util.List;

public interface ProductoService {
    List<Producto> listarTodos();
    Producto buscarPorId(Long id);
    Producto crear(Producto producto);
    Producto actualizar(Long id, Producto producto);
    void eliminar(Long id);
}