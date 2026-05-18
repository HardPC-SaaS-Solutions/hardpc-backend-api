package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.dto.StockMinimoDTO;
import com.hardpc.saas.backendapi.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/alertas")
    public ResponseEntity<List<StockMinimoDTO>> obtenerAlertas() {

        return ResponseEntity.ok(
                stockService.obtenerAlertasStockMinimo()
        );
    }
}
