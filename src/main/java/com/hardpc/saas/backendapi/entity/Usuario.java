package com.hardpc.saas.backendapi.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
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
@Table(name = "usuarios")
@PrimaryKeyJoinColumn(name = "id_usuario") // Vincula el ID con la tabla padre 'personas'
@JsonPropertyOrder({
        "idPersona", "numeroDocumento", "tipoDocumento", "nombres", "apellidos",
        "razonSocial", "telefono", "email", "direccion", "estado",
        "username", "password", "rol", "avatarUrl",
        "fechaCreacion", "fechaActualizacion"
})
public class Usuario extends Persona {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 50, message = "El nombre de usuario no puede exceder los 50 caracteres")
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(max = 255, message = "La contraseña no puede exceder los 255 caracteres")
    @Column(nullable = false, length = 255)
    private String password;

    @NotNull(message = "El rol es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @Size(max = 255, message = "La URL del avatar no puede exceder los 255 caracteres")
    @Column(name = "avatar_url", length = 255) // Nullable por defecto
    private String avatarUrl;
}