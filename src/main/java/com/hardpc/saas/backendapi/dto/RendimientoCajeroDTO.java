package com.hardpc.saas.backendapi.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RendimientoCajeroDTO {
    private Long idUsuario;
    private String username;
    private BigDecimal totalVendido;
    private Long cantidadTransacciones;
}