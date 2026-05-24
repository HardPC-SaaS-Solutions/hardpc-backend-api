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
@Table(name = "tipos_documento")
@JsonPropertyOrder({"idTipoDocumento", "nombre", "longitudExacta", "estado", "fechaCreacion", "fechaActualizacion"})
public class TipoDocumento extends AuditoriaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipoDocumento;

    @NotBlank(message = "El nombre del tipo de documento es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder los 50 caracteres")
    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    @NotBlank(message = "La abreviatura es obligatoria")
    @Size(min = 2, max = 10, message = "La abreviatura debe tener entre 2 y 10 caracteres")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "La abreviatura solo debe contener letras mayúsculas y números (ej. DNI, RUC)")
    @Column(nullable = false, unique = true, length = 10)
    private String abreviatura;

    @NotNull(message = "La longitud exacta es obligatoria")
    @Min(value = 1, message = "La longitud debe ser al menos 1")
    @Max(value = 20, message = "La longitud no puede ser mayor a 20")
    @Column(name = "longitud_exacta", nullable = false)
    private Integer longitudExacta;

    @Column(nullable = false)
    private Boolean estado = true;
}