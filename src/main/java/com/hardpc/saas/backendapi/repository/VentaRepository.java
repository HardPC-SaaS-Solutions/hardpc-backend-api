package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.dto.IngresoMensualDTO;
import com.hardpc.saas.backendapi.dto.RendimientoCajeroDTO;
import com.hardpc.saas.backendapi.dto.TopProductoDTO;
import com.hardpc.saas.backendapi.dto.VentasPorClienteDTO;
import com.hardpc.saas.backendapi.entity.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    // --- Unicidad de Comprobante ---
    boolean existsByTipoComprobante_IdTipoComprobanteAndSerieComprobanteAndNumeroComprobante(
            Long idTipoComprobante, String serie, String numero);

    // --- Búsqueda Paginada Avanzada ---
    @Query("SELECT v FROM Venta v WHERE " +
            "(:fechaInicio IS NULL OR v.fechaVenta >= :fechaInicio) AND " +
            "(:fechaFin IS NULL OR v.fechaVenta <= :fechaFin) AND " +
            "(:idCliente IS NULL OR v.cliente.idPersona = :idCliente) AND " +
            "(:idLocal IS NULL OR v.local.idLocal = :idLocal) AND " +
            "(:estado IS NULL OR v.estadoVenta = :estado) AND " +
            "(:comprobante IS NULL OR " +
            "  LOWER(v.serieComprobante) LIKE LOWER(CONCAT('%', :comprobante, '%')) OR " +
            "  LOWER(v.numeroComprobante) LIKE LOWER(CONCAT('%', :comprobante, '%')) " +
            ") " +
            "ORDER BY v.fechaVenta DESC")
    Page<Venta> buscarVentasAvanzado(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            @Param("idCliente") Long idCliente,
            @Param("idLocal") Long idLocal,
            @Param("estado") com.hardpc.saas.backendapi.enums.EstadoVenta estado,
            @Param("comprobante") String comprobante,
            Pageable pageable);

    // --- REPORTES FINANCIEROS (BI) ---
    @Query("SELECT new com.hardpc.saas.backendapi.dto.IngresoMensualDTO(" +
            "YEAR(v.fechaVenta), MONTH(v.fechaVenta), SUM(v.totalVenta), COUNT(v)) " +
            "FROM Venta v " +
            "WHERE v.estadoVenta = 'REGISTRADA' " +
            "GROUP BY YEAR(v.fechaVenta), MONTH(v.fechaVenta) " +
            "ORDER BY YEAR(v.fechaVenta) DESC, MONTH(v.fechaVenta) DESC")
    List<IngresoMensualDTO> obtenerIngresoMensual();

    @Query("SELECT new com.hardpc.saas.backendapi.dto.VentasPorClienteDTO(" +
            "v.cliente.idPersona, v.cliente.numeroDocumento, " +
            "CONCAT(COALESCE(v.cliente.nombres, ''), ' ', COALESCE(v.cliente.apellidos, ''), COALESCE(v.cliente.razonSocial, '')), " +
            "SUM(v.totalVenta), COUNT(v)) " +
            "FROM Venta v GROUP BY v.cliente.idPersona, v.cliente.numeroDocumento, v.cliente.nombres, v.cliente.apellidos, v.cliente.razonSocial " +
            "ORDER BY SUM(v.totalVenta) DESC")
    List<VentasPorClienteDTO> obtenerVentasPorCliente();

    // 1. TOP 10 Productos Más Vendidos (Rotación de Inventario)
    // Se usa Pageable para limitar los resultados desde el Service (ej. PageRequest.of(0, 10))
    @Query("SELECT new com.hardpc.saas.backendapi.dto.TopProductoDTO(d.producto.idProducto, d.producto.descripcion, SUM(d.cantidad)) " +
            "FROM DetalleVenta d WHERE d.venta.estadoVenta = 'REGISTRADA' " +
            "GROUP BY d.producto.idProducto, d.producto.descripcion " +
            "ORDER BY SUM(d.cantidad) DESC")
    List<TopProductoDTO> obtenerTopProductosVendidos(Pageable pageable);

    // 2. Rendimiento de Cajeros (Productividad)
    @Query("SELECT new com.hardpc.saas.backendapi.dto.RendimientoCajeroDTO(v.usuario.idPersona, v.usuario.username, SUM(v.totalVenta), COUNT(v)) " +
            "FROM Venta v WHERE v.estadoVenta = 'REGISTRADA' " +
            "GROUP BY v.usuario.idPersona, v.usuario.username " +
            "ORDER BY SUM(v.totalVenta) DESC")
    List<RendimientoCajeroDTO> obtenerRendimientoCajeros();

    // Calcula el total vendido en efectivo por un cajero en un local específico desde que abrió la caja
    @Query("SELECT COALESCE(SUM(v.totalVenta), 0) FROM Venta v WHERE v.usuario.idPersona = :idUsuario AND v.local.idLocal = :idLocal AND v.formaPago.idFormaPago = 1 AND v.fechaVenta >= :fechaApertura AND v.estadoVenta = 'REGISTRADA'")
    BigDecimal sumarVentasEfectivoEnTurno(
            @Param("idUsuario") Long idUsuario,
            @Param("idLocal") Long idLocal,
            @Param("fechaApertura") LocalDateTime fechaApertura
    );
}