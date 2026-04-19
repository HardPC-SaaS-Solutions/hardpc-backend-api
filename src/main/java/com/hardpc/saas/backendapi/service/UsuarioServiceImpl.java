package com.hardpc.saas.backendapi.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hardpc.saas.backendapi.entity.Usuario;
import com.hardpc.saas.backendapi.repository.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository repo;

    @Override
    public List<Usuario> listar() {
        return repo.findAll();
    }

    @Override
    public Usuario obtener(Long id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        return repo.save(usuario);
    }

    @Override
    public Usuario actualizar(Long id, Usuario usuario) {
    Usuario existente = repo.findById(id).orElse(null);

    if (existente != null) {
        usuario.setIdPersona(id);
        return repo.save(usuario);
    }

    return null;
}

    @Override
    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}