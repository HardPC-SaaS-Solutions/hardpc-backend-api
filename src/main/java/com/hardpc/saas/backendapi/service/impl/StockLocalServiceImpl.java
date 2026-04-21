package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.entity.StockLocal;
import com.hardpc.saas.backendapi.repository.StockLocalRepository;
import com.hardpc.saas.backendapi.service.StockLocalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StockLocalServiceImpl implements StockLocalService {

    private final StockLocalRepository stockLocalRepository;

    @Override
    @Transactional(readOnly = true)
    public List<StockLocal> listarTodos() {
        return stockLocalRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public StockLocal buscarPorId(Long id) {
        return stockLocalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro de stock no encontrado con id: " + id));
    }

    @Override
    public StockLocal crear(StockLocal stockLocal) {
        stockLocal.setIdStockLocal(null);
        return stockLocalRepository.save(stockLocal);
    }

    @Override
    public StockLocal actualizar(Long id, StockLocal stockLocal) {
        StockLocal existente = buscarPorId(id);
        existente.setProducto(stockLocal.getProducto());
        existente.setLocal(stockLocal.getLocal());
        existente.setCantidadActual(stockLocal.getCantidadActual());
        existente.setStockMinimo(stockLocal.getStockMinimo());
        return stockLocalRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        StockLocal existente = buscarPorId(id);
        stockLocalRepository.delete(existente);
    }
}