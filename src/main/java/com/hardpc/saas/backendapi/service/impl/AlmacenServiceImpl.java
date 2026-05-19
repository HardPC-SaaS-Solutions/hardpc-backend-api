package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.dto.ProductoBusquedaAlmacenDTO;
import com.hardpc.saas.backendapi.repository.ProductoRepository;
import com.hardpc.saas.backendapi.service.AlmacenService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlmacenServiceImpl implements AlmacenService {

    private final ProductoRepository productoRepository;

    public AlmacenServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }
    @Override
    public List<ProductoBusquedaAlmacenDTO> buscarProductos(
            Long idLocal,
            String sku,
            String marca,
            String categoria,
            String descripcion
    ) {

        return productoRepository.buscarPorLocal(
                idLocal,
                sku,
                marca,
                categoria,
                descripcion
        );
    }


}
