package com.hardpc.saas.backendapi.mapper;

import com.hardpc.saas.backendapi.dto.ClienteRequestDTO;
import com.hardpc.saas.backendapi.dto.ClienteResponseDTO;
import com.hardpc.saas.backendapi.entity.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ClienteMapper {

    // --- DE REQUEST A ENTIDAD (POST) ---
    @Mapping(source = "idTipoDocumento", target = "tipoDocumento.idTipoDocumento")
    Cliente toEntity(ClienteRequestDTO dto);

    // --- DE ENTIDAD A RESPONSE (GET) ---
    @Mapping(source = "idPersona", target = "id")
    @Mapping(source = "tipoDocumento.idTipoDocumento", target = "idTipoDocumento")
    @Mapping(source = "tipoDocumento.abreviatura", target = "abreviaturaTipoDocumento") // O nombre si no existe abreviatura
    ClienteResponseDTO toResponseDTO(Cliente entity);

    // --- ACTUALIZACIÓN PARCIAL (PUT) ---
    @Mapping(source = "idTipoDocumento", target = "tipoDocumento.idTipoDocumento")
    void updateEntity(ClienteRequestDTO dto, @MappingTarget Cliente entity);
}