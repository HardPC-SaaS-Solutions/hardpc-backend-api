package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.entity.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocalRepository extends JpaRepository<Local, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdLocalNot(String nombre, Long idLocal);
    boolean existsByDireccionIgnoreCase(String direccion);
    boolean existsByDireccionIgnoreCaseAndIdLocalNot(String direccion, Long idLocal);
    Page<Local> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
    List<Local> findByEstadoTrueOrderByNombreAsc();
    Optional<Local> findByNombreIgnoreCase(String nombre);
}