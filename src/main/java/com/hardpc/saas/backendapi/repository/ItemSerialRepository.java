package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.dto.ResumenEstadoSerialDTO;
import com.hardpc.saas.backendapi.entity.ItemSerial;
import com.hardpc.saas.backendapi.enums.EstadoDisponibilidad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemSerialRepository extends JpaRepository<ItemSerial, Long> {

    // --- 1. Validaciones de Unicidad ---
    boolean existsByProducto_IdProductoAndNumeroSerieIgnoreCase(Long idProducto, String numeroSerie);
    boolean existsByProducto_IdProductoAndNumeroSerieIgnoreCaseAndIdItemSerialNot(Long idProducto, String numeroSerie, Long idItemSerial);

    // --- 2. Trazabilidad (Escáner de Mostrador) ---
    Optional<ItemSerial> findByNumeroSerieIgnoreCase(String numeroSerie);

    // --- 3. Reporte 1: Agrupación de Estados ---
    @Query("SELECT new com.hardpc.saas.backendapi.dto.ResumenEstadoSerialDTO(i.local.idLocal, i.local.nombre, i.estadoDisponibilidad, COUNT(i)) " +
            "FROM ItemSerial i GROUP BY i.local.idLocal, i.local.nombre, i.estadoDisponibilidad")
    List<ResumenEstadoSerialDTO> contarItemsPorLocalYEstado();

    // --- 4. Reporte 2: Disponibilidad en Local ---
    Page<ItemSerial> findByLocal_IdLocalAndEstadoDisponibilidad(Long idLocal, EstadoDisponibilidad estado, Pageable pageable);

    // --- 5. Búsqueda Avanzada ---
    @Query("SELECT i FROM ItemSerial i WHERE " +
            "(:idLocal IS NULL OR i.local.idLocal = :idLocal) AND " +
            "(LOWER(i.numeroSerie) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
            "LOWER(i.producto.codigoSku) LIKE LOWER(CONCAT('%', :buscar, '%')))")
    Page<ItemSerial> buscarPaginadoAvanzado(@Param("buscar") String buscar, @Param("idLocal") Long idLocal, Pageable pageable);
}