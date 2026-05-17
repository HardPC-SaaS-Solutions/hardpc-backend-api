package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.entity.UnidadMedida;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnidadMedidaRepository extends JpaRepository<UnidadMedida, Long> {
    boolean existsByDescripcionIgnoreCase(String descripcion);
    boolean existsByAbreviaturaIgnoreCase(String abreviatura);
    boolean existsByDescripcionIgnoreCaseAndIdUnidadMedidaNot(String descripcion, Long idUnidadMedida);
    boolean existsByAbreviaturaIgnoreCaseAndIdUnidadMedidaNot(String abreviatura, Long idUnidadMedida);
    Page<UnidadMedida> findByDescripcionContainingIgnoreCase(String descripcion, Pageable pageable);
    List<UnidadMedida> findByEstadoTrueOrderByDescripcionAsc();
    Optional<UnidadMedida> findByDescripcionIgnoreCase(String descripcion);
    Optional<UnidadMedida> findByAbreviaturaIgnoreCase(String abreviatura);
}