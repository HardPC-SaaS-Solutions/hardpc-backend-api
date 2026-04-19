package com.hardpc.saas.backendapi.repository.transaccional;

import com.hardpc.saas.backendapi.entity.IngresoCompra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngresoCompraRepository extends JpaRepository<IngresoCompra,Long> {
}
