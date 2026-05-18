package com.hardpc.saas.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StockMinimoDTO {

    private String local;
    private String producto;
    private Integer stockActual;
    private Integer stockMinimo;
}
