package com.hardpc.saas.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@JsonPropertyOrder({
        "id", "codigoSku", "descripcion", "precioUsd", "mesesGarantia", "esSerializado",
        "idMarca", "nombreMarca", "idCategoria", "nombreCategoria",
        "idUnidadMedida", "descripcionUnidadMedida", "imagenUrl", "estado"
})
public class ProductoResponseDTO {

    // Mapeado desde idProducto
    private Long id;

    // --- Aplanamiento de Relaciones Maestras ---
    // REGLA DE ORO: Evitamos objetos anidados para no saturar al frontend
    private Long idMarca;
    private String nombreMarca;

    private Long idCategoria;
    private String nombreCategoria;

    private Long idUnidadMedida;
    private String descripcionUnidadMedida;

    // --- Datos Planos ---
    private String codigoSku;
    private String descripcion;
    private BigDecimal precioUsd;
    private Integer mesesGarantia;
    private Boolean esSerializado;
    private String imagenUrl;
    private Boolean estado;
}