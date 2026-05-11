package com.hardpc.saas.backendapi.dto;

import com.hardpc.saas.backendapi.enums.RolNombre;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RolDTO {
    private Long id;

    @NotNull(message = "El nombre del rol es obligatorio")
    private RolNombre nombre;

    @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres")
    private String descripcion;

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;
}