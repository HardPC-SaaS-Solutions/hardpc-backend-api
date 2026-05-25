package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.dto.IngresoMensualDTO;
import com.hardpc.saas.backendapi.dto.VentasPorClienteDTO;
import com.hardpc.saas.backendapi.entity.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    // --- Unicidad de Comprobante ---
    boolean existsByTipoComprobante_IdTipoComprobanteAndSerieComprobanteAndNumeroComprobante(
            Long idTipoComprobante, String serie, String numero);

    // --- Búsqueda Paginada Avanzada (CRÍTICO: Manejo dinámico de fechas con >= y <=) ---
    @Query("SELECT v FROM Venta v WHERE " +
            "(:fechaInicio IS NULL OR v.fechaVenta >= :fechaInicio) AND " +
            "(:fechaFin IS NULL OR v.fechaVenta <= :fechaFin) AND " +
            "(:idCliente IS NULL OR v.cliente.idPersona = :idCliente) AND " +
            "(:idLocal IS NULL OR v.local.idLocal = :idLocal) " +
            "ORDER BY v.fechaVenta DESC")
    Page<Venta> buscarVentasAvanzado(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            @Param("idCliente") Long idCliente,
            @Param("idLocal") Long idLocal,
            Pageable pageable);

    // --- REPORTES FINANCIEROS (BI) ---
    @Query("SELECT new com.hardpc.saas.backendapi.dto.IngresoMensualDTO(" +
            "YEAR(v.fechaVenta), MONTH(v.fechaVenta), SUM(v.totalVenta), COUNT(v)) " +
            "FROM Venta v GROUP BY YEAR(v.fechaVenta), MONTH(v.fechaVenta) " +
            "ORDER BY YEAR(v.fechaVenta) DESC, MONTH(v.fechaVenta) DESC")
    List<IngresoMensualDTO> obtenerIngresoMensual();

    @Query("SELECT new com.hardpc.saas.backendapi.dto.VentasPorClienteDTO(" +
            "v.cliente.idPersona, v.cliente.numeroDocumento, " +
            "CONCAT(COALESCE(v.cliente.nombres, ''), ' ', COALESCE(v.cliente.apellidos, ''), COALESCE(v.cliente.razonSocial, '')), " +
            "SUM(v.totalVenta), COUNT(v)) " +
            "FROM Venta v GROUP BY v.cliente.idPersona, v.cliente.numeroDocumento, v.cliente.nombres, v.cliente.apellidos, v.cliente.razonSocial " +
            "ORDER BY SUM(v.totalVenta) DESC")
    List<VentasPorClienteDTO> obtenerVentasPorCliente();
}