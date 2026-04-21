package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.entity.Rol;
import com.hardpc.saas.backendapi.repository.RolRepository;
import com.hardpc.saas.backendapi.service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Rol> listarTodos() {
        return rolRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Rol buscarPorId(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado con id: " + id));
    }

    @Override
    public Rol crear(Rol rol) {
        rol.setIdRol(null);
        return rolRepository.save(rol);
    }

    @Override
    public Rol actualizar(Long id, Rol rol) {
        Rol existente = buscarPorId(id);
        existente.setNombre(rol.getNombre());
        existente.setDescripcion(rol.getDescripcion());
        existente.setEstado(rol.getEstado());
        return rolRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        Rol existente = buscarPorId(id);
        rolRepository.delete(existente);
    }
}