package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.entity.TipoComprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoComprobanteRepository extends JpaRepository<TipoComprobante, Long> {
}
