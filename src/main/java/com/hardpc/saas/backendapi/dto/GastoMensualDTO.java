package com.hardpc.saas.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GastoMensualDTO {
    private Integer anio;
    private Integer mes;
    private BigDecimal totalGasto;
    private Long cantidadCompras;
}