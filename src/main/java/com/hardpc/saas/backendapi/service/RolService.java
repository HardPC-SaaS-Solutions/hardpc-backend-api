package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.Rol;
import java.util.List;

public interface RolService {
    List<Rol> listarTodos();
    Rol buscarPorId(Long id);
    Rol crear(Rol rol);
    Rol actualizar(Long id, Rol rol);
    void eliminar(Long id);
}