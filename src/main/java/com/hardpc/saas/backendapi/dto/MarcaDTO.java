package com.hardpc.saas.backendapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MarcaDTO {
    private Long id;

    @NotBlank(message = "El nombre de la marca es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String nombre;

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;

    @Size(max = 500, message = "La URL del logo no puede exceder los 500 caracteres")
    private String logoUrl;
}