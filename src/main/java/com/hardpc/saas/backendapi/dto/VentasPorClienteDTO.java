package com.hardpc.saas.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentasPorClienteDTO {
    private Long idCliente;
    private String identificacionCliente;
    private String nombreAplanado; // Concatena Nombres/Apellidos o Razón Social
    private BigDecimal totalComprado;
    private Long cantidadVentas;
}