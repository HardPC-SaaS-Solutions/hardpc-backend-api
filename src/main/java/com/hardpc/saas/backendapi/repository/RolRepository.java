package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.entity.Rol;
import com.hardpc.saas.backendapi.enums.RolNombre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(RolNombre nombre);
}