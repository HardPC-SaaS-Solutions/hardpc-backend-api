package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.entity.Marca;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdMarcaNot(String nombre, Long idMarca);
    Page<Marca> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
    List<Marca> findByEstadoTrueOrderByNombreAsc();
    Optional<Marca> findByNombreIgnoreCase(String nombre);
}