package com.hardpc.saas.backendapi.mapper;

import com.hardpc.saas.backendapi.dto.ProveedorRequestDTO;
import com.hardpc.saas.backendapi.dto.ProveedorResponseDTO;
import com.hardpc.saas.backendapi.entity.Proveedor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProveedorMapper {

    // --- DE REQUEST A ENTIDAD (POST) ---
    Proveedor toEntity(ProveedorRequestDTO dto);

    // --- DE ENTIDAD A RESPONSE (GET) ---
    @Mapping(source = "idProveedor", target = "id")
    ProveedorResponseDTO toResponseDTO(Proveedor entity);

    // --- ACTUALIZACIÓN PARCIAL (PUT) ---
    void updateEntity(ProveedorRequestDTO dto, @MappingTarget Proveedor entity);
}