package com.hardpc.saas.backendapi.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "unidades_medida")
@JsonPropertyOrder({"idUnidadMedida", "descripcion", "abreviatura", "estado", "fechaCreacion", "fechaActualizacion"})
public class UnidadMedida extends AuditoriaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUnidadMedida;

    @NotBlank(message = "La descripción de la unidad es obligatoria")
    @Size(max = 100, message = "La descripción no puede exceder los 100 caracteres")
    @Column(nullable = false, length = 100, unique = true)
    private String descripcion;

    @NotBlank(message = "La abreviatura es obligatoria")
    @Size(max = 10, message = "La abreviatura no puede exceder los 10 caracteres")
    @Column(nullable = false, length = 10)
    private String abreviatura;

    @Column(nullable = false)
    private Boolean estado = true;
}