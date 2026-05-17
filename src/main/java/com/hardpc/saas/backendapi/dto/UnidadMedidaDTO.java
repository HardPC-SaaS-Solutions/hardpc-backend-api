package com.hardpc.saas.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@JsonPropertyOrder({"id", "descripcion", "abreviatura", "estado"})
public class UnidadMedidaDTO {
    private Long id;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 100, message = "La descripción no puede exceder los 100 caracteres")
    private String descripcion;

    @NotBlank(message = "La abreviatura es obligatoria")
    @Size(max = 10, message = "La abreviatura no puede exceder los 10 caracteres")
    private String abreviatura;

    private Boolean estado;
}