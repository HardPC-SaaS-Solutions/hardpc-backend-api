package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.dto.StockMinimoDTO;
import com.hardpc.saas.backendapi.repository.StockLocalRepository;
import com.hardpc.saas.backendapi.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockLocalRepository stockLocalRepository;
    @Override
    public List<StockMinimoDTO> obtenerAlertasStockMinimo() {
        return stockLocalRepository.obtenerProductosConStockMinimo();
    }
}
