package com.hardpc.saas.backendapi.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hardpc.saas.backendapi.enums.EstadoIngreso;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "ingresos_compra", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_ingreso_proveedor_comprobante",
                columnNames = {"id_proveedor", "id_tipo_comprobante", "serie_comprobante", "numero_comprobante"}
        )
})
@JsonPropertyOrder({"idIngreso", "serieComprobante", "numeroComprobante", "fechaIngreso", "proveedor", "tipoComprobante", "usuario", "local", "impuesto", "totalCompra", "estadoIngreso", "comprobanteDocUrl", "fechaCreacion", "fechaActualizacion"})
public class IngresoCompra extends AuditoriaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idIngreso;

    @NotNull(message = "El proveedor es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_proveedor", nullable = false)
    private Proveedor proveedor;

    @NotNull(message = "El tipo de comprobante es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_tipo_comprobante", nullable = false)
    private TipoComprobante tipoComprobante;

    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @NotNull(message = "El local es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_local", nullable = false)
    private Local local;

    @NotBlank(message = "La serie del comprobante es obligatoria")
    @Size(max = 20, message = "La serie no puede exceder los 20 caracteres")
    @Column(name = "serie_comprobante", nullable = false, length = 20)
    private String serieComprobante;

    @NotBlank(message = "El número del comprobante es obligatorio")
    @Size(max = 50, message = "El número no puede exceder los 50 caracteres")
    @Column(name = "numero_comprobante", nullable = false, length = 50)
    private String numeroComprobante;

    @NotNull(message = "La fecha de ingreso es obligatoria")
    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;

    @NotNull(message = "El impuesto es obligatorio")
    @PositiveOrZero(message = "El impuesto no puede ser negativo")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal impuesto;

    @NotNull(message = "El total de la compra es obligatorio")
    @PositiveOrZero(message = "El total no puede ser negativo")
    @Column(name = "total_compra", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalCompra;

    @NotBlank(message = "El estado del ingreso es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_ingreso", nullable = false, length = 50)
    private EstadoIngreso estadoIngreso = EstadoIngreso.REGISTRADO;

    @Size(max = 255, message = "La URL del documento no puede exceder los 255 caracteres")
    @Column(name = "comprobante_doc_url", length = 255)
    private String comprobanteDocUrl;

    @OneToMany(mappedBy = "ingresoCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DetalleIngreso> detalles;
}