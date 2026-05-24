package com.hardpc.saas.backendapi.dto;

import com.hardpc.saas.backendapi.enums.EstadoDisponibilidad;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumenEstadoSerialDTO {
    private Long idLocal;
    private String nombreLocal;
    private EstadoDisponibilidad estadoDisponibilidad;
    private Long cantidad;
}