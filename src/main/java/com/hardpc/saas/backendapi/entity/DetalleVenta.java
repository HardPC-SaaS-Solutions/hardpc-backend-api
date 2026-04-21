package com.hardpc.saas.backendapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "detalles_ventas")
public class DetalleVenta extends AuditoriaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetalleVenta;

    @NotNull(message = "La venta es obligatoria")
    @ManyToOne
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    @NotNull(message = "El producto es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    // Puede ser nulo si el producto no es serializado
    @ManyToOne
    @JoinColumn(name = "id_item_serial")
    private ItemSerial itemSerial;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(nullable = false)
    private Integer cantidad;

    @NotNull(message = "El precio de venta unitario es obligatorio")
    @PositiveOrZero(message = "El precio no puede ser negativo")
    @Column(name = "precio_venta_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioVentaUnitario;

    @NotNull(message = "El descuento es obligatorio")
    @PositiveOrZero(message = "El descuento no puede ser negativo")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO; // Valor por defecto
}