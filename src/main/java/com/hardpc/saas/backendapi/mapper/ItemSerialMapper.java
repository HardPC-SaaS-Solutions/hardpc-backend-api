package com.hardpc.saas.backendapi.mapper;

import com.hardpc.saas.backendapi.dto.ItemSerialRequestDTO;
import com.hardpc.saas.backendapi.dto.ItemSerialResponseDTO;
import com.hardpc.saas.backendapi.entity.ItemSerial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ItemSerialMapper {

    // --- DE REQUEST A ENTIDAD (POST) ---
    @Mapping(source = "idProducto", target = "producto.idProducto")
    @Mapping(source = "idLocal", target = "local.idLocal")
    @Mapping(source = "idDetalleIngreso", target = "detalleIngreso.idDetalleIngreso")
    ItemSerial toEntity(ItemSerialRequestDTO dto);

    // --- DE ENTIDAD A RESPONSE (GET - Aplanamiento Total) ---
    @Mapping(source = "idItemSerial", target = "id")
    @Mapping(source = "producto.idProducto", target = "idProducto")
    @Mapping(source = "producto.codigoSku", target = "codigoSkuProducto")
    @Mapping(source = "producto.descripcion", target = "descripcionProducto")
    @Mapping(source = "local.idLocal", target = "idLocal")
    @Mapping(source = "local.nombre", target = "nombreLocal")
    @Mapping(source = "detalleIngreso.idDetalleIngreso", target = "idDetalleIngreso")
    ItemSerialResponseDTO toResponseDTO(ItemSerial entity);

    // --- ACTUALIZACIÓN PARCIAL (PUT) ---
    @Mapping(source = "idProducto", target = "producto.idProducto")
    @Mapping(source = "idLocal", target = "local.idLocal")
    @Mapping(source = "idDetalleIngreso", target = "detalleIngreso.idDetalleIngreso")
    void updateEntity(ItemSerialRequestDTO dto, @MappingTarget ItemSerial entity);
}