package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.entity.UnidadMedida;
import com.hardpc.saas.backendapi.repository.UnidadMedidaRepository;
import com.hardpc.saas.backendapi.service.UnidadMedidaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UnidadMedidaServiceImpl implements UnidadMedidaService {

    private final UnidadMedidaRepository unidadMedidaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UnidadMedida> listarTodos() {
        return unidadMedidaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public UnidadMedida buscarPorId(Long id) {
        return unidadMedidaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad de medida no encontrada con id: " + id));
    }

    @Override
    public UnidadMedida crear(UnidadMedida unidadMedida) {
        unidadMedida.setIdUnidadMedida(null);
        return unidadMedidaRepository.save(unidadMedida);
    }

    @Override
    public UnidadMedida actualizar(Long id, UnidadMedida unidadMedida) {
        UnidadMedida existente = buscarPorId(id);
        existente.setDescripcion(unidadMedida.getDescripcion());
        existente.setAbreviatura(unidadMedida.getAbreviatura());
        existente.setEstado(unidadMedida.getEstado());
        return unidadMedidaRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        UnidadMedida existente = buscarPorId(id);
        unidadMedidaRepository.delete(existente);
    }
}