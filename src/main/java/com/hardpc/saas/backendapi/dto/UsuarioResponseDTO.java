package com.hardpc.saas.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hardpc.saas.backendapi.enums.RolNombre;
import lombok.Data;

@Data
@JsonPropertyOrder({
        "id", "numeroDocumento", "idTipoDocumento", "abreviaturaTipoDocumento",
        "nombres", "apellidos", "telefono", "email", "direccion",
        "username", "idRol", "nombreRol", "avatarUrl", "estado"
})
public class UsuarioResponseDTO {

    // Mapeado desde idPersona
    private Long id;

    // --- Aplanamiento de Relaciones ---
    private Long idTipoDocumento;
    private String abreviaturaTipoDocumento;
    private Long idRol;
    private RolNombre nombreRol; // El Enum mantiene la semántica en el JSON de salida

    // --- Datos Planos ---
    private String numeroDocumento;
    private String nombres;
    private String apellidos;
    //private String razonSocial;
    private String telefono;
    private String email;
    private String direccion;
    private String username;
    private String avatarUrl;
    private Boolean estado;

    // REGLA DE ORO APLICADA: No existe campo 'password'.
}