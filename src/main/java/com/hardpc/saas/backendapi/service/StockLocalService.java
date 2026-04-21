package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.StockLocal;
import java.util.List;

public interface StockLocalService {
    List<StockLocal> listarTodos();
    StockLocal buscarPorId(Long id);
    StockLocal crear(StockLocal stockLocal);
    StockLocal actualizar(Long id, StockLocal stockLocal);
    void eliminar(Long id);
}