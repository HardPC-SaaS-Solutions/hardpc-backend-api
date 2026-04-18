package com.hardpc.saas.backendapi.entity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ingresos_compras")
public class IngresoCompra extends AuditoriaBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idIngreso;
}
