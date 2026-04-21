package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.DetalleIngreso;

import java.util.List;

public interface DetalleIngresoService {

    List<DetalleIngreso> listar();

    DetalleIngreso buscarPorId(Long id);

    DetalleIngreso guardarDetalle(DetalleIngreso detalleIngreso);


}
