package com.hardpc.saas.backendapi.service;

import java.util.List;
import com.hardpc.saas.backendapi.entity.Usuario;

public interface UsuarioService {

    List<Usuario> listar();

    Usuario obtener(Long id);

    Usuario guardar(Usuario usuario);

    Usuario actualizar(Long id, Usuario usuario);

    void eliminar(Long id);
}