package com.hardpc.saas.backendapi.repository.transaccional;

import com.hardpc.saas.backendapi.entity.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento,Long> {
}
