package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.UnidadMedida;
import java.util.List;

public interface UnidadMedidaService {
    List<UnidadMedida> listarTodos();
    UnidadMedida buscarPorId(Long id);
    UnidadMedida crear(UnidadMedida unidadMedida);
    UnidadMedida actualizar(Long id, UnidadMedida unidadMedida);
    void eliminar(Long id);
}