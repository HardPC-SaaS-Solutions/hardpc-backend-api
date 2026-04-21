package com.hardpc.saas.backendapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "items_seriales")
public class ItemSerial extends AuditoriaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idItemSerial;

    @NotBlank(message = "El número de serie es obligatorio")
    @Size(max = 100, message = "El número de serie no puede exceder los 100 caracteres")
    @Column(name = "numero_serie", nullable = false, unique = true, length = 100)
    private String numeroSerie;

    @NotBlank(message = "La condición es obligatoria")
    @Column(nullable = false, length = 50)
    private String condicion; // Ejemplo: NUEVO, USADO, REACONDICIONADO

    @NotBlank(message = "El estado de disponibilidad es obligatorio")
    @Column(name = "estado_disponibilidad", nullable = false, length = 50)
    private String estadoDisponibilidad; // Ejemplo: DISPONIBLE, VENDIDO, RESERVADO

    @Column(name = "fecha_fin_garantia")
    private LocalDateTime fechaFinGarantia;

    @NotNull(message = "El producto es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @NotNull(message = "El local es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_local", nullable = false)
    private Local local;

    @Column(name = "id_detalle_ingreso")
    private Long idDetalleIngreso;
}