package com.hardpc.saas.backendapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "detalles_venta")
public class DetalleVenta extends AuditoriaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetalleVenta;

    @ManyToOne
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id_item_serial", nullable = true)
    private ItemSerial itemSerial;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_venta_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVentaUnitario;

    @Column(precision = 10, scale = 2)
    private BigDecimal descuento;
}