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
@Table(name = "tipos_comprobante")
@JsonPropertyOrder({"idTipoComprobante", "descripcion", "codigoSunat", "estado", "fechaCreacion", "fechaActualizacion"})
public class TipoComprobante extends AuditoriaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipoComprobante;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 100, message = "La descripción no puede exceder los 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String descripcion;

    @NotBlank(message = "El código SUNAT es obligatorio")
    @Size(max = 20, message = "El código SUNAT no puede exceder los 20 caracteres")
    @Column(name = "codigo_sunat", nullable = false, unique = true, length = 20)
    private String codigoSunat;

    @NotNull(message = "El estado es obligatorio")
    @Column(nullable = false)
    private Boolean estado = true;
}