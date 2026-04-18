package com.hardpc.saas.backendapi.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "stocks_locales")
public class StockLocal extends AuditoriaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idStockLocal;

    private int cantidad;

    // Cada stock pertenece a un local
    @ManyToOne
    @JoinColumn(name = "id_local")
    private Local local;

    // Relación con el item (producto serializado)
    @ManyToOne
    @JoinColumn(name = "id_item_serial")
    private ItemSerial itemSerial;
}