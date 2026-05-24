package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.dto.LocalDTO;
import com.hardpc.saas.backendapi.service.LocalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locales")
@RequiredArgsConstructor
public class LocalRestController {

    private final LocalService service;

    @GetMapping
    public ResponseEntity<Page<LocalDTO>> listarPaginado(
            @RequestParam(required = false, defaultValue = "") String buscar,
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(service.listarPaginado(buscar, pageable));
    }

    @GetMapping("/combo")
    public ResponseEntity<List<LocalDTO>> listarParaCombo() {
        return ResponseEntity.ok(service.listarActivosParaCombo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocalDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<LocalDTO> crear(@Valid @RequestBody LocalDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocalDTO> actualizar(@PathVariable Long id, @Valid @RequestBody LocalDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLogico(@PathVariable Long id) {
        service.eliminarLogico(id);
        return ResponseEntity.noContent().build();
    }
}