package com.hardpc.saas.backendapi.mapper;

import com.hardpc.saas.backendapi.dto.*;
import com.hardpc.saas.backendapi.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MaestroMapper {

    // --- Categoria ---
    @Mapping(source = "idCategoria", target = "id")
    CategoriaDTO toDTO(Categoria entity);
    @Mapping(source = "id", target = "idCategoria")
    Categoria toEntity(CategoriaDTO dto);
    List<CategoriaDTO> toCategoriaDTOList(List<Categoria> entities);

    // --- Marca ---
    @Mapping(source = "idMarca", target = "id")
    MarcaDTO toDTO(Marca entity);
    @Mapping(source = "id", target = "idMarca")
    Marca toEntity(MarcaDTO dto);
    List<MarcaDTO> toMarcaDTOList(List<Marca> entities);

    // --- UnidadMedida ---
    @Mapping(source = "idUnidadMedida", target = "id")
    UnidadMedidaDTO toDTO(UnidadMedida entity);
    @Mapping(source = "id", target = "idUnidadMedida")
    UnidadMedida toEntity(UnidadMedidaDTO dto);
    List<UnidadMedidaDTO> toUnidadMedidaDTOList(List<UnidadMedida> entities);

    // --- FormaPago ---
    @Mapping(source = "idFormaPago", target = "id")
    FormaPagoDTO toDTO(FormaPago entity);
    @Mapping(source = "id", target = "idFormaPago")
    FormaPago toEntity(FormaPagoDTO dto);
    List<FormaPagoDTO> toFormaPagoDTOList(List<FormaPago> entities);

    // --- TipoComprobante ---
    @Mapping(source = "idTipoComprobante", target = "id")
    TipoComprobanteDTO toDTO(TipoComprobante entity);
    @Mapping(source = "id", target = "idTipoComprobante")
    TipoComprobante toEntity(TipoComprobanteDTO dto);
    List<TipoComprobanteDTO> toTipoComprobanteDTOList(List<TipoComprobante> entities);

    // --- TipoDocumento ---
    @Mapping(source = "idTipoDocumento", target = "id")
    TipoDocumentoDTO toDTO(TipoDocumento entity);
    @Mapping(source = "id", target = "idTipoDocumento")
    TipoDocumento toEntity(TipoDocumentoDTO dto);
    List<TipoDocumentoDTO> toTipoDocumentoDTOList(List<TipoDocumento> entities);

    // --- Rol ---
    @Mapping(source = "idRol", target = "id")
    RolDTO toDTO(Rol entity);
    @Mapping(source = "id", target = "idRol")
    Rol toEntity(RolDTO dto);
    List<RolDTO> toRolDTOList(List<Rol> entities);

    // --- Local ---
    @Mapping(source = "idLocal", target = "id")
    LocalDTO toDTO(Local entity);
    @Mapping(source = "id", target = "idLocal")
    Local toEntity(LocalDTO dto);
    List<LocalDTO> toLocalDTOList(List<Local> entities);
}