package com.hardpc.saas.backendapi.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hardpc.saas.backendapi.enums.TipoMovimiento;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "movimientos_inventario")
@JsonPropertyOrder({"idMovimiento", "tipoMovimiento", "fechaHora", "cantidad", "observacion", "producto", "itemSerial", "localDestino", "usuario", "fechaCreacion", "fechaActualizacion"})
public class MovimientoInventario extends AuditoriaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMovimiento;

    @NotNull(message = "El tipo de movimiento es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 20)
    private TipoMovimiento tipoMovimiento;

    @NotNull(message = "La fecha y hora son obligatorias")
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(nullable = false)
    private Integer cantidad;

    @NotNull(message = "El producto es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id_item_serial")
    private ItemSerial itemSerial;

    @ManyToOne
    @JoinColumn(name = "id_local_origen")
    private Local localOrigen;

    @ManyToOne
    @JoinColumn(name = "id_local_destino")
    private Local localDestino;

    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(columnDefinition = "TEXT")
    private String observacion;
}