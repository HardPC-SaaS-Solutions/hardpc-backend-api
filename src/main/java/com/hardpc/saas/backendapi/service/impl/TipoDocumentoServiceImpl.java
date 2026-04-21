package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.entity.TipoDocumento;
import com.hardpc.saas.backendapi.repository.TipoDocumentoRepository;
import com.hardpc.saas.backendapi.service.TipoDocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TipoDocumentoServiceImpl implements TipoDocumentoService {

    private final TipoDocumentoRepository tipoDocumentoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TipoDocumento> listarTodos() {
        return tipoDocumentoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public TipoDocumento buscarPorId(Long id) {
        return tipoDocumentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de documento no encontrado con id: " + id));
    }

    @Override
    public TipoDocumento crear(TipoDocumento tipoDocumento) {
        tipoDocumento.setIdTipoDocumento(null);
        return tipoDocumentoRepository.save(tipoDocumento);
    }

    @Override
    public TipoDocumento actualizar(Long id, TipoDocumento tipoDocumento) {
        TipoDocumento existente = buscarPorId(id);
        existente.setNombre(tipoDocumento.getNombre());
        existente.setLongitudExacta(tipoDocumento.getLongitudExacta());
        existente.setEstado(tipoDocumento.getEstado());
        return tipoDocumentoRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        TipoDocumento existente = buscarPorId(id);
        tipoDocumentoRepository.delete(existente);
    }
}