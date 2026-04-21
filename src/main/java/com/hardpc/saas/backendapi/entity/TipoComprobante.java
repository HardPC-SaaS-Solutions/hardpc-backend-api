package com.hardpc.saas.backendapi.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tipos_comprobante")
public class TipoComprobante extends AuditoriaBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipoComprobante;

    @Column(nullable = false, length = 100)
    private String descripcion;

    @Column(name = "codigo_sunat", nullable = false, length = 10)
    private String codigoSunat;

    @Column(nullable = false)
    private Boolean estado = true;
}