package com.hardpc.saas.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hardpc.saas.backendapi.enums.EstadoVenta;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonPropertyOrder({
        "idVenta", "serieComprobante", "numeroComprobante", "fechaVenta",
        "idCliente", "nombreCliente", "idUsuario", "username",
        "idTipoComprobante", "descripcionTipoComprobante",
        "idFormaPago", "descripcionFormaPago", "idLocal", "nombreLocal",
        "impuesto", "totalVenta", "estadoVenta", "detalles"
})
public class VentaResponseDTO {

    private Long idVenta;
    private String serieComprobante;
    private String numeroComprobante;
    private LocalDateTime fechaVenta;

    // --- Aplanamiento de Foráneas ---
    private Long idCliente;
    private String nombreCliente;

    private Long idUsuario;
    private String username;

    private Long idTipoComprobante;
    private String descripcionTipoComprobante;

    private Long idFormaPago;
    private String descripcionFormaPago;

    private Long idLocal;
    private String nombreLocal;

    private BigDecimal impuesto;
    private BigDecimal totalVenta;
    private EstadoVenta estadoVenta;

    private List<DetalleResponseDTO> detalles;

    // --- DTO ANIDADO: Detalle ---
    @Data
    public static class DetalleResponseDTO {
        private Long idDetalleVenta;
        private Long idProducto;
        private String codigoSkuProducto;
        private String descripcionProducto;
        private Integer cantidad;
        private BigDecimal precioVentaUnitario;
        private BigDecimal descuento;
    }
}