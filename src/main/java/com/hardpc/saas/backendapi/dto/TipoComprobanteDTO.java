package com.hardpc.saas.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonPropertyOrder({"id", "descripcion", "codigoSunat", "estado"})
public class TipoComprobanteDTO {
    private Long id;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 100, message = "La descripción no puede exceder los 100 caracteres")
    private String descripcion;

    @NotBlank(message = "El código SUNAT es obligatorio")
    @Size(max = 20, message = "El código SUNAT no puede exceder los 20 caracteres")
    private String codigoSunat;

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;
}