package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.dto.InversionStockDTO;

import java.util.List;

public interface ReporteService {

    List<InversionStockDTO> obtenerInversionPorLocal();
}
