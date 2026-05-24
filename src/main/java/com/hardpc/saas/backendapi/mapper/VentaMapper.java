package com.hardpc.saas.backendapi.mapper;

import com.hardpc.saas.backendapi.dto.VentaRequestDTO;
import com.hardpc.saas.backendapi.dto.VentaResponseDTO;
import com.hardpc.saas.backendapi.entity.DetalleVenta;
import com.hardpc.saas.backendapi.entity.Venta;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface VentaMapper {

    // --- DE REQUEST A ENTIDAD (Cabecera) ---
    @Mapping(source = "idCliente", target = "cliente.idPersona")
    @Mapping(source = "idUsuario", target = "usuario.idPersona")
    @Mapping(source = "idTipoComprobante", target = "tipoComprobante.idTipoComprobante")
    @Mapping(source = "idFormaPago", target = "formaPago.idFormaPago")
    @Mapping(source = "idLocal", target = "local.idLocal")
    Venta toEntity(VentaRequestDTO dto);

    // --- DE REQUEST A ENTIDAD (Detalle) ---
    @Mapping(source = "idProducto", target = "producto.idProducto")
    // Se ignora porque los números de serie son físicos y no se mapean directo a un listado String en DetalleVenta
    // El Orquestador extraerá 1 serial de BD para asignarlo dinámicamente si corresponde
    @Mapping(target = "itemSerial", ignore = true)
    DetalleVenta toDetalleEntity(VentaRequestDTO.DetalleRequestDTO dto);

    // --- CRÍTICO: CONFIGURACIÓN BIDIRECCIONAL (CascadeType.ALL) ---
    @AfterMapping
    default void enlazarDetallesVenta(@MappingTarget Venta venta) {
        if (venta.getDetalles() != null) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                detalle.setVenta(venta); // Padre reclama al hijo para Hibernate
            }
        }
    }

    // --- DE ENTIDAD A RESPONSE (Cabecera) ---
    @Mapping(source = "cliente.idPersona", target = "idCliente")
    // Expresión para unificar Nombre o Razón Social al frontend
    @Mapping(target = "nombreCliente", expression = "java( entity.getCliente().getRazonSocial() != null ? entity.getCliente().getRazonSocial() : entity.getCliente().getNombres() + ' ' + entity.getCliente().getApellidos() )")
    @Mapping(source = "usuario.idPersona", target = "idUsuario")
    @Mapping(source = "usuario.username", target = "username")
    @Mapping(source = "tipoComprobante.idTipoComprobante", target = "idTipoComprobante")
    @Mapping(source = "tipoComprobante.descripcion", target = "descripcionTipoComprobante")
    @Mapping(source = "formaPago.idFormaPago", target = "idFormaPago")
    @Mapping(source = "formaPago.descripcion", target = "descripcionFormaPago")
    @Mapping(source = "local.idLocal", target = "idLocal")
    @Mapping(source = "local.nombre", target = "nombreLocal")
    VentaResponseDTO toResponseDTO(Venta entity);

    // --- DE ENTIDAD A RESPONSE (Detalle) ---
    @Mapping(source = "producto.idProducto", target = "idProducto")
    @Mapping(source = "producto.codigoSku", target = "codigoSkuProducto")
    @Mapping(source = "producto.descripcion", target = "descripcionProducto")
    VentaResponseDTO.DetalleResponseDTO toDetalleResponseDTO(DetalleVenta entity);
}