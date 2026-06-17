package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.dto.InversionStockDTO;
import com.hardpc.saas.backendapi.entity.StockLocal;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockLocalRepository extends JpaRepository<StockLocal, Long> {

    // --- 1. Validaciones de Unicidad ---
    boolean existsByProducto_IdProductoAndLocal_IdLocal(Long idProducto, Long idLocal);

    boolean existsByProducto_IdProductoAndLocal_IdLocalAndIdStockLocalNot(Long idProducto, Long idLocal, Long idStockLocal);

    // --- 2. Absorción 1 (Alertas de Stock Mínimo) ---
    @Query("SELECT s FROM StockLocal s WHERE s.cantidadActual <= s.stockMinimo")
    Page<StockLocal> findAlertasStockMinimo(Pageable pageable);

    // --- 3. Absorción 2 (Inversión por Local) ---
    @Query("SELECT new com.hardpc.saas.backendapi.dto.InversionStockDTO(s.local.idLocal, s.local.nombre, SUM(s.cantidadActual * s.producto.precioUsd)) " +
            "FROM StockLocal s GROUP BY s.local.idLocal, s.local.nombre")
    List<InversionStockDTO> obtenerInversionPorLocal();

    // --- 4. Absorción 3 (Búsqueda Avanzada dentro de un Local) ---
    @Query("SELECT s FROM StockLocal s WHERE s.local.idLocal = :idLocal AND " +
            "(LOWER(s.producto.codigoSku) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
            "LOWER(s.producto.descripcion) LIKE LOWER(CONCAT('%', :buscar, '%')))")
    Page<StockLocal> buscarEnLocalPaginado(@Param("idLocal") Long idLocal, @Param("buscar") String buscar, Pageable pageable);

    // Verifica si hay stock físico suficiente para una venta
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM StockLocal s WHERE s.producto.idProducto = :idProducto AND s.local.idLocal = :idLocal AND s.cantidadActual >= :cantidadRequerida")
    boolean hasStockSuficiente(@Param("idProducto") Long idProducto, @Param("idLocal") Long idLocal, @Param("cantidadRequerida") Integer cantidadRequerida);

    // --- BLOQUEO PESIMISTA: Prevención de Condiciones de Carrera (Race Conditions) ---
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
    Optional<StockLocal> findByProducto_IdProductoAndLocal_IdLocal(Long idProducto, Long idLocal);

    @Query("""
    SELECT s
    FROM StockLocal s
    JOIN FETCH s.producto p
    WHERE s.local.idLocal = :idLocal
    AND (
        :buscar = '' OR
        LOWER(p.codigoSku) LIKE LOWER(CONCAT('%', :buscar, '%'))
        OR
        LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :buscar, '%'))
    )
    ORDER BY p.descripcion
    """)
    List<StockLocal> buscarTodoPorLocal(
            @Param("idLocal") Long idLocal,
            @Param("buscar") String buscar
    );
    }