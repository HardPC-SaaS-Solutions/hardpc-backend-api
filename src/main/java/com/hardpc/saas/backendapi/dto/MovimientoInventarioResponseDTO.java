package com.hardpc.saas.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hardpc.saas.backendapi.enums.TipoMovimiento;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonPropertyOrder({
        "idMovimiento", "tipoMovimiento", "fechaHora", "cantidad", "observacion",
        "idProducto", "codigoSkuProducto", "descripcionProducto",
        "idItemSerial", "numeroSerie",
        "idLocalOrigen", "nombreLocalOrigen",
        "idLocalDestino", "nombreLocalDestino",
        "idUsuario", "username"
})
public class MovimientoInventarioResponseDTO {

    private Long idMovimiento;
    private TipoMovimiento tipoMovimiento;
    private LocalDateTime fechaHora;
    private Integer cantidad;
    private String observacion;

    // --- Aplanamiento de Relaciones ---
    private Long idProducto;
    private String codigoSkuProducto;
    private String descripcionProducto;

    private Long idItemSerial;
    private String numeroSerie;

    private Long idLocalOrigen;
    private String nombreLocalOrigen;

    private Long idLocalDestino;
    private String nombreLocalDestino;

    private Long idUsuario;
    private String username;
}