package com.hardpc.saas.backendapi.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UsuarioRequestDTO {

    // --- Datos Heredados (Persona) ---
    @NotNull(message = "El tipo de documento es obligatorio")
    private Long idTipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 20, message = "El documento no puede exceder los 20 caracteres")
    private String numeroDocumento;

    @Size(max = 100, message = "Los nombres no pueden exceder los 100 caracteres")
    private String nombres;

    @Size(max = 100, message = "Los apellidos no pueden exceder los 100 caracteres")
    private String apellidos;

    //@Size(max = 150, message = "La razón social no puede exceder los 150 caracteres")
    //private String razonSocial;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres")
    private String telefono;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
    private String email;

    @Size(max = 255, message = "La dirección no puede exceder los 255 caracteres")
    private String direccion;

    // --- Datos de Seguridad y Dominio (Usuario) ---
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 50, message = "El nombre de usuario no puede exceder los 50 caracteres")
    private String username;

    // Nota Arquitectónica: Es opcional en el DTO porque en un UPDATE(PUT) el admin podría no querer cambiarla.
    // La obligación estricta al crear (POST) la manejaremos en la capa Service.
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,20}$|^$",
            message = "La contraseña debe tener entre 8 y 20 caracteres, incluir al menos un número, una mayúscula y una minúscula")
    @Size(max = 255, message = "La contraseña no puede exceder los 255 caracteres")
    private String password;

    @NotNull(message = "El rol es obligatorio")
    private Long idRol;

    @Size(max = 255, message = "La URL del avatar no puede exceder los 255 caracteres")
    private String avatarUrl;

    // El estado es opcional en el request; el backend lo fuerza a TRUE en la creación.
    private Boolean estado;
}