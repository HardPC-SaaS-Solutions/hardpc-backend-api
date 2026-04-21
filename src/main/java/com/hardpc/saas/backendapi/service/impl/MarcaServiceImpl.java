package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.entity.Marca;
import com.hardpc.saas.backendapi.repository.MarcaRepository;
import com.hardpc.saas.backendapi.service.MarcaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MarcaServiceImpl implements MarcaService {

    private final MarcaRepository marcaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Marca> listarTodos() {
        return marcaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Marca buscarPorId(Long id) {
        return marcaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Marca no encontrada con id: " + id));
    }

    @Override
    public Marca crear(Marca marca) {
        marca.setIdMarca(null);
        return marcaRepository.save(marca);
    }

    @Override
    public Marca actualizar(Long id, Marca marca) {
        Marca existente = buscarPorId(id);
        existente.setNombre(marca.getNombre());
        existente.setEstado(marca.getEstado());
        existente.setLogoUrl(marca.getLogoUrl());
        return marcaRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        Marca existente = buscarPorId(id);
        marcaRepository.delete(existente);
    }
}