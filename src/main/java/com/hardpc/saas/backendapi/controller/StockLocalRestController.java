package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.StockLocal;
import com.hardpc.saas.backendapi.service.StockLocalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/stocks-locales")
@RequiredArgsConstructor
public class StockLocalRestController {

    private final StockLocalService stockLocalService;

    @GetMapping
    public ResponseEntity<List<StockLocal>> listar() {
        return ResponseEntity.ok(stockLocalService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockLocal> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(stockLocalService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<StockLocal> crear(@Valid @RequestBody StockLocal stockLocal) {
        StockLocal creado = stockLocalService.crear(stockLocal);
        return ResponseEntity.created(URI.create("/api/stocks-locales/" + creado.getIdStockLocal())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockLocal> actualizar(@PathVariable Long id, @Valid @RequestBody StockLocal stockLocal) {
        return ResponseEntity.ok(stockLocalService.actualizar(id, stockLocal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        stockLocalService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}