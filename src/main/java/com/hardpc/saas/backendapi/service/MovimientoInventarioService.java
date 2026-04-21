package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.MovimientoInventario;
import java.util.List;

public interface MovimientoInventarioService {
    List<MovimientoInventario> listarTodos();
    MovimientoInventario buscarPorId(Long id);
    MovimientoInventario crear(MovimientoInventario movimiento);
    MovimientoInventario actualizar(Long id, MovimientoInventario movimiento);
    void eliminar(Long id);
}