package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.dto.ClienteRequestDTO;
import com.hardpc.saas.backendapi.dto.ClienteResponseDTO;
import com.hardpc.saas.backendapi.enums.TipoCliente;
import com.hardpc.saas.backendapi.service.ClienteService;
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
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteRestController {

    private final ClienteService service;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATIVO', 'CAJERO')")
    @GetMapping
    public ResponseEntity<Page<ClienteResponseDTO>> listarPaginado(
            @RequestParam(required = false, defaultValue = "") String buscar,
            @RequestParam(required = false) TipoCliente tipoCliente,
            @PageableDefault(size = 10, sort = "numeroDocumento") Pageable pageable) {
        return ResponseEntity.ok(service.listarPaginado(buscar, tipoCliente, pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATIVO', 'CAJERO')")
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'CAJERO')")
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> crear(@Valid @RequestBody ClienteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'CAJERO')")
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ClienteRequestDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLogico(@PathVariable Long id) {
        service.eliminarLogico(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivarCliente(@PathVariable Long id) {
        service.reactivar(id);
        return ResponseEntity.ok().build();
    }
}