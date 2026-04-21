package com.hardpc.saas.backendapi.controller;


import com.hardpc.saas.backendapi.entity.DetalleIngreso;
import com.hardpc.saas.backendapi.service.DetalleIngresoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detalles-ingreso")

public class DetallesIngresoController {

    private final DetalleIngresoService service;

    DetallesIngresoController(DetalleIngresoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<DetalleIngreso>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleIngreso> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<DetalleIngreso> guardar(@RequestBody @Valid DetalleIngreso detalle) {
        return ResponseEntity.ok(service.guardarDetalle(detalle));
    }
}

