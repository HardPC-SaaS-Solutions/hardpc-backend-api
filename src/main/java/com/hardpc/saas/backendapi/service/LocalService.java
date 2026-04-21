package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.Local;
import java.util.List;

public interface LocalService {
    List<Local> listarTodos();
    Local buscarPorId(Long id);
    Local crear(Local local);
    Local actualizar(Long id, Local local);
    void eliminar(Long id);
}