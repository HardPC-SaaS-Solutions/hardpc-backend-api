package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.entity.IngresoCompra;
import com.hardpc.saas.backendapi.repository.IngresoCompraRepository;
import com.hardpc.saas.backendapi.service.IngresoCompraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class IngresoCompraServiceImpl implements IngresoCompraService {

    private final IngresoCompraRepository ingresoCompraRepository;

    @Override
    @Transactional(readOnly = true)
    public List<IngresoCompra> listarTodos() {
        return ingresoCompraRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public IngresoCompra buscarPorId(Long id) {
        return ingresoCompraRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingreso de compra no encontrado con id: " + id));
    }

    @Override
    public IngresoCompra crear(IngresoCompra ingresoCompra) {
        ingresoCompra.setIdIngreso(null);
        return ingresoCompraRepository.save(ingresoCompra);
    }

    @Override
    public IngresoCompra actualizar(Long id, IngresoCompra ingresoCompra) {
        IngresoCompra existente = buscarPorId(id);

        existente.setProveedor(ingresoCompra.getProveedor());
        existente.setTipoComprobante(ingresoCompra.getTipoComprobante());
        existente.setUsuario(ingresoCompra.getUsuario());
        existente.setLocal(ingresoCompra.getLocal());
        existente.setSerieComprobante(ingresoCompra.getSerieComprobante());
        existente.setNumeroComprobante(ingresoCompra.getNumeroComprobante());
        existente.setFechaIngreso(ingresoCompra.getFechaIngreso());
        existente.setImpuesto(ingresoCompra.getImpuesto());
        existente.setTotalCompra(ingresoCompra.getTotalCompra());
        existente.setEstadoIngreso(ingresoCompra.getEstadoIngreso());
        existente.setComprobanteDocUrl(ingresoCompra.getComprobanteDocUrl());

        return ingresoCompraRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        IngresoCompra existente = buscarPorId(id);
        ingresoCompraRepository.delete(existente);
    }
}