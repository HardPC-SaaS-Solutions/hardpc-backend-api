package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.MovimientoInventario;
import com.hardpc.saas.backendapi.repository.MovimientoInventarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovimientoInventarioService {

    private final MovimientoInventarioRepository repository;

    public MovimientoInventarioService(MovimientoInventarioRepository repository) {
        this.repository = repository;
    }

    public List<MovimientoInventario> listar() {
        return repository.findAll();
    }

    public MovimientoInventario guardar(MovimientoInventario mov) {
        return repository.save(mov);
    }

    public MovimientoInventario buscar(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}