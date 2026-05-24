package com.hardpc.saas.backendapi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductoRequestDTO {

    @NotBlank(message = "El código SKU es obligatorio")
    @Size(max = 50, message = "El SKU no puede exceder los 50 caracteres")
    private String codigoSku;

    @NotBlank(message = "La descripción del producto es obligatoria")
    @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a cero")
    private BigDecimal precioUsd;

    @NotNull(message = "Los meses de garantía son obligatorios")
    @Min(value = 0, message = "Los meses de garantía no pueden ser negativos")
    private Integer mesesGarantia;

    @NotNull(message = "Debe especificar si es serializado")
    private Boolean esSerializado;

    @Size(max = 255, message = "La URL de la imagen no puede exceder los 255 caracteres")
    private String imagenUrl;

    // --- Relaciones Maestras (Solo IDs) ---
    @NotNull(message = "Debe seleccionar una marca")
    private Long idMarca;

    @NotNull(message = "Debe seleccionar una categoría")
    private Long idCategoria;

    @NotNull(message = "Debe seleccionar una unidad de medida")
    private Long idUnidadMedida;

    private Boolean estado;
}