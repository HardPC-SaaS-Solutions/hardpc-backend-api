package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.entity.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {
}