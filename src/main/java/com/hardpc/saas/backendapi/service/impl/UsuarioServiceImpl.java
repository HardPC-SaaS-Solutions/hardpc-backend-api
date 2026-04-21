package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.entity.Usuario;
import com.hardpc.saas.backendapi.repository.UsuarioRepository;
import com.hardpc.saas.backendapi.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + id));
    }

    @Override
    public Usuario crear(Usuario usuario) {
        // Aseguramos que el ID sea nulo para que JPA sepa que es una inserción nueva
        usuario.setIdPersona(null);
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario actualizar(Long id, Usuario usuario) {
        Usuario existente = buscarPorId(id);

        // 1. Campos heredados de Persona
        existente.setNumeroDocumento(usuario.getNumeroDocumento());
        existente.setTipoDocumento(usuario.getTipoDocumento());
        existente.setNombres(usuario.getNombres());
        existente.setApellidos(usuario.getApellidos());
        existente.setRazonSocial(usuario.getRazonSocial());
        existente.setTelefono(usuario.getTelefono());
        existente.setEmail(usuario.getEmail());
        existente.setDireccion(usuario.getDireccion());
        existente.setEstado(usuario.getEstado());

        // 2. Campos propios de Usuario
        existente.setUsername(usuario.getUsername());
        existente.setPassword(usuario.getPassword());
        existente.setRol(usuario.getRol());
        existente.setAvatarUrl(usuario.getAvatarUrl()); // <-- Nuevo campo añadido

        return usuarioRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        Usuario existente = buscarPorId(id);
        usuarioRepository.delete(existente);
    }
}