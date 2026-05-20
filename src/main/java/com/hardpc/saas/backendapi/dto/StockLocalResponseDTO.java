package com.hardpc.saas.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({
        "id", "idProducto", "codigoSkuProducto", "descripcionProducto",
        "idLocal", "nombreLocal", "cantidadActual", "stockMinimo"
})
public class StockLocalResponseDTO {

    private Long id;

    private Long idProducto;
    private String codigoSkuProducto;
    private String descripcionProducto;

    private Long idLocal;
    private String nombreLocal;

    private Integer cantidadActual;
    private Integer stockMinimo;
}