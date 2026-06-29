package com.hardpc.saas.backendapi.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopProductoDTO {
    private Long idProducto;
    private String descripcion;
    private Long cantidadVendida;
}