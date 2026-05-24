package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.entity.TipoComprobante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoComprobanteRepository extends JpaRepository<TipoComprobante, Long> {
    boolean existsByDescripcionIgnoreCase(String descripcion);
    boolean existsByDescripcionIgnoreCaseAndIdTipoComprobanteNot(String descripcion, Long idTipoComprobante);
    boolean existsByCodigoSunatIgnoreCase(String codigoSunat);
    boolean existsByCodigoSunatIgnoreCaseAndIdTipoComprobanteNot(String codigoSunat, Long idTipoComprobante);
    Page<TipoComprobante> findByDescripcionContainingIgnoreCase(String descripcion, Pageable pageable);
    List<TipoComprobante> findByEstadoTrueOrderByDescripcionAsc();
    Optional<TipoComprobante> findByCodigoSunatIgnoreCase(String codigoSunat);
}