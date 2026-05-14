package com.hardpc.saas.backendapi.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "detalles_ingreso")
@JsonPropertyOrder({"idDetalleIngreso", "ingreso", "producto", "cantidad", "precioCompraUnitario", "fechaCreacion", "fechaActualizacion"})
public class DetalleIngreso extends AuditoriaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetalleIngreso;

    @NotNull(message = "El ingreso de compra es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_ingreso", nullable = false)
    @JsonBackReference
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

    // --- RELACIÓN HACIA ABAJO (Los Hijos Serializados) ---
    // Aquí es donde vinculas los ItemSerial que te llegaron en esta compra
    @OneToMany(mappedBy = "detalleIngreso", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Eres el padre de los ItemSerial, permite serializarlos
    private List<ItemSerial> itemsSeriales;
}