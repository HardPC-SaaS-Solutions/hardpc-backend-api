package com.hardpc.saas.backendapi.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "movimientos_inventario")
public class MovimientoInventario extends AuditoriaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMovimiento;

    private String tipoMovimiento; // ENTRADA o SALIDA

    private int cantidad;

    private String descripcion;

    // Movimiento ocurre en un local
    @ManyToOne
    @JoinColumn(name = "id_local")
    private Local local;

    // Movimiento afecta un item específico
    @ManyToOne
    @JoinColumn(name = "id_item_serial")
    private ItemSerial itemSerial;
}