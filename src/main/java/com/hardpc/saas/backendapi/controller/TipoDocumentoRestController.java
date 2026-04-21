package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.TipoDocumento;
import com.hardpc.saas.backendapi.service.TipoDocumentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tipos-documento")
@RequiredArgsConstructor
public class TipoDocumentoRestController {

    private final TipoDocumentoService tipoDocumentoService;

    @GetMapping
    public ResponseEntity<List<TipoDocumento>> listar() {
        return ResponseEntity.ok(tipoDocumentoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoDocumento> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tipoDocumentoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<TipoDocumento> crear(@Valid @RequestBody TipoDocumento tipoDocumento) {
        TipoDocumento creado = tipoDocumentoService.crear(tipoDocumento);
        return ResponseEntity.created(URI.create("/api/tipos-documento/" + creado.getIdTipoDocumento())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoDocumento> actualizar(@PathVariable Long id, @Valid @RequestBody TipoDocumento tipoDocumento) {
        return ResponseEntity.ok(tipoDocumentoService.actualizar(id, tipoDocumento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tipoDocumentoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}