package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.dto.GastoMensualDTO;
import com.hardpc.saas.backendapi.dto.GastoProveedorDTO;
import com.hardpc.saas.backendapi.dto.IngresoCompraRequestDTO;
import com.hardpc.saas.backendapi.dto.IngresoCompraResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface IngresoCompraService {
    IngresoCompraResponseDTO registrarCompra(IngresoCompraRequestDTO dto);
    IngresoCompraResponseDTO buscarPorId(Long id);
    Page<IngresoCompraResponseDTO> listarPaginadoAvanzado(LocalDateTime inicio, LocalDateTime fin, Long idProveedor, Long idLocal, Pageable pageable);

    // Reportes BI
    List<GastoMensualDTO> obtenerReporteGastoMensual();
    List<GastoProveedorDTO> obtenerReporteGastoPorProveedor();

    IngresoCompraResponseDTO anularIngresoCompra(Long idIngreso);
}