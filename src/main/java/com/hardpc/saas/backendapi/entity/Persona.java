package com.hardpc.saas.backendapi.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "personas")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonPropertyOrder({"idPersona", "numeroDocumento", "tipoDocumento", "nombres", "apellidos", "razonSocial", "telefono", "email", "direccion", "estado", "fechaCreacion", "fechaActualizacion"})
public abstract class Persona extends AuditoriaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPersona;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 20, message = "El documento no puede exceder los 20 caracteres")
    @Column(name = "numero_documento", nullable = false, unique = true, length = 20)
    private String numeroDocumento;

    @ManyToOne
    @JoinColumn(name = "id_tipo_documento", nullable = false)
    private TipoDocumento tipoDocumento;

    //@NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100, message = "Los nombres no pueden exceder los 100 caracteres")
    @Column(length = 100)
    private String nombres;

    @Size(max = 100, message = "Los apellidos no pueden exceder los 100 caracteres")
    @Column(length = 100)
    private String apellidos;

    @Size(max = 150, message = "La razón social no puede exceder los 150 caracteres")
    @Column(name = "razon_social", length = 150)
    private String razonSocial;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres")
    @Column(nullable = false, length = 20)
    private String telefono;

    @Email(message = "El formato del email no es válido")
    @NotBlank(message = "El email es obligatorio")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Size(max = 255, message = "La dirección no puede exceder los 255 caracteres")
    @Column(length = 255)
    private String direccion;

    @NotNull
    @Column(nullable = false)
    private Boolean estado = true;
}