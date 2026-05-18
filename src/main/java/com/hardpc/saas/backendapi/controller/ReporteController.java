package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.dto.InversionStockDTO;
import com.hardpc.saas.backendapi.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
    @RequestMapping("/api/reportes")
    @RequiredArgsConstructor
    public class ReporteController {

        private final ReporteService reporteService;

        @GetMapping("/inversion-stock")
        public ResponseEntity<List<InversionStockDTO>> obtenerInversionStock() {

            return ResponseEntity.ok(
                    reporteService.obtenerInversionPorLocal()
            );
        }
    }

