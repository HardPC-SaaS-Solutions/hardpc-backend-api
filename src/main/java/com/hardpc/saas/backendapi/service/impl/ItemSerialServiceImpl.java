package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.dto.ItemSerialRequestDTO;
import com.hardpc.saas.backendapi.dto.ItemSerialResponseDTO;
import com.hardpc.saas.backendapi.dto.ResumenEstadoSerialDTO;
import com.hardpc.saas.backendapi.entity.ItemSerial;
import com.hardpc.saas.backendapi.entity.Producto;
import com.hardpc.saas.backendapi.enums.EstadoDisponibilidad;
import com.hardpc.saas.backendapi.exception.custom.BusinessException;
import com.hardpc.saas.backendapi.mapper.ItemSerialMapper;
import com.hardpc.saas.backendapi.repository.ItemSerialRepository;
import com.hardpc.saas.backendapi.repository.LocalRepository;
import com.hardpc.saas.backendapi.repository.ProductoRepository;
import com.hardpc.saas.backendapi.service.ItemSerialService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemSerialServiceImpl implements ItemSerialService {

    private final ItemSerialRepository repository;
    private final LocalRepository localRepository;
    private final ProductoRepository productoRepository;
    private final ItemSerialMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ItemSerialResponseDTO> listarPaginado(String buscar, Long idLocal, Pageable pageable) {
        String termino = (buscar == null || buscar.trim().isEmpty()) ? "" : buscar.trim();
        return repository.buscarPaginadoAvanzado(termino, idLocal, pageable)
                .map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemSerialResponseDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Item serial no encontrado con el ID: " + id));
    }

    @Override
    @Transactional
    public ItemSerialResponseDTO crear(ItemSerialRequestDTO dto) {
        validarMaestrasYSerializacionInversa(dto);
        validarUnicidad(dto, null);

        ItemSerial entidad = mapper.toEntity(dto);
        entidad.setIdItemSerial(null);

        if (entidad.getDetalleIngreso() != null && entidad.getDetalleIngreso().getIdDetalleIngreso() == null) {
            entidad.setDetalleIngreso(null);
        }

        // Si el estado no viene configurado, forzamos DISPONIBLE por defecto
        if (entidad.getEstadoDisponibilidad() == null) {
            entidad.setEstadoDisponibilidad(EstadoDisponibilidad.DISPONIBLE);
        }

        return mapper.toResponseDTO(repository.save(entidad));
    }

    @Override
    @Transactional
    public ItemSerialResponseDTO actualizar(Long id, ItemSerialRequestDTO dto) {
        ItemSerial existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item serial no encontrado con el ID: " + id));

        validarMaestrasYSerializacionInversa(dto);
        validarUnicidad(dto, id);

        mapper.updateEntity(dto, existente);

        if (existente.getDetalleIngreso() != null && existente.getDetalleIngreso().getIdDetalleIngreso() == null) {
            existente.setDetalleIngreso(null);
        }

        return mapper.toResponseDTO(repository.save(existente));
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> obtenerSeriesDisponibles(Long idProducto, Long idLocal) {
        return repository.findSeriesDisponiblesParaVenta(idProducto, idLocal);
    }

    // --- MÉTODOS OPERATIVOS ESPECIALES ---

    /**
     * Permite un cambio de estado ágil sin enviar todo el payload (ej. de DISPONIBLE a EN_REPARACION).
     */
    @Override
    @Transactional
    public void cambiarEstado(Long idItemSerial, EstadoDisponibilidad nuevoEstado) {
        if (nuevoEstado == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_ESTADO_NULO", "Debe proveer un nuevo estado válido.");
        }
        ItemSerial existente = repository.findById(idItemSerial)
                .orElseThrow(() -> new EntityNotFoundException("Item serial no encontrado con el ID: " + idItemSerial));

        existente.setEstadoDisponibilidad(nuevoEstado);
        repository.save(existente);
    }

    /**
     * Trazabilidad pura: Escaneas un código de barras físico y el sistema te dice
     * exactamente qué es, dónde está y su garantía.
     */
    @Override
    @Transactional(readOnly = true)
    public ItemSerialResponseDTO buscarPorSerialExacto(String numeroSerie) {
        return repository.findByNumeroSerieIgnoreCase(numeroSerie)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("No existe ninguna unidad física con el serial: " + numeroSerie));
    }

    // --- REPORTES ---

    @Override
    @Transactional(readOnly = true)
    public List<ResumenEstadoSerialDTO> reporteEstadosAgrupados() {
        return repository.contarItemsPorLocalYEstado();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemSerialResponseDTO> listarDisponiblesPorLocal(Long idLocal, Pageable pageable) {
        if (!localRepository.existsById(idLocal)) {
            throw new EntityNotFoundException("El local especificado no existe.");
        }
        return repository.findByLocal_IdLocalAndEstadoDisponibilidad(idLocal, EstadoDisponibilidad.DISPONIBLE, pageable)
                .map(mapper::toResponseDTO);
    }

    // --- REGLAS DE NEGOCIO PRIVADAS ---

    /**
     * CRÍTICO: BARRERA DE SERIALIZACIÓN INVERSA.
     * Si el producto (ej. Cables, Mousepads) NO está marcado como serializado en su catálogo maestro,
     * el sistema prohibirá terminantemente que se cree un registro en esta tabla.
     */
    private void validarMaestrasYSerializacionInversa(ItemSerialRequestDTO dto) {
        if (!localRepository.existsById(dto.getIdLocal())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_LOCAL_NOT_FOUND", "El local seleccionado no existe.");
        }

        Producto producto = productoRepository.findById(dto.getIdProducto())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "ERR_PRODUCTO_NOT_FOUND", "El producto seleccionado no existe."));

        if (Boolean.FALSE.equals(producto.getEsSerializado())) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "ERR_PRODUCTO_NO_SERIALIZADO",
                    "El producto no es serializado. Gestione su stock mediante agrupaciones en el módulo de Stock Local."
            );
        }
    }

    private void validarUnicidad(ItemSerialRequestDTO dto, Long idExistente) {
        boolean serialDuplicado = (idExistente == null)
                ? repository.existsByProducto_IdProductoAndNumeroSerieIgnoreCase(dto.getIdProducto(), dto.getNumeroSerie())
                : repository.existsByProducto_IdProductoAndNumeroSerieIgnoreCaseAndIdItemSerialNot(dto.getIdProducto(), dto.getNumeroSerie(), idExistente);

        if (serialDuplicado) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    "ERR_DUPLICATE_SERIAL",
                    "El número de serie ingresado ya se encuentra registrado para este producto."
            );
        }
    }
}