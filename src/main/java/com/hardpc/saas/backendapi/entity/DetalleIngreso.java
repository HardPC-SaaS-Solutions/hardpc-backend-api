package com.hardpc.saas.backendapi.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "detalles_ingresos")
public class DetalleIngreso extends AuditoriaBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetalleIngreso;

    @NotNull
    @Column(nullable = false)
    private Long idProducto; //Version simple a cambiar ------------------------------------------------

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer cantidad;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCompraUnitario;

    /*RELACIONES*/

    /*Muchos DetalleIngreso pertenecen a un solo IngresoCompra*/
    @ManyToOne(fetch = FetchType.LAZY)/*cuando carga detalle ingreso,
    AUTOMATICAMENTE carga IngresoCompra, genera lentitud LAZY evita eso*/
    @JoinColumn(name = "id_ingreso",nullable = false)
    @JsonBackReference
    private IngresoCompra ingresoCompra;


}
