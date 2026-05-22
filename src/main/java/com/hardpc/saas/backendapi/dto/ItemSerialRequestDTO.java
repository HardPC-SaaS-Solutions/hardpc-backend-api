package com.hardpc.saas.backendapi.dto;

import com.hardpc.saas.backendapi.enums.Condicion;
import com.hardpc.saas.backendapi.enums.EstadoDisponibilidad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemSerialRequestDTO {

    @NotNull(message = "El producto es obligatorio")
    private Long idProducto;

    @NotNull(message = "El local es obligatorio")
    private Long idLocal;

    @NotBlank(message = "El número de serie es obligatorio")
    private String numeroSerie;

    @NotNull(message = "La condición del producto es obligatoria")
    private Condicion condicion;

    private EstadoDisponibilidad estadoDisponibilidad;

    private LocalDateTime fechaFinGarantia;

    // Es opcional porque un serial puede ingresarse manualmente por un ajuste de inventario inicial
    // y no necesariamente a través de una compra formal (DetalleIngreso)
    private Long idDetalleIngreso;
}