package com.hardpc.saas.backendapi.service.transaccional.impl;

import com.hardpc.saas.backendapi.entity.TipoDocumento;
import com.hardpc.saas.backendapi.repository.transaccional.TipoDocumentoRepository;
import com.hardpc.saas.backendapi.service.transaccional.TipoDocumentoService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TipoDocumentoServiceImpl implements TipoDocumentoService {

    private final TipoDocumentoRepository repository;

    public TipoDocumentoServiceImpl(TipoDocumentoRepository repository) {
        this.repository = repository;
    }

    @Override
    public TipoDocumento guardarTipo(TipoDocumento tipoDocumento) {
        return repository.save(tipoDocumento);
    }

    @Override
    public List<TipoDocumento> listarTipoDocumentos() {
        return repository.findAll();
    }

    @Override
    public TipoDocumento buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(()->new RuntimeException("TipoDocumento no encontrado"));
    }

    @Override
    public TipoDocumento actualizarTipoDocumento(Long id, TipoDocumento tipoDocumento) {

        TipoDocumento existente = buscarPorId(id);

        existente.setNombre(tipoDocumento.getNombre());
        existente.setLongitudExacta(tipoDocumento.getLongitudExacta());
        existente.setEstado(tipoDocumento.getEstado());

        return repository.save(existente);
    }

    @Override
    public void eliminarTipoDocumento(Long id) {

        TipoDocumento tipo = buscarPorId(id);
        repository.delete(tipo);

    }
}
