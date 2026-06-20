package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.dto.MovimientoInventarioRequestDTO;
import com.hardpc.saas.backendapi.dto.MovimientoInventarioResponseDTO;
import com.hardpc.saas.backendapi.enums.TipoMovimiento;
import com.hardpc.saas.backendapi.service.MovimientoInventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/movimientos-inventario")
@RequiredArgsConstructor
public class MovimientoInventarioRestController {

    private final MovimientoInventarioService service;

    // --- ACCESO DE LECTURA E HISTÓRICOS (Toda la operación comercial) ---

    // --- Historial General (El Ledger completo) ---
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATIVO', 'CAJERO')")
    public ResponseEntity<Page<MovimientoInventarioResponseDTO>> listarTodos(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.listarTodos(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATIVO', 'CAJERO')")
    public ResponseEntity<MovimientoInventarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/producto/{idProducto}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATIVO', 'CAJERO')")
    public ResponseEntity<Page<MovimientoInventarioResponseDTO>> listarPorProducto(
            @PathVariable Long idProducto,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.listarPorProducto(idProducto, pageable));
    }

    @GetMapping("/local/{idLocal}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATIVO', 'CAJERO')")
    public ResponseEntity<Page<MovimientoInventarioResponseDTO>> listarPorLocal(
            @PathVariable Long idLocal,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.listarPorLocal(idLocal, pageable));
    }

    @GetMapping("/auditoria")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATIVO', 'CAJERO')")
    public ResponseEntity<Page<MovimientoInventarioResponseDTO>> filtrarHistorial(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(required = false) TipoMovimiento tipo,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.filtrarHistorial(fechaInicio, fechaFin, tipo, pageable));
    }

    // --- ACCESO DE ESCRITURA ESTRICTO (El Ledger solo crece, nunca muta ni decrece) ---

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATIVO')")
    public ResponseEntity<MovimientoInventarioResponseDTO> registrarMovimiento(@Valid @RequestBody MovimientoInventarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarMovimiento(dto));
    }

    @PostMapping("/traslado")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATIVO')")
    public ResponseEntity<MovimientoInventarioResponseDTO> registrarTraslado(@Valid @RequestBody MovimientoInventarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarTraslado(dto));
    }

    // ARQUITECTURA: SIN @PutMapping, SIN @DeleteMapping, SIN @PatchMapping.
    // Un registro contable/logístico jamás se borra ni edita.
}