package com.hardpc.saas.backendapi.mapper;

import com.hardpc.saas.backendapi.dto.IngresoCompraRequestDTO;
import com.hardpc.saas.backendapi.dto.IngresoCompraResponseDTO;
import com.hardpc.saas.backendapi.entity.DetalleIngreso;
import com.hardpc.saas.backendapi.entity.IngresoCompra;
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
public interface IngresoCompraMapper {

    // --- DE REQUEST A ENTIDAD (Cabecera) ---
    @Mapping(source = "idProveedor", target = "proveedor.idProveedor")
    @Mapping(source = "idTipoComprobante", target = "tipoComprobante.idTipoComprobante")
    //@Mapping(source = "idUsuario", target = "usuario.idPersona")
    @Mapping(source = "idLocal", target = "local.idLocal")
    IngresoCompra toEntity(IngresoCompraRequestDTO dto);

    // --- DE REQUEST A ENTIDAD (Detalle) ---
    @Mapping(source = "idProducto", target = "producto.idProducto")
    // Ignoramos la lista de Strings porque no mapea directamente a entidades, el orquestador lo maneja
    @Mapping(target = "itemsSeriales", ignore = true)
    DetalleIngreso toDetalleEntity(IngresoCompraRequestDTO.DetalleRequestDTO dto);

    // --- CRÍTICO: CONFIGURACIÓN BIDIRECCIONAL (CascadeType.ALL) ---
    @AfterMapping
    default void enlazarDetalles(@MappingTarget IngresoCompra ingreso) {
        if (ingreso.getDetalles() != null) {
            for (DetalleIngreso detalle : ingreso.getDetalles()) {
                detalle.setIngresoCompra(ingreso); // El hijo reconoce a su padre
            }
        }
    }

    // --- DE ENTIDAD A RESPONSE (Cabecera) ---
    @Mapping(source = "proveedor.idProveedor", target = "idProveedor")
    @Mapping(source = "proveedor.razonSocial", target = "razonSocialProveedor")
    @Mapping(source = "tipoComprobante.idTipoComprobante", target = "idTipoComprobante")
    @Mapping(source = "tipoComprobante.descripcion", target = "descripcionTipoComprobante")
    @Mapping(source = "usuario.idPersona", target = "idUsuario")
    @Mapping(source = "usuario.username", target = "username")
    @Mapping(source = "local.idLocal", target = "idLocal")
    @Mapping(source = "local.nombre", target = "nombreLocal")
    IngresoCompraResponseDTO toResponseDTO(IngresoCompra entity);

    // --- DE ENTIDAD A RESPONSE (Detalle) ---
    @Mapping(source = "producto.idProducto", target = "idProducto")
    @Mapping(source = "producto.codigoSku", target = "codigoSkuProducto")
    @Mapping(source = "producto.descripcion", target = "descripcionProducto")
    IngresoCompraResponseDTO.DetalleResponseDTO toDetalleResponseDTO(DetalleIngreso entity);
}