package com.hardpc.saas.backendapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CajaCierreRequestDTO {
    @NotNull(message = "El monto de cierre en efectivo es obligatorio")
    @PositiveOrZero(message = "El monto no puede ser negativo")
    private BigDecimal montoCierreEfectivoReal;
}