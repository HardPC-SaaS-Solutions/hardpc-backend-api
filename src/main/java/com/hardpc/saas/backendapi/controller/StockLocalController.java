package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.StockLocal;
import com.hardpc.saas.backendapi.service.StockLocalService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-local")
public class StockLocalController {

    private final StockLocalService service;

    public StockLocalController(StockLocalService service) {
        this.service = service;
    }

    @GetMapping
    public List<StockLocal> listar() {
        return service.listar();
    }

    @PostMapping
    public StockLocal crear(@RequestBody StockLocal stock) {
        return service.guardar(stock);
    }

    @GetMapping("/{id}")
    public StockLocal buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}