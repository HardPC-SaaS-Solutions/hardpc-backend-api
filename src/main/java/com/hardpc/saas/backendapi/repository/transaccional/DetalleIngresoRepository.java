package com.hardpc.saas.backendapi.repository.transaccional;

import com.hardpc.saas.backendapi.entity.DetalleIngreso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleIngresoRepository extends JpaRepository<DetalleIngreso, Long> {
}
