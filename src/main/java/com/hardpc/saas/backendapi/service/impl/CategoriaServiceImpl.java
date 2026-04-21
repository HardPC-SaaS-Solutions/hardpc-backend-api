package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.entity.Categoria;
import com.hardpc.saas.backendapi.repository.CategoriaRepository;
import com.hardpc.saas.backendapi.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> listarTodos() {
        return categoriaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada con id: " + id));
    }

    @Override
    public Categoria crear(Categoria categoria) {
        categoria.setIdCategoria(null);
        return categoriaRepository.save(categoria);
    }

    @Override
    public Categoria actualizar(Long id, Categoria categoria) {
        Categoria existente = buscarPorId(id);
        existente.setNombre(categoria.getNombre());
        existente.setDescripcion(categoria.getDescripcion());
        existente.setEstado(categoria.getEstado());
        existente.setIconoUrl(categoria.getIconoUrl());
        return categoriaRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        Categoria existente = buscarPorId(id);
        categoriaRepository.delete(existente);
    }
}