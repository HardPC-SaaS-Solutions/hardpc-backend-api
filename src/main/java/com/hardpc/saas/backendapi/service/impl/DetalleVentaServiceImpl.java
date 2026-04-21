package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.entity.DetalleVenta;
import com.hardpc.saas.backendapi.repository.DetalleVentaRepository;
import com.hardpc.saas.backendapi.service.DetalleVentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DetalleVentaServiceImpl implements DetalleVentaService {

    private final DetalleVentaRepository detalleVentaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DetalleVenta> listarTodos() {
        return detalleVentaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public DetalleVenta buscarPorId(Long id) {
        return detalleVentaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Detalle de venta no encontrado con id: " + id));
    }

    @Override
    public DetalleVenta crear(DetalleVenta detalleVenta) {
        detalleVenta.setIdDetalleVenta(null);
        return detalleVentaRepository.save(detalleVenta);
    }

    @Override
    public DetalleVenta actualizar(Long id, DetalleVenta detalleVenta) {
        DetalleVenta existente = buscarPorId(id);

        existente.setVenta(detalleVenta.getVenta());
        existente.setProducto(detalleVenta.getProducto());
        existente.setItemSerial(detalleVenta.getItemSerial());
        existente.setCantidad(detalleVenta.getCantidad());
        existente.setPrecioVentaUnitario(detalleVenta.getPrecioVentaUnitario());
        existente.setDescuento(detalleVenta.getDescuento());

        return detalleVentaRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        DetalleVenta existente = buscarPorId(id);
        detalleVentaRepository.delete(existente);
    }
}