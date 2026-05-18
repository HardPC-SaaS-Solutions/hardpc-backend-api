package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.dto.StockMinimoDTO;

import java.util.List;

public interface StockService {

    List<StockMinimoDTO> obtenerAlertasStockMinimo();
}
