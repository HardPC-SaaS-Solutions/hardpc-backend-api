package com.hardpc.saas.backendapi.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
public class ProductoBusquedaAlmacenDTO {
    private Long idProducto;
    private String codigoSku;
    private String descripcion;

    private String marca;
    private String categoria;

    private BigDecimal precioUsd;

    private Integer  stockActual;
    private Integer  stockMinimo;
    private Long idLocal;

}
