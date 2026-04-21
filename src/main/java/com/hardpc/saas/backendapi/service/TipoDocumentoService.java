package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.TipoDocumento;
import java.util.List;

public interface TipoDocumentoService {
    List<TipoDocumento> listarTodos();
    TipoDocumento buscarPorId(Long id);
    TipoDocumento crear(TipoDocumento tipoDocumento);
    TipoDocumento actualizar(Long id, TipoDocumento tipoDocumento);
    void eliminar(Long id);
}