package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.dto.TipoDocumentoDTO;
import com.hardpc.saas.backendapi.service.TipoDocumentoService;
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
@RequestMapping("/api/tipos-documento")
@RequiredArgsConstructor
public class TipoDocumentoRestController {

    private final TipoDocumentoService service;

    @GetMapping
    public ResponseEntity<Page<TipoDocumentoDTO>> listarPaginado(
            @RequestParam(required = false, defaultValue = "") String buscar,
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(service.listarPaginado(buscar, pageable));
    }

    @GetMapping("/combo")
    public ResponseEntity<List<TipoDocumentoDTO>> listarParaCombo() {
        return ResponseEntity.ok(service.listarActivosParaCombo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoDocumentoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/abreviatura/{abreviatura}")
    public ResponseEntity<TipoDocumentoDTO> buscarPorAbreviatura(@PathVariable String abreviatura) {
        return ResponseEntity.ok(service.buscarPorAbreviatura(abreviatura));
    }

    @PostMapping
    public ResponseEntity<TipoDocumentoDTO> crear(@Valid @RequestBody TipoDocumentoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoDocumentoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody TipoDocumentoDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLogico(@PathVariable Long id) {
        service.eliminarLogico(id);
        return ResponseEntity.noContent().build();
    }
}