package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.entity.MovimientoInventario;
import com.hardpc.saas.backendapi.enums.TipoMovimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    // --- Historial por Producto ---
    Page<MovimientoInventario> findByProducto_IdProductoOrderByFechaHoraDesc(Long idProducto, Pageable pageable);

    // --- Historial por Local (Sea Origen o Destino) ---
    @Query("SELECT m FROM MovimientoInventario m WHERE m.localOrigen.idLocal = :idLocal OR m.localDestino.idLocal = :idLocal ORDER BY m.fechaHora DESC")
    Page<MovimientoInventario> findByLocalOrigenOrLocalDestinoPaginado(@Param("idLocal") Long idLocal, Pageable pageable);

    // --- Filtros Dinámicos de Auditoría ---
    @Query("SELECT m FROM MovimientoInventario m WHERE " +
            "(:fechaInicio IS NULL OR m.fechaHora >= :fechaInicio) AND " +
            "(:fechaFin IS NULL OR m.fechaHora <= :fechaFin) AND " +
            "(:tipoMovimiento IS NULL OR m.tipoMovimiento = :tipoMovimiento) " +
            "ORDER BY m.fechaHora DESC")
    Page<MovimientoInventario> buscarPorFiltrosAuditoria(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            @Param("tipoMovimiento") TipoMovimiento tipoMovimiento,
            Pageable pageable);

    // --- Historial General Cronológico (Sin Filtros) ---
    Page<MovimientoInventario> findAllByOrderByFechaHoraDesc(Pageable pageable);
}