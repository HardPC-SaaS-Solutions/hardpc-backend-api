package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.TipoComprobante;

import java.util.List;

public interface TipoComprobanteService {
    List<TipoComprobante> listarTodos();
    TipoComprobante buscarPorId(Long id);
    TipoComprobante crear(TipoComprobante tipoComprobante);
    TipoComprobante actualizar(Long id, TipoComprobante tipoComprobante);
    void eliminar(Long id);
}
