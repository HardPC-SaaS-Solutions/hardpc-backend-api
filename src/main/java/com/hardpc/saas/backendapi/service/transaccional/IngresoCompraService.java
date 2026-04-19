package com.hardpc.saas.backendapi.service.transaccional;

import com.hardpc.saas.backendapi.entity.IngresoCompra;

import java.util.List;

public interface IngresoCompraService {

    List<IngresoCompra> listarIngresoCompra();

    IngresoCompra buscarPorId(Long id);

    IngresoCompra guardarCompra(IngresoCompra ingreso);

    void eliminarPorId(Long id);

}
