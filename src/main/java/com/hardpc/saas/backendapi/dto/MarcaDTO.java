package com.hardpc.saas.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@JsonPropertyOrder({"id", "nombre", "logoUrl", "estado"})
public class MarcaDTO {
    private Long id;

    @NotBlank(message = "El nombre de la marca es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String nombre;

    private Boolean estado;

    @Size(max = 500, message = "La URL del logo no puede exceder los 500 caracteres")
    private String logoUrl;
}