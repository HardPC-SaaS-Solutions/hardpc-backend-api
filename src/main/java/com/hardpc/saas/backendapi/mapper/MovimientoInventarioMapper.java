package com.hardpc.saas.backendapi.mapper;

import com.hardpc.saas.backendapi.dto.MovimientoInventarioRequestDTO;
import com.hardpc.saas.backendapi.dto.MovimientoInventarioResponseDTO;
import com.hardpc.saas.backendapi.entity.MovimientoInventario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MovimientoInventarioMapper {

    // --- DE REQUEST A ENTIDAD (POST) ---
    // NOTA: Usuario hereda de Persona, su ID en base de datos es idPersona
    @Mapping(source = "idProducto", target = "producto.idProducto")
    @Mapping(source = "idUsuario", target = "usuario.idPersona")
    @Mapping(source = "idLocalOrigen", target = "localOrigen.idLocal")
    @Mapping(source = "idLocalDestino", target = "localDestino.idLocal")
    @Mapping(source = "idItemSerial", target = "itemSerial.idItemSerial")
    MovimientoInventario toEntity(MovimientoInventarioRequestDTO dto);

    // --- DE ENTIDAD A RESPONSE (GET) ---
    @Mapping(source = "producto.idProducto", target = "idProducto")
    @Mapping(source = "producto.codigoSku", target = "codigoSkuProducto")
    @Mapping(source = "producto.descripcion", target = "descripcionProducto")

    @Mapping(source = "itemSerial.idItemSerial", target = "idItemSerial")
    @Mapping(source = "itemSerial.numeroSerie", target = "numeroSerie")

    @Mapping(source = "localOrigen.idLocal", target = "idLocalOrigen")
    @Mapping(source = "localOrigen.nombre", target = "nombreLocalOrigen")

    @Mapping(source = "localDestino.idLocal", target = "idLocalDestino")
    @Mapping(source = "localDestino.nombre", target = "nombreLocalDestino")

    @Mapping(source = "usuario.idPersona", target = "idUsuario")
    @Mapping(source = "usuario.username", target = "username")
    MovimientoInventarioResponseDTO toResponseDTO(MovimientoInventario entity);
}