package com.hardpc.saas.backendapi.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hardpc.saas.backendapi.enums.RolNombre;
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
@Table(name = "roles")
@JsonPropertyOrder({"idRol", "nombre", "descripcion", "estado", "fechaCreacion", "fechaActualizacion"})
public class Rol extends AuditoriaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRol;

    @NotNull(message = "El nombre del rol es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 50)
    private RolNombre nombre;

    @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres")
    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    private Boolean estado = true;
}