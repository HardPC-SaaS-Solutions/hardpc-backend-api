package com.hardpc.saas.backendapi.service.transaccional;

import com.hardpc.saas.backendapi.entity.TipoDocumento;

import java.util.List;

public interface TipoDocumentoService {

    TipoDocumento guardarTipo(TipoDocumento tipoDocumento);

    List<TipoDocumento> listarTipoDocumentos();

    TipoDocumento buscarPorId(Long id);

    TipoDocumento actualizarTipoDocumento(Long id, TipoDocumento tipoDocumento);

    void eliminarTipoDocumento(Long id);

}
