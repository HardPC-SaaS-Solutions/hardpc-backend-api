package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.StockLocal;
import com.hardpc.saas.backendapi.repository.StockLocalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockLocalService {

    private final StockLocalRepository repository;

    public StockLocalService(StockLocalRepository repository) {
        this.repository = repository;
    }

    public List<StockLocal> listar() {
        return repository.findAll();
    }

    public StockLocal guardar(StockLocal stock) {
        return repository.save(stock);
    }

    public StockLocal buscar(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}