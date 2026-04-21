package com.hardpc.saas.backendapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @NotNull(message = "El ingreso de compra es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_ingreso", nullable = false)
    private IngresoCompra ingresoCompra;

    @NotNull(message = "El producto es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(nullable = false)
    private Integer cantidad;

    @NotNull(message = "El precio de compra unitario es obligatorio")
    @Positive(message = "El precio debe ser mayor a cero")
    @Column(name = "precio_compra_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioCompraUnitario;
}