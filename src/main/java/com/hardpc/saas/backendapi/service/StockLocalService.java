package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.dto.InversionStockDTO;
import com.hardpc.saas.backendapi.dto.StockLocalRequestDTO;
import com.hardpc.saas.backendapi.dto.StockLocalResponseDTO;
import com.hardpc.saas.backendapi.enums.TipoMovimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StockLocalService {
    // Operaciones CRUD base (Sin Delete)
    Page<StockLocalResponseDTO> listarPaginado(Pageable pageable);
    StockLocalResponseDTO buscarPorId(Long id);
    StockLocalResponseDTO crear(StockLocalRequestDTO dto);
    StockLocalResponseDTO actualizar(Long id, StockLocalRequestDTO dto);

    // Absorciones (Reportes y Consultas Específicas)
    Page<StockLocalResponseDTO> listarAlertasStockMinimo(Pageable pageable);
    List<InversionStockDTO> obtenerReporteInversion();
    Page<StockLocalResponseDTO> buscarEnLocalPaginado(Long idLocal, String buscar, Pageable pageable);

    void actualizarStock(Long idProducto, Long idLocal, Integer cantidad, TipoMovimiento tipoMovimiento);
}