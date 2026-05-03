package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.IngresoCompra;
import com.hardpc.saas.backendapi.service.IngresoCompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/ingresos-compra")
@RequiredArgsConstructor
public class IngresoCompraRestController {

    private final IngresoCompraService ingresoCompraService;

    @GetMapping
    public ResponseEntity<List<IngresoCompra>> listar() {
        return ResponseEntity.ok(ingresoCompraService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngresoCompra> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ingresoCompraService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<IngresoCompra> crear(@Valid @RequestBody IngresoCompra ingresoCompra) {
        IngresoCompra creado = ingresoCompraService.crear(ingresoCompra);
        return ResponseEntity.created(URI.create("/api/ingresos-compras/" + creado.getIdIngreso())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngresoCompra> actualizar(@PathVariable Long id, @Valid @RequestBody IngresoCompra ingresoCompra) {
        return ResponseEntity.ok(ingresoCompraService.actualizar(id, ingresoCompra));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ingresoCompraService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}