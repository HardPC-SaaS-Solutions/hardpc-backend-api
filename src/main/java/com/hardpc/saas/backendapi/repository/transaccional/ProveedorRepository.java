package com.hardpc.saas.backendapi.repository.transaccional;

import com.hardpc.saas.backendapi.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<Proveedor,Long> {
}
