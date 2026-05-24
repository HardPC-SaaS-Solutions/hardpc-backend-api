package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.dto.FormaPagoDTO;
import com.hardpc.saas.backendapi.service.FormaPagoService;
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
@RequestMapping("/api/formas-pago")
@RequiredArgsConstructor
public class FormaPagoRestController {

    private final FormaPagoService service;

    @GetMapping
    public ResponseEntity<Page<FormaPagoDTO>> listarPaginado(
            @RequestParam(required = false, defaultValue = "") String buscar,
            @PageableDefault(size = 10, sort = "descripcion") Pageable pageable) {
        return ResponseEntity.ok(service.listarPaginado(buscar, pageable));
    }

    @GetMapping("/combo")
    public ResponseEntity<List<FormaPagoDTO>> listarParaCombo() {
        return ResponseEntity.ok(service.listarActivosParaCombo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormaPagoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<FormaPagoDTO> crear(@Valid @RequestBody FormaPagoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FormaPagoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody FormaPagoDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLogico(@PathVariable Long id) {
        service.eliminarLogico(id);
        return ResponseEntity.noContent().build();
    }
}