package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.DetalleVenta;
import com.hardpc.saas.backendapi.service.DetalleVentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/detalles-venta")
@RequiredArgsConstructor
public class DetalleVentaRestController {

    private final DetalleVentaService detalleVentaService;

    @GetMapping
    public ResponseEntity<List<DetalleVenta>> listar() {
        return ResponseEntity.ok(detalleVentaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleVenta> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(detalleVentaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<DetalleVenta> crear(@Valid @RequestBody DetalleVenta detalleVenta) {
        DetalleVenta creado = detalleVentaService.crear(detalleVenta);
        return ResponseEntity.created(URI.create("/api/detalles-ventas/" + creado.getIdDetalleVenta())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetalleVenta> actualizar(@PathVariable Long id, @Valid @RequestBody DetalleVenta detalleVenta) {
        return ResponseEntity.ok(detalleVentaService.actualizar(id, detalleVenta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        detalleVentaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}