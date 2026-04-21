package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.Marca;
import com.hardpc.saas.backendapi.service.MarcaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/marcas")
@RequiredArgsConstructor
public class MarcaRestController {

    private final MarcaService marcaService;

    @GetMapping
    public ResponseEntity<List<Marca>> listar() {
        return ResponseEntity.ok(marcaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Marca> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(marcaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Marca> crear(@Valid @RequestBody Marca marca) {
        Marca creada = marcaService.crear(marca);
        return ResponseEntity.created(URI.create("/api/marcas/" + creada.getIdMarca())).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Marca> actualizar(@PathVariable Long id, @Valid @RequestBody Marca marca) {
        return ResponseEntity.ok(marcaService.actualizar(id, marca));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        marcaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}