package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.entity.FormaPago;
import com.hardpc.saas.backendapi.repository.FormaPagoRepository;
import com.hardpc.saas.backendapi.service.FormaPagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FormaPagoServiceImpl implements FormaPagoService {

    private final FormaPagoRepository formaPagoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<FormaPago> listarTodos() {
        return formaPagoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public FormaPago buscarPorId(Long id) {
        return formaPagoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Forma de pago no encontrada con id: " + id));
    }

    @Override
    public FormaPago crear(FormaPago formaPago) {
        formaPago.setIdFormaPago(null);
        return formaPagoRepository.save(formaPago);
    }

    @Override
    public FormaPago actualizar(Long id, FormaPago formaPago) {
        FormaPago existente = buscarPorId(id);
        existente.setDescripcion(formaPago.getDescripcion());
        existente.setEstado(formaPago.getEstado());
        return formaPagoRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        FormaPago existente = buscarPorId(id);
        formaPagoRepository.delete(existente);
    }
}
