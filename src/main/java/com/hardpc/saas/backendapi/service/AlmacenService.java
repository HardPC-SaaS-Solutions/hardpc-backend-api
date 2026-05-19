package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.dto.ProductoBusquedaAlmacenDTO;

import java.util.List;

public interface AlmacenService {
    List<ProductoBusquedaAlmacenDTO> buscarProductos(
            Long idLocal,
            String sku,
            String marca,
            String categoria,
            String descripcion
    );
}
