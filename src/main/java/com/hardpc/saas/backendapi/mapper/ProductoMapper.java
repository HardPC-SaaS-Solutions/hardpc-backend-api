package com.hardpc.saas.backendapi.mapper;

import com.hardpc.saas.backendapi.dto.ProductoRequestDTO;
import com.hardpc.saas.backendapi.dto.ProductoResponseDTO;
import com.hardpc.saas.backendapi.entity.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductoMapper {

    // --- DE REQUEST A ENTIDAD (POST) ---
    // Mapeo estratégico: Inyectamos los IDs recibidos en las llaves primarias de las relaciones
    @Mapping(source = "idMarca", target = "marca.idMarca")
    @Mapping(source = "idCategoria", target = "categoria.idCategoria")
    @Mapping(source = "idUnidadMedida", target = "unidadMedida.idUnidadMedida")
    Producto toEntity(ProductoRequestDTO dto);

    // --- DE ENTIDAD A RESPONSE (GET) ---
    // Aplanamiento total: Extraemos propiedades internas de los objetos para el Frontend
    @Mapping(source = "idProducto", target = "id")
    @Mapping(source = "marca.idMarca", target = "idMarca")
    @Mapping(source = "marca.nombre", target = "nombreMarca")
    @Mapping(source = "categoria.idCategoria", target = "idCategoria")
    @Mapping(source = "categoria.nombre", target = "nombreCategoria")
    @Mapping(source = "unidadMedida.idUnidadMedida", target = "idUnidadMedida")
    @Mapping(source = "unidadMedida.descripcion", target = "descripcionUnidadMedida")
    ProductoResponseDTO toResponseDTO(Producto entity);

    // --- ACTUALIZACIÓN PARCIAL (PUT) ---
    @Mapping(source = "idMarca", target = "marca.idMarca")
    @Mapping(source = "idCategoria", target = "categoria.idCategoria")
    @Mapping(source = "idUnidadMedida", target = "unidadMedida.idUnidadMedida")
    void updateEntity(ProductoRequestDTO dto, @MappingTarget Producto entity);
}