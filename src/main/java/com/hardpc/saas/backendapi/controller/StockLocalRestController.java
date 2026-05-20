package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.dto.InversionStockDTO;
import com.hardpc.saas.backendapi.dto.StockLocalRequestDTO;
import com.hardpc.saas.backendapi.dto.StockLocalResponseDTO;
import com.hardpc.saas.backendapi.service.StockLocalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks-locales")
@RequiredArgsConstructor
public class StockLocalRestController {

    private final StockLocalService service;

    // --- ACCESO DE LECTURA / BÚSQUEDAS (Operativos) ---

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATIVO', 'CAJERO')")
    public ResponseEntity<Page<StockLocalResponseDTO>> listarPaginado(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(service.listarPaginado(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATIVO', 'CAJERO')")
    public ResponseEntity<StockLocalResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/local/{idLocal}/buscar")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATIVO', 'CAJERO')")
    public ResponseEntity<Page<StockLocalResponseDTO>> buscarEnLocalPaginado(
            @PathVariable Long idLocal,
            @RequestParam(required = false, defaultValue = "") String buscar,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(service.buscarEnLocalPaginado(idLocal, buscar, pageable));
    }

    // --- REPORTES FINANCIEROS Y DE ALERTA (Solo Jefaturas) ---

    @GetMapping("/alertas-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Page<StockLocalResponseDTO>> listarAlertasStockMinimo(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(service.listarAlertasStockMinimo(pageable));
    }

    @GetMapping("/reporte-inversion")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<InversionStockDTO>> obtenerReporteInversion() {
        return ResponseEntity.ok(service.obtenerReporteInversion());
    }

    // --- ACCESO DE ESCRITURA (Gestión administrativa) ---

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<StockLocalResponseDTO> crear(@Valid @RequestBody StockLocalRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<StockLocalResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody StockLocalRequestDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    // NOTA DE ARQUITECTO: Omitimos deliberadamente el endpoint @DeleteMapping.
    // Un registro de stock nunca se borra, su cantidad evoluciona (o decae a 0).
}