package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.entity.TipoComprobante;
import com.hardpc.saas.backendapi.repository.TipoComprobanteRepository;
import com.hardpc.saas.backendapi.service.TipoComprobanteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TipoComprobanteServiceImpl implements TipoComprobanteService {

    private final TipoComprobanteRepository tipoComprobanteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TipoComprobante> listarTodos() {
        return tipoComprobanteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public TipoComprobante buscarPorId(Long id) {
        return tipoComprobanteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de comprobante no encontrado con id: " + id));
    }

    @Override
    public TipoComprobante crear(TipoComprobante tipoComprobante) {
        tipoComprobante.setIdTipoComprobante(null);
        return tipoComprobanteRepository.save(tipoComprobante);
    }

    @Override
    public TipoComprobante actualizar(Long id, TipoComprobante tipoComprobante) {
        TipoComprobante existente = buscarPorId(id);
        existente.setDescripcion(tipoComprobante.getDescripcion());
        existente.setCodigoSunat(tipoComprobante.getCodigoSunat());
        existente.setEstado(tipoComprobante.getEstado());
        return tipoComprobanteRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        TipoComprobante existente = buscarPorId(id);
        tipoComprobanteRepository.delete(existente);
    }
}
