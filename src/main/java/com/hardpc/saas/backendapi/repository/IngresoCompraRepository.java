package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.entity.IngresoCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngresoCompraRepository extends JpaRepository<IngresoCompra, Long> {
}