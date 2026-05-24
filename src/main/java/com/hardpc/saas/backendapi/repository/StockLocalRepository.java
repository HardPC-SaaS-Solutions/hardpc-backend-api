package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.dto.InversionStockDTO;
import com.hardpc.saas.backendapi.entity.StockLocal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}