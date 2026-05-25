package com.hardpc.saas.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GastoProveedorDTO {
    private Long idProveedor;
    private String razonSocialProveedor;
    private BigDecimal totalGasto;
    private Long cantidadCompras;
}