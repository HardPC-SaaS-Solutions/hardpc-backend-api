package com.hardpc.saas.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonPropertyOrder({"id", "descripcion", "estado"})
public class FormaPagoDTO {
    private Long id;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 50, message = "La descripción no puede exceder los 50 caracteres")
    private String descripcion;

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;
}