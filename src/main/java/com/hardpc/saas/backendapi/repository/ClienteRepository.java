package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // --- Validaciones de Unicidad (Creación) ---
    boolean existsByEmail(String email);
    boolean existsByNumeroDocumento(String numeroDocumento);

    // --- Validaciones de Unicidad (Actualización) ---
    boolean existsByEmailAndIdPersonaNot(String email, Long idPersona);
    boolean existsByNumeroDocumentoAndIdPersonaNot(String numeroDocumento, Long idPersona);

    // --- Búsqueda Paginada Flexible ---
    @Query("SELECT c FROM Cliente c WHERE " +
            "LOWER(c.numeroDocumento) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
            "LOWER(COALESCE(c.nombres, '')) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
            "LOWER(COALESCE(c.apellidos, '')) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
            "LOWER(COALESCE(c.razonSocial, '')) LIKE LOWER(CONCAT('%', :buscar, '%'))")
    Page<Cliente> buscarPaginado(@Param("buscar") String buscar, Pageable pageable);

    @Query("SELECT CONCAT(COALESCE(c.nombres, ''), ' ', COALESCE(c.apellidos, ''), COALESCE(c.razonSocial, '')) FROM Cliente c WHERE c.idPersona = :idCliente")
    String obtenerNombreAplanadoPorId(@Param("idCliente") Long idCliente);
}