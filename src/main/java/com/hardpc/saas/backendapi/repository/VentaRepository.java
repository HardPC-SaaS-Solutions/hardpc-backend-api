package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
}