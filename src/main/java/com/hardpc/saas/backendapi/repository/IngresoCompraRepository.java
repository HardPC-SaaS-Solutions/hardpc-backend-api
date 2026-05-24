package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.dto.GastoMensualDTO;
import com.hardpc.saas.backendapi.dto.GastoProveedorDTO;
import com.hardpc.saas.backendapi.entity.IngresoCompra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IngresoCompraRepository extends JpaRepository<IngresoCompra, Long> {

    // --- Validación de Unicidad de Documento Físico (Evita doble facturación) ---
    boolean existsByProveedor_IdProveedorAndTipoComprobante_IdTipoComprobanteAndSerieComprobanteAndNumeroComprobante(
            Long idProveedor, Long idTipoComprobante, String serieComprobante, String numeroComprobante);

    // --- Búsqueda Paginada de Auditoría ---
    @Query("SELECT i FROM IngresoCompra i WHERE " +
            "(:fechaInicio IS NULL OR i.fechaIngreso >= :fechaInicio) AND " +
            "(:fechaFin IS NULL OR i.fechaIngreso <= :fechaFin) AND " +
            "(:idProveedor IS NULL OR i.proveedor.idProveedor = :idProveedor) AND " +
            "(:idLocal IS NULL OR i.local.idLocal = :idLocal) " +
            "ORDER BY i.fechaIngreso DESC")
    Page<IngresoCompra> buscarPaginadoAvanzado(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            @Param("idProveedor") Long idProveedor,
            @Param("idLocal") Long idLocal,
            Pageable pageable);

    // --- REPORTES FINANCIEROS Y BI ---

    @Query("SELECT new com.hardpc.saas.backendapi.dto.GastoMensualDTO(" +
            "YEAR(i.fechaIngreso), MONTH(i.fechaIngreso), SUM(i.totalCompra), COUNT(i)) " +
            "FROM IngresoCompra i GROUP BY YEAR(i.fechaIngreso), MONTH(i.fechaIngreso) " +
            "ORDER BY YEAR(i.fechaIngreso) DESC, MONTH(i.fechaIngreso) DESC")
    List<GastoMensualDTO> obtenerGastoMensual();

    @Query("SELECT new com.hardpc.saas.backendapi.dto.GastoProveedorDTO(" +
            "i.proveedor.idProveedor, i.proveedor.razonSocial, SUM(i.totalCompra), COUNT(i)) " +
            "FROM IngresoCompra i GROUP BY i.proveedor.idProveedor, i.proveedor.razonSocial " +
            "ORDER BY SUM(i.totalCompra) DESC")
    List<GastoProveedorDTO> obtenerGastoPorProveedor();
}