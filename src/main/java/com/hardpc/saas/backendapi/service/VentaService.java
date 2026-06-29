package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.dto.*;
import com.hardpc.saas.backendapi.enums.EstadoVenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaService {
    VentaResponseDTO registrarVenta(VentaRequestDTO dto);
    VentaResponseDTO buscarPorId(Long id);
    Page<VentaResponseDTO> listarPaginadoAvanzado(LocalDateTime inicio, LocalDateTime fin, Long idCliente, Long idLocal, EstadoVenta estado, String comprobante, Pageable pageable);

    // BI (Business Intelligence)
    List<IngresoMensualDTO> obtenerReporteIngresoMensual();
    List<VentasPorClienteDTO> obtenerReporteVentasPorCliente();

    // Recupera el top 10 de productos con mayor rotación/unidades vendidas.
    List<TopProductoDTO> obtenerTopProductosVendidos();

    // Evalúa la productividad y volúmenes de venta acumulados por cada cajero.
    List<RendimientoCajeroDTO> obtenerRendimientoCajeros();

    // Operaciones Transaccionales
    VentaResponseDTO anularVenta(Long idVenta);

    byte[] generarTicketPdf(Long idVenta);
}