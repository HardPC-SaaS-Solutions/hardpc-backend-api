package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.dto.TipoComprobanteDTO;
import com.hardpc.saas.backendapi.service.TipoComprobanteService;
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
@RequestMapping("/api/tipos-comprobante")
@RequiredArgsConstructor
public class TipoComprobanteRestController {

    private final TipoComprobanteService service;

    @GetMapping
    public ResponseEntity<Page<TipoComprobanteDTO>> listarPaginado(
            @RequestParam(required = false, defaultValue = "") String buscar,
            @PageableDefault(size = 10, sort = "descripcion") Pageable pageable) {
        return ResponseEntity.ok(service.listarPaginado(buscar, pageable));
    }

    @GetMapping("/combo")
    public ResponseEntity<List<TipoComprobanteDTO>> listarParaCombo() {
        return ResponseEntity.ok(service.listarActivosParaCombo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoComprobanteDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/sunat/{codigo}")
    public ResponseEntity<TipoComprobanteDTO> buscarPorCodigoSunat(@PathVariable String codigo) {
        return ResponseEntity.ok(service.buscarPorCodigoSunat(codigo));
    }

    @PostMapping
    public ResponseEntity<TipoComprobanteDTO> crear(@Valid @RequestBody TipoComprobanteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoComprobanteDTO> actualizar(@PathVariable Long id, @Valid @RequestBody TipoComprobanteDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLogico(@PathVariable Long id) {
        service.eliminarLogico(id);
        return ResponseEntity.noContent().build();
    }
}