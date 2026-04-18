package com.hardpc.saas.backendapi.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "items_seriales")
public class ItemSerial extends AuditoriaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idItemSerial;

    private String codigoSerial;

    // RELACIÓN: muchos items pueden estar en un Local
    @ManyToOne
    @JoinColumn(name = "id_local")
    private Local local;

    // FUTURO: relación con detalle ingreso (proveedor / compra)
    @ManyToOne
    @JoinColumn(name = "id_detalle_ingreso")
    private Object detalleIngreso; // luego lo cambian por entidad real
}