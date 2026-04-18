package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.Local;
import com.hardpc.saas.backendapi.repository.LocalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocalService {

    private final LocalRepository repository;

    public LocalService(LocalRepository repository) {
        this.repository = repository;
    }

    public List<Local> listar() {
        return repository.findAll();
    }

    public Local guardar(Local local) {
        return repository.save(local);
    }

    public Local buscar(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}