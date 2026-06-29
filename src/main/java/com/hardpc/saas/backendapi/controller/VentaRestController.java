package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.dto.IngresoMensualDTO;
import com.hardpc.saas.backendapi.dto.VentaRequestDTO;
import com.hardpc.saas.backendapi.dto.VentaResponseDTO;
import com.hardpc.saas.backendapi.dto.VentasPorClienteDTO;
import com.hardpc.saas.backendapi.service.VentaService;
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

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ContentDisposition;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaRestController {

    private final VentaService service;

    // --- ACCESO DE ESCRITURA ESTRICTO (Operativa de Caja) ---

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'CAJERO')")
    public ResponseEntity<VentaResponseDTO> registrarVenta(@Valid @RequestBody VentaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarVenta(dto));
    }

    // --- ACCESO DE LECTURA E HISTÓRICOS ---

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATIVO', 'CAJERO')")
    public ResponseEntity<VentaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATIVO', 'CAJERO')")
    public ResponseEntity<Page<VentaResponseDTO>> listarPaginadoAvanzado(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(required = false) Long idCliente,
            @RequestParam(required = false) Long idLocal,
            @RequestParam(required = false) com.hardpc.saas.backendapi.enums.EstadoVenta estado,
            @RequestParam(required = false) String comprobante,
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseEntity.ok(service.listarPaginadoAvanzado(fechaInicio, fechaFin, idCliente, idLocal, estado, comprobante, pageable));
    }

    // --- REPORTES FINANCIEROS Y DE AUDITORÍA (BI) ---

    @GetMapping("/reportes/ingreso-mensual")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<IngresoMensualDTO>> obtenerReporteIngresoMensual() {
        return ResponseEntity.ok(service.obtenerReporteIngresoMensual());
    }

    @GetMapping("/reportes/ventas-cliente")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<VentasPorClienteDTO>> obtenerReporteVentasPorCliente() {
        return ResponseEntity.ok(service.obtenerReporteVentasPorCliente());
    }

    // --- ANULACIÓN DE VENTA ---

    @PutMapping("/{id}/anular")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<VentaResponseDTO> anularVenta(@PathVariable Long id) {
        return ResponseEntity.ok(service.anularVenta(id));
    }

    // --- IMPRESIÓN DE TICKETS ---
    @GetMapping("/{id}/ticket")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATIVO', 'CAJERO')")
    public ResponseEntity<byte[]> generarTicketPdf(@PathVariable Long id) {
        byte[] pdfBytes = service.generarTicketPdf(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // "inline" indica al navegador que debe abrirlo en el visor integrado en vez de forzar descarga ciega
        headers.setContentDisposition(ContentDisposition.inline().filename("Ticket_Venta_HardPC_" + id + ".pdf").build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}