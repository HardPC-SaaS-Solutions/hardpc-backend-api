package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.dto.CajaCierreRequestDTO;
import com.hardpc.saas.backendapi.dto.CajaSesionRequestDTO;
import com.hardpc.saas.backendapi.entity.CajaSesion;
import com.hardpc.saas.backendapi.enums.EstadoCaja;
import com.hardpc.saas.backendapi.repository.CajaSesionRepository;
import com.hardpc.saas.backendapi.repository.LocalRepository;
import com.hardpc.saas.backendapi.repository.VentaRepository;
import com.hardpc.saas.backendapi.security.CustomUserDetails;
import com.hardpc.saas.backendapi.exception.custom.BusinessException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/cajas")
@RequiredArgsConstructor
public class CajaSesionRestController {

    private final CajaSesionRepository repository;
    private final LocalRepository localRepository;
    private final VentaRepository ventaRepository;

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

    @PostMapping("/cerrar")
    @PreAuthorize("hasAnyRole('CAJERO', 'SUPERVISOR', 'ADMIN')")
    @Transactional // Garantiza atomicidad en el cálculo y guardado
    public ResponseEntity<?> cerrarCaja(@Valid @RequestBody CajaCierreRequestDTO dto) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long idUsuario = user.getUsuario().getIdPersona();

        CajaSesion cajaActiva = repository.findByUsuario_IdPersonaAndEstado(idUsuario, EstadoCaja.ABIERTA)
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "ERR_SIN_CAJA", "No tienes ninguna sesión de caja abierta para cerrar."));

        // 1. Calcular cuánto dinero debería haber en sistema (Apertura + Ventas en Efectivo)
        BigDecimal ventasEfectivo = ventaRepository.sumarVentasEfectivoEnTurno(
                idUsuario,
                cajaActiva.getLocal().getIdLocal(),
                cajaActiva.getFechaApertura()
        );

        BigDecimal montoSistema = cajaActiva.getMontoApertura().add(ventasEfectivo);

        // 2. Aplicar el cierre inmutable
        cajaActiva.setMontoCierreSistema(montoSistema);
        cajaActiva.setMontoCierreEfectivoReal(dto.getMontoCierreEfectivoReal());
        cajaActiva.setFechaCierre(LocalDateTime.now());
        cajaActiva.setEstado(EstadoCaja.CERRADA);

        return ResponseEntity.ok(repository.save(cajaActiva));
    }
}