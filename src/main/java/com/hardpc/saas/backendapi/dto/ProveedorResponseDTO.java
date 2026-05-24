package com.hardpc.saas.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({
        "id", "ruc", "razonSocial", "nombreComercial",
        "direccion", "telefono", "email", "estado"
})
public class ProveedorResponseDTO {

    // Mapeado desde idProveedor
    private Long id;

    private String ruc;
    private String razonSocial;
    private String nombreComercial;
    private String direccion;
    private String telefono;
    private String email;
    private Boolean estado;
}