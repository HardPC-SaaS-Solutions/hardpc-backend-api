package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.dto.ItemSerialRequestDTO;
import com.hardpc.saas.backendapi.dto.ItemSerialResponseDTO;
import com.hardpc.saas.backendapi.dto.ResumenEstadoSerialDTO;
import com.hardpc.saas.backendapi.enums.EstadoDisponibilidad;
import com.hardpc.saas.backendapi.service.ItemSerialService;
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
@RequestMapping("/api/items-seriales")
@RequiredArgsConstructor
public class ItemSerialRestController {

    private final ItemSerialService service;

    // --- ACCESO DE LECTURA (Toda la operación: Búsquedas y Trazabilidad) ---

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATIVO', 'CAJERO')")
    public ResponseEntity<Page<ItemSerialResponseDTO>> listarPaginado(
            @RequestParam(required = false, defaultValue = "") String buscar,
            @RequestParam(required = false) Long idLocal,
            @PageableDefault(size = 10, sort = "numeroSerie") Pageable pageable) {
        return ResponseEntity.ok(service.listarPaginado(buscar, idLocal, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATIVO', 'CAJERO')")
    public ResponseEntity<ItemSerialResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    /**
     * Endpoint de Trazabilidad: Permite a un cajero o encargado de garantías escanear
     * un serial con una pistola de códigos y saber automáticamente el estado y origen del equipo.
     */
    @GetMapping("/serial/{numeroSerie}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATIVO', 'CAJERO')")
    public ResponseEntity<ItemSerialResponseDTO> buscarPorSerialExacto(@PathVariable String numeroSerie) {
        return ResponseEntity.ok(service.buscarPorSerialExacto(numeroSerie));
    }

    @GetMapping("/local/{idLocal}/disponibles")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATIVO', 'CAJERO')")
    public ResponseEntity<Page<ItemSerialResponseDTO>> listarDisponiblesPorLocal(
            @PathVariable Long idLocal,
            @PageableDefault(size = 10, sort = "fechaCreacion") Pageable pageable) {
        return ResponseEntity.ok(service.listarDisponiblesPorLocal(idLocal, pageable));
    }

    // --- REPORTES FINANCIEROS Y AUDITORÍA ---

    @GetMapping("/reporte-estados")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATIVO')")
    public ResponseEntity<List<ResumenEstadoSerialDTO>> reporteEstadosAgrupados() {
        return ResponseEntity.ok(service.reporteEstadosAgrupados());
    }

    // --- ACCESO DE ESCRITURA (Gestión administrativa restringida) ---

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<ItemSerialResponseDTO> crear(@Valid @RequestBody ItemSerialRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<ItemSerialResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ItemSerialRequestDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    /**
     * Endpoint Operativo: Permite mandar rápidamente un equipo defectuoso a 'EN_REPARACION'
     * sin necesidad de cargar un formulario pesado mediante PUT.
     */
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoDisponibilidad nuevoEstado) {
        service.cambiarEstado(id, nuevoEstado);
        return ResponseEntity.ok().build();
    }
}