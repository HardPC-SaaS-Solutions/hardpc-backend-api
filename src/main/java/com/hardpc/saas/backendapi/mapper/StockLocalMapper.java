package com.hardpc.saas.backendapi.mapper;

import com.hardpc.saas.backendapi.dto.StockLocalRequestDTO;
import com.hardpc.saas.backendapi.dto.StockLocalResponseDTO;
import com.hardpc.saas.backendapi.entity.StockLocal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface StockLocalMapper {

    // --- DE REQUEST A ENTIDAD (POST) ---
    @Mapping(source = "idProducto", target = "producto.idProducto")
    @Mapping(source = "idLocal", target = "local.idLocal")
    StockLocal toEntity(StockLocalRequestDTO dto);

    // --- DE ENTIDAD A RESPONSE (GET) ---
    @Mapping(source = "idStockLocal", target = "id")
    @Mapping(source = "producto.idProducto", target = "idProducto")
    @Mapping(source = "producto.codigoSku", target = "codigoSkuProducto")
    @Mapping(source = "producto.descripcion", target = "descripcionProducto")
    @Mapping(source = "local.idLocal", target = "idLocal")
    @Mapping(source = "local.nombre", target = "nombreLocal")
    StockLocalResponseDTO toResponseDTO(StockLocal entity);

    // --- ACTUALIZACIÓN PARCIAL (PUT) ---
    @Mapping(source = "idProducto", target = "producto.idProducto")
    @Mapping(source = "idLocal", target = "local.idLocal")
    void updateEntity(StockLocalRequestDTO dto, @MappingTarget StockLocal entity);
}