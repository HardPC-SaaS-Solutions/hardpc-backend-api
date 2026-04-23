package com.hardpc.saas.backendapi.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "clientes")
@PrimaryKeyJoinColumn(name = "id_cliente") // Vincula el ID con la tabla padre 'personas'
@JsonPropertyOrder({
        "idPersona", "numeroDocumento", "tipoDocumento", "nombres", "apellidos",
        "razonSocial", "telefono", "email", "direccion", "estado",
        "tipoCliente",
        "fechaCreacion", "fechaActualizacion"
})
public class Cliente extends Persona {

    @NotBlank(message = "El tipo de cliente es obligatorio")
    @Pattern(regexp = "^(MAYORISTA|MINORISTA)$", message = "El tipo de cliente debe ser MAYORISTA o MINORISTA")
    @Column(name = "tipo_cliente", nullable = false, length = 20)
    private String tipoCliente;
}