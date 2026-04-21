package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.FormaPago;
import com.hardpc.saas.backendapi.service.FormaPagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/formas-pago")
@RequiredArgsConstructor
public class FormaPagoRestController {

    private final FormaPagoService formaPagoService;

    @GetMapping
    public ResponseEntity<List<FormaPago>> listar() {
        return ResponseEntity.ok(formaPagoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormaPago> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(formaPagoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<FormaPago> crear(@Valid @RequestBody FormaPago formaPago) {
        FormaPago creado = formaPagoService.crear(formaPago);
        return ResponseEntity.created(URI.create("/api/formas-pago/" + creado.getIdFormaPago())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FormaPago> actualizar(@PathVariable Long id, @Valid @RequestBody FormaPago formaPago) {
        return ResponseEntity.ok(formaPagoService.actualizar(id, formaPago));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        formaPagoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}