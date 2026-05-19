package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.dto.ProductoBusquedaAlmacenDTO;
import com.hardpc.saas.backendapi.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    @Query("""
SELECT new com.hardpc.saas.backendapi.dto.ProductoBusquedaAlmacenDTO(
    p.idProducto,
    p.codigoSku,
    p.descripcion,
    m.nombre,
    c.nombre,
    p.precioUsd,
    sl.cantidadActual,
    sl.stockMinimo,
    sl.local.idLocal
)
FROM Producto p
JOIN p.marca m
JOIN p.categoria c
LEFT JOIN StockLocal sl
    ON sl.producto = p
    AND sl.local.idLocal = :idLocal
WHERE p.estado = true
AND (:sku IS NULL OR p.codigoSku LIKE %:sku%)
AND (:marca IS NULL OR LOWER(m.nombre) LIKE LOWER(CONCAT('%', :marca, '%')))
AND (:categoria IS NULL OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :categoria, '%')))
AND (:descripcion IS NULL OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :descripcion, '%')))
""")
    List<ProductoBusquedaAlmacenDTO> buscarPorLocal(
            @Param("idLocal") Long idLocal,
            @Param("sku") String sku,
            @Param("marca") String marca,
            @Param("categoria") String categoria,
            @Param("descripcion") String descripcion
    );
}