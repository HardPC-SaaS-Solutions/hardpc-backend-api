package com.hardpc.saas.backendapi.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "locales")
@JsonPropertyOrder({"idLocal", "nombre", "direccion", "telefono", "estado", "fechaCreacion", "fechaActualizacion"})
public class Local extends AuditoriaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLocal;

    @NotBlank(message = "El nombre del local es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 255, message = "La dirección no puede exceder los 255 caracteres")
    @Column(nullable = false, length = 255)
    private String direccion;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres")
    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(nullable = false)
    private Boolean estado = true;

    @Size(max = 255, message = "La URL de la foto no puede exceder los 255 caracteres")
    @Column(name = "foto_portada_url", length = 255)
    private String fotoPortadaUrl;
}