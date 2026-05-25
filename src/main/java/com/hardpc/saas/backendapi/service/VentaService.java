package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.dto.IngresoMensualDTO;
import com.hardpc.saas.backendapi.dto.VentaRequestDTO;
import com.hardpc.saas.backendapi.dto.VentaResponseDTO;
import com.hardpc.saas.backendapi.dto.VentasPorClienteDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaService {
    VentaResponseDTO registrarVenta(VentaRequestDTO dto);
    VentaResponseDTO buscarPorId(Long id);
    Page<VentaResponseDTO> listarPaginadoAvanzado(LocalDateTime inicio, LocalDateTime fin, Long idCliente, Long idLocal, Pageable pageable);

    // BI (Business Intelligence)
    List<IngresoMensualDTO> obtenerReporteIngresoMensual();
    List<VentasPorClienteDTO> obtenerReporteVentasPorCliente();

    // Operaciones Transaccionales
    VentaResponseDTO anularVenta(Long idVenta);
}