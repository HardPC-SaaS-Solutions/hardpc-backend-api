package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.entity.CajaSesion;
import com.hardpc.saas.backendapi.enums.EstadoCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CajaSesionRepository extends JpaRepository<CajaSesion, Long> {
    // Un cajero solo debería tener una caja abierta a la vez en todo el sistema
    Optional<CajaSesion> findByUsuario_IdPersonaAndEstado(Long idUsuario, EstadoCaja estado);

    boolean existsByUsuario_IdPersonaAndEstado(Long idUsuario, EstadoCaja estado);
}