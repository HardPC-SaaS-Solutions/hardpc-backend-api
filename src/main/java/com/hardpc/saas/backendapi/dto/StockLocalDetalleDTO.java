package com.hardpc.saas.backendapi.dto;


import lombok.Data;

import java.util.List;

@Data
public class StockLocalDetalleDTO {

    private Long idProducto;
    private String codigoSku;
    private String descripcionProducto;

    private Integer cantidadActual;
    private Integer stockMinimo;

    private Boolean esSerializado;

    private List<SerialDisponibleDTO> seriales;
}
