package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.entity.Local;
import com.hardpc.saas.backendapi.repository.LocalRepository;
import com.hardpc.saas.backendapi.service.LocalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LocalServiceImpl implements LocalService {

    private final LocalRepository localRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Local> listarTodos() {
        return localRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Local buscarPorId(Long id) {
        return localRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Local no encontrado con id: " + id));
    }

    @Override
    public Local crear(Local local) {
        local.setIdLocal(null);
        return localRepository.save(local);
    }

    @Override
    public Local actualizar(Long id, Local local) {
        Local existente = buscarPorId(id);
        existente.setNombre(local.getNombre());
        existente.setDireccion(local.getDireccion());
        existente.setTelefono(local.getTelefono());
        existente.setEstado(local.getEstado());
        existente.setFotoPortadaUrl(local.getFotoPortadaUrl());
        return localRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        Local existente = buscarPorId(id);
        localRepository.delete(existente);
    }
}