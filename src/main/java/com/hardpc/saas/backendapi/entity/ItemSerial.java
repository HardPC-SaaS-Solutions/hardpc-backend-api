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

    // TODO: Agregar fk id_detalle_ingreso para rastrear garantia con proveedor
}