package com.hardpc.saas.backendapi.dto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CajaSesionRequestDTO {
    @NotNull(message = "El local es obligatorio")
    private Long idLocal;

    @NotNull(message = "El monto de apertura es obligatorio")
    @PositiveOrZero(message = "El monto no puede ser negativo")
    private BigDecimal montoApertura;
}