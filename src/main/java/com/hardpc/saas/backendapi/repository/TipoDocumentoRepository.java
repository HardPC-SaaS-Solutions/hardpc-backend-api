package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.entity.TipoDocumento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdTipoDocumentoNot(String nombre, Long idTipoDocumento);
    boolean existsByAbreviaturaIgnoreCase(String abreviatura);
    boolean existsByAbreviaturaIgnoreCaseAndIdTipoDocumentoNot(String abreviatura, Long idTipoDocumento);
    Optional<TipoDocumento> findByAbreviaturaIgnoreCase(String abreviatura);
    Optional<TipoDocumento> findByNombreIgnoreCase(String nombre);
    Page<TipoDocumento> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
    List<TipoDocumento> findByEstadoTrueOrderByNombreAsc();
}