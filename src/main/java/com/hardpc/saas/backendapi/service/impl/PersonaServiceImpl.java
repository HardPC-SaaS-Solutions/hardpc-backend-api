package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.entity.Persona;
import com.hardpc.saas.backendapi.repository.PersonaRepository;
import com.hardpc.saas.backendapi.service.PersonaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository personaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Persona> listarTodos() {
        return personaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Persona buscarPorId(Long id) {
        return personaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada con id: " + id));
    }

    @Override
    public void eliminar(Long id) {
        Persona existente = buscarPorId(id);
        personaRepository.delete(existente);
    }
}