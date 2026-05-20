package com.hardpc.saas.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InversionStockDTO {
    private Long idLocal;
    private String nombreLocal;
    private BigDecimal totalInversion;
}