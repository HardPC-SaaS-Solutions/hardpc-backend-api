package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // --- Validaciones de Unicidad ---
    boolean existsByCodigoSku(String codigoSku);
    boolean existsByCodigoSkuAndIdProductoNot(String codigoSku, Long idProducto);

    // --- Búsqueda Paginada Flexible (Optimizada para DataTables y Filtros Dinámicos Cruzados) ---
    @Query("SELECT p FROM Producto p WHERE " +
            "(:esSerializado IS NULL OR p.esSerializado = :esSerializado) AND " +
            "(:idCategoria IS NULL OR p.categoria.idCategoria = :idCategoria) AND " +
            "(:idMarca IS NULL OR p.marca.idMarca = :idMarca) AND " +
            "(:idUnidadMedida IS NULL OR p.unidadMedida.idUnidadMedida = :idUnidadMedida) AND " +
            "(LOWER(p.codigoSku) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
            "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :buscar, '%')))")
    Page<Producto> buscarPaginadoAvanzado(
            @Param("buscar") String buscar,
            @Param("esSerializado") Boolean esSerializado,
            @Param("idCategoria") Long idCategoria,
            @Param("idMarca") Long idMarca,
            @Param("idUnidadMedida") Long idUnidadMedida,
            Pageable pageable
    );

    // --- Autonomía Arquitectónica: Consultas por Catálogos ---
    // Útiles para futuros filtros en reportes o en la pantalla de Punto de Venta (POS)
    Page<Producto> findByCategoria_IdCategoriaAndEstadoTrue(Long idCategoria, Pageable pageable);
    Page<Producto> findByMarca_IdMarcaAndEstadoTrue(Long idMarca, Pageable pageable);
}