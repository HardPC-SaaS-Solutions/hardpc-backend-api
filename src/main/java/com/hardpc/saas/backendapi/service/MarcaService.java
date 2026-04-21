package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.Marca;
import java.util.List;

public interface MarcaService {
    List<Marca> listarTodos();
    Marca buscarPorId(Long id);
    Marca crear(Marca marca);
    Marca actualizar(Long id, Marca marca);
    void eliminar(Long id);
}