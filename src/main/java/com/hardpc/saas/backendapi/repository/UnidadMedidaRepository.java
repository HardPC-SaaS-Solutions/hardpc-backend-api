package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.entity.UnidadMedida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnidadMedidaRepository extends JpaRepository<UnidadMedida, Long> {
}