package com.hardpc.saas.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class InversionStockDTO {

    private String local;
    private BigDecimal totalInvertido;
}
