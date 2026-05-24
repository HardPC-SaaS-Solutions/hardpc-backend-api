package com.hardpc.saas.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hardpc.saas.backendapi.enums.Condicion;
import com.hardpc.saas.backendapi.enums.EstadoDisponibilidad;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonPropertyOrder({
        "id", "idProducto", "codigoSkuProducto", "descripcionProducto",
        "idLocal", "nombreLocal", "numeroSerie", "condicion",
        "estadoDisponibilidad", "fechaFinGarantia", "idDetalleIngreso"
})
public class ItemSerialResponseDTO {

    private Long id;

    private Long idProducto;
    private String codigoSkuProducto;
    private String descripcionProducto;

    private Long idLocal;
    private String nombreLocal;

    private String numeroSerie;
    private Condicion condicion;
    private EstadoDisponibilidad estadoDisponibilidad;
    private LocalDateTime fechaFinGarantia;

    private Long idDetalleIngreso;
}