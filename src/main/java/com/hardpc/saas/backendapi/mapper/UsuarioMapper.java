package com.hardpc.saas.backendapi.mapper;

import com.hardpc.saas.backendapi.dto.UsuarioRequestDTO;
import com.hardpc.saas.backendapi.dto.UsuarioResponseDTO;
import com.hardpc.saas.backendapi.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UsuarioMapper {

    // --- DE REQUEST A ENTIDAD (POST) ---
    @Mapping(source = "idTipoDocumento", target = "tipoDocumento.idTipoDocumento")
    @Mapping(source = "idRol", target = "rol.idRol")
    // Ignoramos el password en el mapper porque el Service lo encriptará manualmente
    @Mapping(target = "password", ignore = true)
    Usuario toEntity(UsuarioRequestDTO dto);

    // --- DE ENTIDAD A RESPONSE (GET) - Aplanamiento ---
    @Mapping(source = "idPersona", target = "id")
    @Mapping(source = "tipoDocumento.idTipoDocumento", target = "idTipoDocumento")
    @Mapping(source = "tipoDocumento.abreviatura", target = "abreviaturaTipoDocumento")
    @Mapping(source = "rol.idRol", target = "idRol")
    @Mapping(source = "rol.nombre", target = "nombreRol")
    UsuarioResponseDTO toResponseDTO(Usuario entity);

    // --- ACTUALIZACIÓN PARCIAL DE ENTIDAD (PUT) ---
    @Mapping(source = "idTipoDocumento", target = "tipoDocumento.idTipoDocumento")
    @Mapping(source = "idRol", target = "rol.idRol")
    @Mapping(target = "password", ignore = true) // Protegemos el hash existente
    void updateEntity(UsuarioRequestDTO dto, @MappingTarget Usuario entity);
}