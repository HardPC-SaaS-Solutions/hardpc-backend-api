package com.hardpc.saas.backendapi.entity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "ingresos_compras")
public class IngresoCompra extends AuditoriaBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idIngreso;

    @NotBlank
    @Size(max = 10)
    @Column(nullable = false, length = 10)
    private String serieComprobante;

    @NotNull
    @DecimalMin("0.0") /*valor mayor o igual a este minimo*/
    @Column(nullable = false,precision = 10,scale = 2) /*precision: numero maximo de digitos/ scale: numeros a la derecha del punto decimal*/
    private BigDecimal impuesto;

    @NotNull
    @DecimalMin("0.0") /*valor mayor o igual a este minimo*/
    @Column(nullable = false,precision = 12,scale = 2) /*precision: numero maximo de digitos/ scale: numeros a la derecha del punto decimal*/
    private BigDecimal totalCompra;

    @NotBlank
    @Size (max=20)
    @Column(nullable = false,length = 20)
    private String estadoIngreso;

    /*RELACIONES*/

    /*Muchas compras (IngresoCompra) pueden pertenecer a un solo proveedor*/
    @ManyToOne
    @JoinColumn(name="id_proveedor",nullable = false)
    private Proveedor proveedor;

    /*COLECCIONES*/

    /*Un IngresoCompra tiene muchos DetalleIngreso*/
    @OneToMany(mappedBy = "ingresoCompra", cascade = CascadeType.ALL, orphanRemoval = true)/*CascadeType.ALL: EL CRUD se propagara*/
    @JsonManagedReference                                                                 /*a las entidades hijas, OrphanRemoval:si un hijo se elimina de la coleccion del padre, se elimina de la db*/
    private List<DetalleIngreso> detalles;
}
