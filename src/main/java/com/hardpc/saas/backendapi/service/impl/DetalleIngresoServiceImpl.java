package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.entity.DetalleIngreso;
import com.hardpc.saas.backendapi.repository.DetalleIngresoRepository;
import com.hardpc.saas.backendapi.service.DetalleIngresoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DetalleIngresoServiceImpl implements DetalleIngresoService {

    private final DetalleIngresoRepository detalleIngresoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DetalleIngreso> listarTodos() {
        return detalleIngresoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public DetalleIngreso buscarPorId(Long id) {
        return detalleIngresoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Detalle de ingreso no encontrado con id: " + id));
    }

    @Override
    public DetalleIngreso crear(DetalleIngreso detalleIngreso) {
        detalleIngreso.setIdDetalleIngreso(null);
        return detalleIngresoRepository.save(detalleIngreso);
    }

    @Override
    public DetalleIngreso actualizar(Long id, DetalleIngreso detalleIngreso) {
        DetalleIngreso existente = buscarPorId(id);

        existente.setIngresoCompra(detalleIngreso.getIngresoCompra());
        existente.setProducto(detalleIngreso.getProducto());
        existente.setCantidad(detalleIngreso.getCantidad());
        existente.setPrecioCompraUnitario(detalleIngreso.getPrecioCompraUnitario());

        return detalleIngresoRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        DetalleIngreso existente = buscarPorId(id);
        detalleIngresoRepository.delete(existente);
    }
}