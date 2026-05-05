package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.DetalleIngreso;
import com.hardpc.saas.backendapi.service.DetalleIngresoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/detalles-ingreso")
@RequiredArgsConstructor
public class DetalleIngresoRestController {

    private final DetalleIngresoService detalleIngresoService;

    @GetMapping
    public ResponseEntity<List<DetalleIngreso>> listar() {
        return ResponseEntity.ok(detalleIngresoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleIngreso> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(detalleIngresoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<DetalleIngreso> crear(@Valid @RequestBody DetalleIngreso detalleIngreso) {
        DetalleIngreso creado = detalleIngresoService.crear(detalleIngreso);
        return ResponseEntity.created(URI.create("/api/detalles-ingresos/" + creado.getIdDetalleIngreso())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetalleIngreso> actualizar(@PathVariable Long id, @Valid @RequestBody DetalleIngreso detalleIngreso) {
        return ResponseEntity.ok(detalleIngresoService.actualizar(id, detalleIngreso));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        detalleIngresoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}