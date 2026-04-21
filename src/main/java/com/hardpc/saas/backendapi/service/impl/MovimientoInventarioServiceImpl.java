package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.entity.MovimientoInventario;
import com.hardpc.saas.backendapi.repository.MovimientoInventarioRepository;
import com.hardpc.saas.backendapi.service.MovimientoInventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MovimientoInventarioServiceImpl implements MovimientoInventarioService {

    private final MovimientoInventarioRepository movimientoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventario> listarTodos() {
        return movimientoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public MovimientoInventario buscarPorId(Long id) {
        return movimientoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movimiento no encontrado: " + id));
    }

    @Override
    public MovimientoInventario crear(MovimientoInventario movimiento) {
        movimiento.setIdMovimiento(null);
        return movimientoRepository.save(movimiento);
    }

    @Override
    public MovimientoInventario actualizar(Long id, MovimientoInventario movimiento) {
        MovimientoInventario existente = buscarPorId(id);
        existente.setTipoMovimiento(movimiento.getTipoMovimiento());
        existente.setFechaHora(movimiento.getFechaHora());
        existente.setCantidad(movimiento.getCantidad());
        existente.setProducto(movimiento.getProducto());
        existente.setItemSerial(movimiento.getItemSerial());
        existente.setLocalOrigen(movimiento.getLocalOrigen());
        existente.setLocalDestino(movimiento.getLocalDestino());
        existente.setUsuario(movimiento.getUsuario());
        existente.setObservacion(movimiento.getObservacion());
        return movimientoRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        MovimientoInventario existente = buscarPorId(id);
        movimientoRepository.delete(existente);
    }
}