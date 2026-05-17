package com.hardpc.saas.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@JsonPropertyOrder({"id", "nombre", "longitudExacta", "estado"})
public class TipoDocumentoDTO {
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder los 50 caracteres")
    private String nombre;

    @NotNull(message = "La longitud exacta es obligatoria")
    @Min(value = 1, message = "La longitud debe ser al menos 1")
    @Max(value = 20, message = "La longitud no puede ser mayor a 20")
    private Integer longitudExacta;

    private Boolean estado;
}