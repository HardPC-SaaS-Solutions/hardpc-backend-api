package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.entity.Venta;
import com.hardpc.saas.backendapi.repository.VentaRepository;
import com.hardpc.saas.backendapi.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Venta> listarTodos() {
        return ventaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Venta buscarPorId(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada con id: " + id));
    }

    @Override
    public Venta crear(Venta venta) {
        venta.setIdVenta(null);
        return ventaRepository.save(venta);
    }

    @Override
    public Venta actualizar(Long id, Venta venta) {
        Venta existente = buscarPorId(id);

        existente.setCliente(venta.getCliente());
        existente.setUsuario(venta.getUsuario());
        existente.setTipoComprobante(venta.getTipoComprobante());
        existente.setFormaPago(venta.getFormaPago());
        existente.setLocal(venta.getLocal());
        existente.setSerieComprobante(venta.getSerieComprobante());
        existente.setNumeroComprobante(venta.getNumeroComprobante());
        existente.setFechaVenta(venta.getFechaVenta());
        existente.setImpuesto(venta.getImpuesto());
        existente.setTotalVenta(venta.getTotalVenta());
        existente.setEstadoVenta(venta.getEstadoVenta());

        return ventaRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        Venta existente = buscarPorId(id);
        ventaRepository.delete(existente);
    }
}