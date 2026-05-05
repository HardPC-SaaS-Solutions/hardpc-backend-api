package com.hardpc.saas.backendapi.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hardpc.saas.backendapi.enums.Condicion;
import com.hardpc.saas.backendapi.enums.EstadoDisponibilidad;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "items_seriales")
@JsonPropertyOrder({"idItemSerial", "numeroSerie", "condicion", "estadoDisponibilidad", "fechaFinGarantia", "producto", "local", "detalleIngreso", "fechaCreacion", "fechaActualizacion"})
public class ItemSerial extends AuditoriaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idItemSerial;

    @NotBlank(message = "El número de serie es obligatorio")
    @Size(max = 100, message = "El número de serie no puede exceder los 100 caracteres")
    @Column(name = "numero_serie", nullable = false, unique = true, length = 100)
    private String numeroSerie;

    @NotBlank(message = "La condición es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Condicion condicion;

    @NotBlank(message = "El estado de disponibilidad es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_disponibilidad", nullable = false, length = 50)
    private EstadoDisponibilidad estadoDisponibilidad = EstadoDisponibilidad.DISPONIBLE;

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

    @ManyToOne
    @JoinColumn(name = "id_detalle_ingreso")
    @JsonBackReference
    private DetalleIngreso detalleIngreso;
}