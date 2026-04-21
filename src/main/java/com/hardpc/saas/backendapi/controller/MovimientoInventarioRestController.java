package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.MovimientoInventario;
import com.hardpc.saas.backendapi.service.MovimientoInventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/movimientos-inventario")
@RequiredArgsConstructor
public class MovimientoInventarioRestController {

    private final MovimientoInventarioService movimientoService;

    @GetMapping
    public ResponseEntity<List<MovimientoInventario>> listar() {
        return ResponseEntity.ok(movimientoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoInventario> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<MovimientoInventario> crear(@Valid @RequestBody MovimientoInventario movimiento) {
        MovimientoInventario creado = movimientoService.crear(movimiento);
        return ResponseEntity.created(URI.create("/api/movimientos-inventario/" + creado.getIdMovimiento())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovimientoInventario> actualizar(@PathVariable Long id, @Valid @RequestBody MovimientoInventario movimiento) {
        return ResponseEntity.ok(movimientoService.actualizar(id, movimiento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        movimientoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}