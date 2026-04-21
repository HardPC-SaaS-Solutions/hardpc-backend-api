package com.hardpc.saas.backendapi.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "formas_pago")
public class FormaPago extends AuditoriaBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFormaPago;

    @Column(nullable = false, length = 50)
    private String descripcion;

    @Column(nullable = false)
    private Boolean estado = true;
}