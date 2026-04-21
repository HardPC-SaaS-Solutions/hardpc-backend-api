package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.entity.DetalleIngreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleIngresoRepository extends JpaRepository<DetalleIngreso, Long> {
}