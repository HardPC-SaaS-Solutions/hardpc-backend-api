package com.hardpc.saas.backendapi.service.transaccional.impl;

import com.hardpc.saas.backendapi.entity.DetalleIngreso;
import com.hardpc.saas.backendapi.entity.IngresoCompra;
import com.hardpc.saas.backendapi.repository.transaccional.DetalleIngresoRepository;
import com.hardpc.saas.backendapi.repository.transaccional.IngresoCompraRepository;
import com.hardpc.saas.backendapi.service.transaccional.DetalleIngresoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetalleIngresoServiceImpl implements DetalleIngresoService {

    private final DetalleIngresoRepository detalleRepository;
    private final IngresoCompraRepository compraRepository;

    public DetalleIngresoServiceImpl(DetalleIngresoRepository detalleRepository, IngresoCompraRepository compraRepository) {
        this.detalleRepository = detalleRepository;
        this.compraRepository = compraRepository;

    }
    @Override
    public List<DetalleIngreso> listar() {
        return detalleRepository.findAll();
    }

    @Override
    public DetalleIngreso buscarPorId(Long id) {
        return detalleRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Detalle Ingreso no encontrado"));
    }

    @Override
    public DetalleIngreso guardarDetalle(DetalleIngreso detalleIngreso) {

        Long idIngreso = detalleIngreso.getIngresoCompra().getIdIngreso();

        IngresoCompra ingreso = compraRepository.findById(idIngreso)
                .orElseThrow(() -> new RuntimeException("Ingreso no encontrado"));

        detalleIngreso.setIngresoCompra(ingreso);

        return detalleRepository.save(detalleIngreso);
    }
}
