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
@Table(name = "marcas")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Marca extends AuditoriaBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMarca;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String nombre;

    @NotNull
    @Column(unique = true, nullable = false)

    @NotNull
    private Boolean estado;

    private String logoUrl;
}
