package com.hardpc.saas.backendapi.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

@Data
@Entity
@Table(name = "categorias")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Categoria extends AuditoriaBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategoria;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String nombre;

    private String descripcion;

    @NotNull
    private Boolean estado;

    private String iconoUrl;
}