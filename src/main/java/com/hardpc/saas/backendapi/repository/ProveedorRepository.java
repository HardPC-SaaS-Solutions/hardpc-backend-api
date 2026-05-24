package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.entity.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    // --- Validaciones de Unicidad (Creación) ---
    boolean existsByRuc(String ruc);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByRazonSocialIgnoreCase(String razonSocial);
    boolean existsByNombreComercialIgnoreCase(String nombreComercial);

    // --- Validaciones de Unicidad (Actualización) ---
    boolean existsByRucAndIdProveedorNot(String ruc, Long idProveedor);
    boolean existsByEmailIgnoreCaseAndIdProveedorNot(String email, Long idProveedor);
    boolean existsByRazonSocialIgnoreCaseAndIdProveedorNot(String razonSocial, Long idProveedor);
    boolean existsByNombreComercialIgnoreCaseAndIdProveedorNot(String nombreComercial, Long idProveedor);

    // --- Búsqueda Paginada Dinámica ---
    @Query("SELECT p FROM Proveedor p WHERE " +
            "LOWER(p.ruc) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
            "LOWER(p.razonSocial) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
            "LOWER(COALESCE(p.nombreComercial, '')) LIKE LOWER(CONCAT('%', :buscar, '%'))")
    Page<Proveedor> buscarPaginado(@Param("buscar") String buscar, Pageable pageable);
}