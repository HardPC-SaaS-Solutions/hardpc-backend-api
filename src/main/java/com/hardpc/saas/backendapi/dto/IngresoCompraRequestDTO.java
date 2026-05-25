package com.hardpc.saas.backendapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class IngresoCompraRequestDTO {

    @NotNull(message = "El proveedor es obligatorio")
    private Long idProveedor;

    @NotNull(message = "El tipo de comprobante es obligatorio")
    private Long idTipoComprobante;

    //@NotNull(message = "El usuario es obligatorio")
    //private Long idUsuario;

    @NotNull(message = "El local destino es obligatorio")
    private Long idLocal;

    @NotBlank(message = "La serie del comprobante es obligatoria")
    private String serieComprobante;

    @NotBlank(message = "El número del comprobante es obligatorio")
    private String numeroComprobante;

    @NotNull(message = "La fecha de ingreso es obligatoria")
    private LocalDateTime fechaIngreso;

    @NotNull(message = "El impuesto es obligatorio")
    @PositiveOrZero(message = "El impuesto no puede ser negativo")
    private BigDecimal impuesto;

    @NotNull(message = "El total de la compra es obligatorio")
    @Positive(message = "El total de la compra debe ser mayor a 0")
    private BigDecimal totalCompra;

    @NotEmpty(message = "La compra debe tener al menos un detalle (producto)")
    @Valid
    private List<DetalleRequestDTO> detalles;

    // --- DTO ANIDADO: Detalle del Request ---
    @Data
    public static class DetalleRequestDTO {
        @NotNull(message = "El producto es obligatorio")
        private Long idProducto;

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        private Integer cantidad;

        @NotNull(message = "El precio unitario de compra es obligatorio")
        @PositiveOrZero(message = "El precio de compra no puede ser negativo")
        private BigDecimal precioCompraUnitario;

        // Solo se utiliza si el producto en la BBDD está marcado como serializado
        private List<String> numerosSerie;
    }
}