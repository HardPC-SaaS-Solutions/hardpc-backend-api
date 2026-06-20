package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.dto.GastoMensualDTO;
import com.hardpc.saas.backendapi.dto.GastoProveedorDTO;
import com.hardpc.saas.backendapi.dto.IngresoCompraRequestDTO;
import com.hardpc.saas.backendapi.dto.IngresoCompraResponseDTO;
import com.hardpc.saas.backendapi.enums.EstadoIngreso;
import com.hardpc.saas.backendapi.service.IngresoCompraService;
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
import java.util.List;

@RestController
@RequestMapping("/api/ingresos-compra")
@RequiredArgsConstructor
public class IngresoCompraRestController {

    private final IngresoCompraService service;

    // --- ACCESO DE ESCRITURA ESTRICTO (Operativa de Almacén) ---

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATIVO')")
    public ResponseEntity<IngresoCompraResponseDTO> registrarCompra(@Valid @RequestBody IngresoCompraRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarCompra(dto));
    }

    // --- ACCESO DE LECTURA E HISTÓRICOS ---

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATIVO', 'CAJERO')")
    public ResponseEntity<IngresoCompraResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATIVO', 'CAJERO')")
    public ResponseEntity<Page<IngresoCompraResponseDTO>> listarPaginadoAvanzado(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(required = false) Long idProveedor,
            @RequestParam(required = false) Long idLocal,
            @RequestParam(required = false) EstadoIngreso estado,
            @RequestParam(required = false) String comprobante,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(service.listarPaginadoAvanzado(fechaInicio, fechaFin, idProveedor, idLocal, estado, comprobante, pageable));
    }

    // --- REPORTES FINANCIEROS Y DE AUDITORÍA DE COMPRAS (BI) ---

    @GetMapping("/reportes/gasto-mensual")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<GastoMensualDTO>> obtenerReporteGastoMensual() {
        return ResponseEntity.ok(service.obtenerReporteGastoMensual());
    }

    @GetMapping("/reportes/gasto-proveedor")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<GastoProveedorDTO>> obtenerReporteGastoPorProveedor() {
        return ResponseEntity.ok(service.obtenerReporteGastoPorProveedor());
    }

    // --- ANULACIÓN DE COMPRA ---

    @PutMapping("/{id}/anular")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<IngresoCompraResponseDTO> anularIngresoCompra(@PathVariable Long id) {
        return ResponseEntity.ok(service.anularIngresoCompra(id));
    }
}