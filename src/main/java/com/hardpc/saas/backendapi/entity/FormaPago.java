package com.hardpc.saas.backendapi.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "formas_pago")
@JsonPropertyOrder({"idFormaPago", "descripcion", "estado", "fechaCreacion", "fechaActualizacion"})
public class FormaPago extends AuditoriaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFormaPago;

    @NotBlank(message = "La descripción de la forma de pago es obligatoria")
    @Size(max = 50, message = "La descripción no puede exceder los 50 caracteres")
    @Column(nullable = false, unique = true, length = 50)
    private String descripcion;

    @NotNull(message = "El estado es obligatorio")
    @Column(nullable = false)
    private Boolean estado = true;
}