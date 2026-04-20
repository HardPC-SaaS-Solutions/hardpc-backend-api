package com.hardpc.saas.backendapi.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "unidades_medida")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnidadMedida extends AuditoriaBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUnidadMedida;

    @NotBlank
    private String descripcion;

    @NotBlank
    private String abreviatura;

    @NotNull
    private Boolean estado;
}