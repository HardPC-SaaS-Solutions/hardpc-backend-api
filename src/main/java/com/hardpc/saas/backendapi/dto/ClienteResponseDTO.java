package com.hardpc.saas.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hardpc.saas.backendapi.enums.TipoCliente;
import lombok.Data;

@Data
@JsonPropertyOrder({
        "id", "numeroDocumento", "idTipoDocumento", "abreviaturaTipoDocumento",
        "tipoCliente", "nombres", "apellidos", "razonSocial",
        "telefono", "email", "direccion", "estado"
})
public class ClienteResponseDTO {

    // Mapeado desde idPersona
    private Long id;

    // --- Aplanamiento de Relaciones ---
    private Long idTipoDocumento;
    private String abreviaturaTipoDocumento;

    // --- Datos Planos ---
    private String numeroDocumento;
    private String nombres;
    private String apellidos;
    private String razonSocial;
    private String telefono;
    private String email;
    private String direccion;
    private TipoCliente tipoCliente;
    private Boolean estado;
}