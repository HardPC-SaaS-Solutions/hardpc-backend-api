package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.dto.ProductoBusquedaAlmacenDTO;
import com.hardpc.saas.backendapi.service.AlmacenService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/almacen")
public class AlmacenController {

    private final AlmacenService almacenService;

    public AlmacenController(AlmacenService almacenService) {
        this.almacenService = almacenService;
    }

    @GetMapping("/buscar-productos")
    public List<ProductoBusquedaAlmacenDTO> buscarProductos(
            @RequestParam Long idLocal,
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String descripcion
    ) {

        return almacenService.buscarProductos(
                idLocal,
                sku,
                marca,
                categoria,
                descripcion
        );
    }
}
