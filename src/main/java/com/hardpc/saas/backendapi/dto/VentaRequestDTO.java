package com.hardpc.saas.backendapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class VentaRequestDTO {

    @NotNull(message = "El cliente es obligatorio")
    private Long idCliente;

    @NotNull(message = "El usuario (vendedor) es obligatorio")
    private Long idUsuario;

    @NotNull(message = "El tipo de comprobante es obligatorio")
    private Long idTipoComprobante;

    @NotNull(message = "La forma de pago es obligatoria")
    private Long idFormaPago;

    @NotNull(message = "El local origen de la venta es obligatorio")
    private Long idLocal;

    @NotBlank(message = "La serie del comprobante es obligatoria")
    private String serieComprobante;

    @NotBlank(message = "El número del comprobante es obligatorio")
    private String numeroComprobante;

    //@NotNull(message = "La fecha de venta es obligatoria")
    //private LocalDateTime fechaVenta;

    @NotNull(message = "El impuesto es obligatorio")
    @PositiveOrZero(message = "El impuesto no puede ser negativo")
    private BigDecimal impuesto;

    @NotNull(message = "El total de la venta es obligatorio")
    @Positive(message = "El total de la venta debe ser mayor a cero")
    private BigDecimal totalVenta;

    @NotEmpty(message = "La venta debe contener al menos un producto")
    @Valid
    private List<DetalleRequestDTO> detalles;

    // --- DTO ANIDADO: Detalle ---
    @Data
    public static class DetalleRequestDTO {
        @NotNull(message = "El producto es obligatorio")
        private Long idProducto;

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        private Integer cantidad;

        @NotNull(message = "El precio de venta unitario es obligatorio")
        @PositiveOrZero(message = "El precio no puede ser negativo")
        private BigDecimal precioVentaUnitario;

        @NotNull(message = "El descuento es obligatorio")
        @PositiveOrZero(message = "El descuento no puede ser negativo")
        private BigDecimal descuento;

        // Opcional, solo exigido y validado si el producto maestro es serializado
        private List<String> numerosSerie;
    }
}