package com.hardpc.saas.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hardpc.saas.backendapi.enums.EstadoIngreso;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonPropertyOrder({
        "idIngreso", "serieComprobante", "numeroComprobante", "fechaIngreso",
        "idProveedor", "razonSocialProveedor", "idTipoComprobante", "descripcionTipoComprobante",
        "idUsuario", "username", "idLocal", "nombreLocal",
        "impuesto", "totalCompra", "estadoIngreso", "comprobanteDocUrl", "detalles"
})
public class IngresoCompraResponseDTO {

    private Long idIngreso;
    private String serieComprobante;
    private String numeroComprobante;
    private LocalDateTime fechaIngreso;

    // Aplanamiento Foráneo
    private Long idProveedor;
    private String razonSocialProveedor;

    private Long idTipoComprobante;
    private String descripcionTipoComprobante;

    private Long idUsuario;
    private String username;

    private Long idLocal;
    private String nombreLocal;

    private BigDecimal impuesto;
    private BigDecimal totalCompra;
    private EstadoIngreso estadoIngreso;
    private String comprobanteDocUrl;

    private List<DetalleResponseDTO> detalles;

    // --- DTO ANIDADO: Detalle del Response ---
    @Data
    public static class DetalleResponseDTO {
        private Long idDetalleIngreso;
        private Long idProducto;
        private String codigoSkuProducto;
        private String descripcionProducto;
        private Integer cantidad;
        private BigDecimal precioCompraUnitario;
    }
}