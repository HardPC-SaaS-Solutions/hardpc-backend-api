package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.entity.Rol;
import com.hardpc.saas.backendapi.enums.RolNombre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    boolean existsByNombre(RolNombre nombre);
    boolean existsByNombreAndIdRolNot(RolNombre nombre, Long idRol);
    Optional<Rol> findByNombre(RolNombre nombre);
    Page<Rol> findByDescripcionContainingIgnoreCase(String descripcion, Pageable pageable);
    List<Rol> findByEstadoTrueOrderByNombreAsc();
}