package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.entity.FormaPago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormaPagoRepository extends JpaRepository<FormaPago, Long> {
    boolean existsByDescripcionIgnoreCase(String descripcion);
    boolean existsByDescripcionIgnoreCaseAndIdFormaPagoNot(String descripcion, Long idFormaPago);
    Page<FormaPago> findByDescripcionContainingIgnoreCase(String descripcion, Pageable pageable);
    List<FormaPago> findByEstadoTrueOrderByDescripcionAsc();
    Optional<FormaPago> findByDescripcionIgnoreCase(String descripcion);
}