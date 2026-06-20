package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.dto.MovimientoInventarioRequestDTO;
import com.hardpc.saas.backendapi.dto.MovimientoInventarioResponseDTO;
import com.hardpc.saas.backendapi.enums.TipoMovimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface MovimientoInventarioService {
    // Inmutabilidad estricta: Solo Leer y Crear
    MovimientoInventarioResponseDTO buscarPorId(Long id);
    MovimientoInventarioResponseDTO registrarMovimiento(MovimientoInventarioRequestDTO dto);
    MovimientoInventarioResponseDTO registrarTraslado(MovimientoInventarioRequestDTO dto);

    // Históricos y Auditoría
    Page<MovimientoInventarioResponseDTO> listarTodos(Pageable pageable);
    Page<MovimientoInventarioResponseDTO> listarPorProducto(Long idProducto, Pageable pageable);
    Page<MovimientoInventarioResponseDTO> listarPorLocal(Long idLocal, Pageable pageable);
    Page<MovimientoInventarioResponseDTO> filtrarHistorial(LocalDateTime fechaInicio, LocalDateTime fechaFin, TipoMovimiento tipo, Pageable pageable);
}