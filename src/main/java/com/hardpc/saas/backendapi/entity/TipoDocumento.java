package com.hardpc.saas.backendapi.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tipos_documento")
public class TipoDocumento extends AuditoriaBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipoDocumento;

    @NotBlank
    @Size(max=50)
    @Column(nullable = false, length = 50)
    private String nombre;

    @NotNull
    @Min(1)
    @Max(20)
    @Column(nullable = false)
    private Integer longitudExacta;

    @NotNull
    @Column(nullable = false)
    private Boolean estado;
}