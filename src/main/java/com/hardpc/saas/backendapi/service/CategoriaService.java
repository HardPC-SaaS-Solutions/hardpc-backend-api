package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.Categoria;
import java.util.List;

public interface CategoriaService {
    List<Categoria> listarTodos();
    Categoria buscarPorId(Long id);
    Categoria crear(Categoria categoria);
    Categoria actualizar(Long id, Categoria categoria);
    void eliminar(Long id);
}