package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.Producto;
import com.hardpc.saas.backendapi.repository.ProductoRepository;


import java.util.List;

public interface ProductoService {

    Producto guardar(Producto producto);

    List<Producto> listar();

    Producto buscarPorId(Long id);

    Producto actualizar(Long id, Producto producto);

    void eliminar(Long id);
}
