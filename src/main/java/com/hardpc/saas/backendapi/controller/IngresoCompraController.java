package com.hardpc.saas.backendapi.controller;


import com.hardpc.saas.backendapi.entity.IngresoCompra;
import com.hardpc.saas.backendapi.service.IngresoCompraService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingresos")
public class IngresoCompraController {

    private final IngresoCompraService ingresoService;

    public IngresoCompraController(IngresoCompraService ingresoService) {
        this.ingresoService = ingresoService;
    }

    @PostMapping
    public ResponseEntity<IngresoCompra> guardar(@Valid @RequestBody IngresoCompra ingreso) {
        return ResponseEntity.ok(ingresoService.guardarCompra(ingreso));
    }

    @GetMapping
    public ResponseEntity<List<IngresoCompra>> listar() {
        return ResponseEntity.ok(ingresoService.listarIngresoCompra());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngresoCompra> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ingresoService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ingresoService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }


}
