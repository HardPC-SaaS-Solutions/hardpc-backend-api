package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.dto.InversionStockDTO;
import com.hardpc.saas.backendapi.dto.StockMinimoDTO;
import com.hardpc.saas.backendapi.entity.StockLocal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockLocalRepository extends JpaRepository<StockLocal, Long> {

    @Query(""" 
         SELECT new com.hardpc.saas.backendapi.dto.InversionStockDTO(
            s.local.nombre,
            SUM(p.precioUsd * s.cantidadActual)
        )
        FROM StockLocal s
        JOIN s.producto p
        GROUP BY s.local.nombre"""

    )
    List<InversionStockDTO> obtenerInversionPorLocal();
    @Query("""
    SELECT new com.hardpc.saas.backendapi.dto.StockMinimoDTO(
        s.local.nombre,
        s.producto.descripcion,
        s.cantidadActual,
        s.stockMinimo
    )
    FROM StockLocal s
    WHERE s.cantidadActual <= s.stockMinimo
""")
    List<StockMinimoDTO> obtenerProductosConStockMinimo();
}

