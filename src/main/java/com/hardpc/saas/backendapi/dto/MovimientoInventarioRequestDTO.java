package com.hardpc.saas.backendapi.dto;

import com.hardpc.saas.backendapi.enums.TipoMovimiento;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MovimientoInventarioRequestDTO {

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private TipoMovimiento tipoMovimiento;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @NotNull(message = "El producto es obligatorio")
    private Long idProducto;

    @NotNull(message = "El usuario responsable es obligatorio")
    private Long idUsuario;

    // --- Campos Opcionales (Validados dinámicamente en el Service) ---
    private Long idLocalOrigen;
    private Long idLocalDestino;
    private Long idItemSerial;

    private String observacion;
}