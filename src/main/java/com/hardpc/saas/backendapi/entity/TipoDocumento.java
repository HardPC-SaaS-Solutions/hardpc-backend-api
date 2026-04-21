package com.hardpc.saas.backendapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tipos_documento")
public class TipoDocumento extends AuditoriaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipoDocumento;

    @NotBlank(message = "El nombre del tipo de documento es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String nombre;

    @NotNull(message = "La longitud exacta es obligatoria")
    @Min(value = 1, message = "La longitud debe ser al menos 1")
    @Max(value = 20, message = "La longitud no puede ser mayor a 20")
    @Column(name = "longitud_exacta", nullable = false)
    private Integer longitudExacta;

    @Column(nullable = false)
    private Boolean estado = true;
}