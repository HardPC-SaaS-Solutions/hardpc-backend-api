package com.hardpc.saas.backendapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ventas")
public class Venta extends AuditoriaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVenta;

    @NotNull(message = "El cliente es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @NotNull(message = "El tipo de comprobante es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_tipo_comprobante", nullable = false)
    private TipoComprobante tipoComprobante;

    @NotNull(message = "La forma de pago es obligatoria")
    @ManyToOne
    @JoinColumn(name = "id_forma_pago", nullable = false)
    private FormaPago formaPago;

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

    @NotNull(message = "La fecha de venta es obligatoria")
    @Column(name = "fecha_venta", nullable = false)
    private LocalDateTime fechaVenta;

    @NotNull(message = "El impuesto es obligatorio")
    @PositiveOrZero(message = "El impuesto no puede ser negativo")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal impuesto;

    @NotNull(message = "El total de la venta es obligatorio")
    @PositiveOrZero(message = "El total no puede ser negativo")
    @Column(name = "total_venta", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalVenta;

    @NotBlank(message = "El estado de la venta es obligatorio")
    @Size(max = 50, message = "El estado no puede exceder los 50 caracteres")
    @Column(name = "estado_venta", nullable = false, length = 50)
    private String estadoVenta; // Ejemplo: REGISTRADA, ANULADA
}