package com.hardpc.saas.backendapi.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name = "proveedores")
public class Proveedor extends AuditoriaBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProveedor;

    @NotBlank
    @Pattern(regexp = "\\d{11}", message = "El ruc debe tener exactamente 11 digitos")
    @Column(nullable = false,unique = true,length = 11)
    private String ruc;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String razonSocial;

    @Size(max=100)
    @Column(length = 100)
    private String nombreComercial;

    @Size(max = 150)
    @Column(length = 150)
    private String direccion;

    @Size(max = 20)
    @Column(length = 20)
    private String telefono;

    @Email
    @Size(max = 100)
    @Column(length = 100)
    private String email;

    @NotNull
    @Column(nullable = false)
    private Boolean estado;
}