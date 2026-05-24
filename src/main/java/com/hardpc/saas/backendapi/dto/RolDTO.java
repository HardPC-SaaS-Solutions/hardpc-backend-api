package com.hardpc.saas.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hardpc.saas.backendapi.enums.RolNombre;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@JsonPropertyOrder({"id", "nombre", "descripcion", "estado"})
public class RolDTO {
    private Long id;

    @NotNull(message = "El nombre del rol es obligatorio")
    private RolNombre nombre;

    @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres")
    private String descripcion;

    private Boolean estado;
}