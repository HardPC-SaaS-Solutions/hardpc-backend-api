package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.UnidadMedida;
import com.hardpc.saas.backendapi.service.UnidadMedidaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/unidades-medida")
@RequiredArgsConstructor
public class UnidadMedidaRestController {

    private final UnidadMedidaService unidadMedidaService;

    @GetMapping
    public ResponseEntity<List<UnidadMedida>> listar() {
        return ResponseEntity.ok(unidadMedidaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnidadMedida> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(unidadMedidaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<UnidadMedida> crear(@Valid @RequestBody UnidadMedida unidadMedida) {
        UnidadMedida creada = unidadMedidaService.crear(unidadMedida);
        return ResponseEntity.created(URI.create("/api/unidades-medida/" + creada.getIdUnidadMedida())).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnidadMedida> actualizar(@PathVariable Long id, @Valid @RequestBody UnidadMedida unidadMedida) {
        return ResponseEntity.ok(unidadMedidaService.actualizar(id, unidadMedida));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        unidadMedidaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}