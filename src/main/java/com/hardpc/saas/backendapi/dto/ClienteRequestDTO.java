package com.hardpc.saas.backendapi.dto;

import com.hardpc.saas.backendapi.enums.TipoCliente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClienteRequestDTO {

    // --- Datos Heredados (Persona) ---
    @NotNull(message = "El tipo de documento es obligatorio")
    private Long idTipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 20, message = "El documento no puede exceder los 20 caracteres")
    private String numeroDocumento;

    // NOTA: Sin @NotBlank porque su obligatoriedad depende del TipoCliente (Validado en Service)
    @Size(max = 100, message = "Los nombres no pueden exceder los 100 caracteres")
    private String nombres;

    @Size(max = 100, message = "Los apellidos no pueden exceder los 100 caracteres")
    private String apellidos;

    @Size(max = 150, message = "La razón social no puede exceder los 150 caracteres")
    private String razonSocial;

    //@NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres")
    private String telefono;

    //@NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
    private String email;

    @Size(max = 255, message = "La dirección no puede exceder los 255 caracteres")
    private String direccion;

    // --- Datos Propios (Cliente) ---
    @NotNull(message = "El tipo de cliente es obligatorio")
    private TipoCliente tipoCliente;

    private Boolean estado;
}