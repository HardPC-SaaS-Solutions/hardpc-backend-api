package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.dto.CajaSesionRequestDTO;
import com.hardpc.saas.backendapi.entity.CajaSesion;
import com.hardpc.saas.backendapi.enums.EstadoCaja;
import com.hardpc.saas.backendapi.repository.CajaSesionRepository;
import com.hardpc.saas.backendapi.repository.LocalRepository;
import com.hardpc.saas.backendapi.security.CustomUserDetails;
import com.hardpc.saas.backendapi.exception.custom.BusinessException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/cajas")
@RequiredArgsConstructor
public class CajaSesionRestController {

    private final CajaSesionRepository repository;
    private final LocalRepository localRepository;

    @GetMapping("/mi-estado")
    @PreAuthorize("hasAnyRole('CAJERO', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<?> obtenerMiCajaActiva() {
        Long idUsuario = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsuario().getIdPersona();
        return repository.findByUsuario_IdPersonaAndEstado(idUsuario, EstadoCaja.ABIERTA)
                .map(caja -> ResponseEntity.ok().body(caja)) // Aquí idealmente deberías usar un DTO y un Mapper
                .orElse(ResponseEntity.noContent().build()); // Retorna 204 Si no hay caja abierta
    }

    @PostMapping("/aperturar")
    @PreAuthorize("hasAnyRole('CAJERO', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<?> aperturarCaja(@Valid @RequestBody CajaSesionRequestDTO dto) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long idUsuario = user.getUsuario().getIdPersona();

        if (repository.existsByUsuario_IdPersonaAndEstado(idUsuario, EstadoCaja.ABIERTA)) {
            throw new BusinessException(HttpStatus.CONFLICT, "ERR_CAJA_YA_ABIERTA", "Ya tienes una sesión de caja activa.");
        }

        CajaSesion nuevaCaja = CajaSesion.builder()
                .usuario(user.getUsuario())
                .local(localRepository.getReferenceById(dto.getIdLocal()))
                .montoApertura(dto.getMontoApertura())
                .fechaApertura(LocalDateTime.now())
                .estado(EstadoCaja.ABIERTA)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(nuevaCaja));
    }
}