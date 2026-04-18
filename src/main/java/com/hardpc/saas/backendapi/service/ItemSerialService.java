package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.ItemSerial;
import com.hardpc.saas.backendapi.repository.ItemSerialRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemSerialService {

    private final ItemSerialRepository repository;

    public ItemSerialService(ItemSerialRepository repository) {
        this.repository = repository;
    }

    public List<ItemSerial> listar() {
        return repository.findAll();
    }

    public ItemSerial guardar(ItemSerial item) {
        return repository.save(item);
    }

    public ItemSerial buscar(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}