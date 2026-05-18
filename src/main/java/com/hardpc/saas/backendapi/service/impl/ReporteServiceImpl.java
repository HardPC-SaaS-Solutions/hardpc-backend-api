package com.hardpc.saas.backendapi.service.impl;


import com.hardpc.saas.backendapi.dto.InversionStockDTO;
import com.hardpc.saas.backendapi.repository.StockLocalRepository;
import com.hardpc.saas.backendapi.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {
    private final StockLocalRepository stockLocalRepository;

    @Override
    public List<InversionStockDTO> obtenerInversionPorLocal() {
        return stockLocalRepository.obtenerInversionPorLocal();
    }
}
