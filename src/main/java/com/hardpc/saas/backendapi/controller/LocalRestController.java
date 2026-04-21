package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.Local;
import com.hardpc.saas.backendapi.service.LocalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/locales")
@RequiredArgsConstructor
public class LocalRestController {

    private final LocalService localService;

    @GetMapping
    public ResponseEntity<List<Local>> listar() {
        return ResponseEntity.ok(localService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Local> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(localService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Local> crear(@Valid @RequestBody Local local) {
        Local creado = localService.crear(local);
        return ResponseEntity.created(URI.create("/api/locales/" + creado.getIdLocal())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Local> actualizar(@PathVariable Long id, @Valid @RequestBody Local local) {
        return ResponseEntity.ok(localService.actualizar(id, local));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        localService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}