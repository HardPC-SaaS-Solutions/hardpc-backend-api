package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.dto.ProductoRequestDTO;
import com.hardpc.saas.backendapi.dto.ProductoResponseDTO;
import com.hardpc.saas.backendapi.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoRestController {

    private final ProductoService service;

    // --- ACCESO DE LECTURA (Toda la operación) ---
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATIVO', 'CAJERO')")
    public ResponseEntity<Page<ProductoResponseDTO>> listarPaginado(
            @RequestParam(required = false, defaultValue = "") String buscar,
            @PageableDefault(size = 10, sort = "codigoSku") Pageable pageable) {
        return ResponseEntity.ok(service.listarPaginado(buscar, pageable));
    }

    // --- ACCESO DE LECTURA: ENDPOINTS PERSONALIZADOS (POS) ---
    @GetMapping("/categoria/{idCategoria}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATIVO', 'CAJERO')")
    public ResponseEntity<Page<ProductoResponseDTO>> listarActivosPorCategoria(
            @PathVariable Long idCategoria,
            @PageableDefault(size = 20, sort = "descripcion") Pageable pageable) {
        return ResponseEntity.ok(service.listarActivosPorCategoria(idCategoria, pageable));
    }

    @GetMapping("/marca/{idMarca}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATIVO', 'CAJERO')")
    public ResponseEntity<Page<ProductoResponseDTO>> listarActivosPorMarca(
            @PathVariable Long idMarca,
            @PageableDefault(size = 20, sort = "descripcion") Pageable pageable) {
        return ResponseEntity.ok(service.listarActivosPorMarca(idMarca, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATIVO', 'CAJERO')")
    public ResponseEntity<ProductoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // --- ACCESO DE ESCRITURA (Gestión administrativa) ---
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<ProductoResponseDTO> crear(@Valid @RequestBody ProductoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<ProductoResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequestDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Void> eliminarLogico(@PathVariable Long id) {
        service.eliminarLogico(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivar")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Void> reactivarProducto(@PathVariable Long id) {
        service.reactivar(id);
        return ResponseEntity.ok().build();
    }
}