package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.dto.ItemSerialRequestDTO;
import com.hardpc.saas.backendapi.dto.ItemSerialResponseDTO;
import com.hardpc.saas.backendapi.dto.ResumenEstadoSerialDTO;
import com.hardpc.saas.backendapi.enums.EstadoDisponibilidad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemSerialService {
    Page<ItemSerialResponseDTO> listarPaginado(String buscar, Long idLocal, Pageable pageable);
    ItemSerialResponseDTO buscarPorId(Long id);
    ItemSerialResponseDTO crear(ItemSerialRequestDTO dto);
    ItemSerialResponseDTO actualizar(Long id, ItemSerialRequestDTO dto);

    // Métodos Operativos Especiales
    void cambiarEstado(Long idItemSerial, EstadoDisponibilidad nuevoEstado);
    ItemSerialResponseDTO buscarPorSerialExacto(String numeroSerie);

    // Reportes
    List<ResumenEstadoSerialDTO> reporteEstadosAgrupados();
    Page<ItemSerialResponseDTO> listarDisponiblesPorLocal(Long idLocal, Pageable pageable);

    List<String> obtenerSeriesDisponibles(Long idProducto, Long idLocal);
}