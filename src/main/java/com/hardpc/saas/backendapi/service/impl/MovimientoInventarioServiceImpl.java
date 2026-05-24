package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.dto.MovimientoInventarioRequestDTO;
import com.hardpc.saas.backendapi.dto.MovimientoInventarioResponseDTO;
import com.hardpc.saas.backendapi.entity.MovimientoInventario;
import com.hardpc.saas.backendapi.entity.Producto;
import com.hardpc.saas.backendapi.enums.TipoMovimiento;
import com.hardpc.saas.backendapi.exception.custom.BusinessException;
import com.hardpc.saas.backendapi.mapper.MovimientoInventarioMapper;
import com.hardpc.saas.backendapi.repository.*;
import com.hardpc.saas.backendapi.service.MovimientoInventarioService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MovimientoInventarioServiceImpl implements MovimientoInventarioService {

    private final MovimientoInventarioRepository repository;
    private final ProductoRepository productoRepository;
    private final LocalRepository localRepository;
    private final ItemSerialRepository itemSerialRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimientoInventarioMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public MovimientoInventarioResponseDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Movimiento no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovimientoInventarioResponseDTO> listarTodos(Pageable pageable) {
        return repository.findAllByOrderByFechaHoraDesc(pageable)
                .map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovimientoInventarioResponseDTO> listarPorProducto(Long idProducto, Pageable pageable) {
        return repository.findByProducto_IdProductoOrderByFechaHoraDesc(idProducto, pageable)
                .map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovimientoInventarioResponseDTO> listarPorLocal(Long idLocal, Pageable pageable) {
        return repository.findByLocalOrigenOrLocalDestinoPaginado(idLocal, pageable)
                .map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovimientoInventarioResponseDTO> filtrarHistorial(LocalDateTime fechaInicio, LocalDateTime fechaFin, TipoMovimiento tipo, Pageable pageable) {
        return repository.buscarPorFiltrosAuditoria(fechaInicio, fechaFin, tipo, pageable)
                .map(mapper::toResponseDTO);
    }

    @Override
    @Transactional
    public MovimientoInventarioResponseDTO registrarMovimiento(MovimientoInventarioRequestDTO dto) {

        // 1. Validar existencia de Usuario
        if (!usuarioRepository.existsById(dto.getIdUsuario())) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "ERR_USUARIO_NOT_FOUND", "El usuario responsable no existe.");
        }

        // 2. Extraer Producto para validación de serialización
        Producto producto = productoRepository.findById(dto.getIdProducto())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "ERR_PRODUCTO_NOT_FOUND", "El producto no existe."));

        // 3. Ejecutar Reglas de Negocio Estrictas
        validarMatrizMovimiento(dto);
        validarReglaSerializacion(dto, producto);

        // 4. Mapear y Configurar Entidad
        MovimientoInventario entidad = mapper.toEntity(dto);
        entidad.setIdMovimiento(null);
        entidad.setFechaHora(LocalDateTime.now());

        // 5. Limpieza de Cascarones (TransientPropertyValueException Prevention)
        limpiarCascaronesVacios(dto, entidad);

        return mapper.toResponseDTO(repository.save(entidad));
    }

    // --- MÉTODOS DE VALIDACIÓN PRIVADOS ---

    /**
     * CRÍTICO: Valida la coherencia de los locales de Origen y Destino según la física del almacén.
     */
    private void validarMatrizMovimiento(MovimientoInventarioRequestDTO dto) {
        if (dto.getTipoMovimiento() == TipoMovimiento.ENTRADA) {
            if (dto.getIdLocalDestino() == null) throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_DESTINO_REQUERIDO", "Una ENTRADA requiere un Local de Destino.");
            if (dto.getIdLocalOrigen() != null) throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_ORIGEN_PROHIBIDO", "Una ENTRADA no debe tener un Local de Origen (viene del exterior).");
            verificarExistenciaLocal(dto.getIdLocalDestino());
        }
        else if (dto.getTipoMovimiento() == TipoMovimiento.SALIDA) {
            if (dto.getIdLocalOrigen() == null) throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_ORIGEN_REQUERIDO", "Una SALIDA requiere un Local de Origen.");
            if (dto.getIdLocalDestino() != null) throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_DESTINO_PROHIBIDO", "Una SALIDA no debe tener un Local de Destino (va hacia el cliente/merma).");
            verificarExistenciaLocal(dto.getIdLocalOrigen());
        }
        else if (dto.getTipoMovimiento() == TipoMovimiento.TRASLADO) {
            if (dto.getIdLocalOrigen() == null || dto.getIdLocalDestino() == null) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_LOCALES_REQUERIDOS", "Un TRASLADO requiere Local de Origen y Local de Destino.");
            }
            if (dto.getIdLocalOrigen().equals(dto.getIdLocalDestino())) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_LOCALES_IGUALES", "En un TRASLADO, el origen y destino no pueden ser el mismo.");
            }
            verificarExistenciaLocal(dto.getIdLocalOrigen());
            verificarExistenciaLocal(dto.getIdLocalDestino());
        }
    }

    /**
     * CRÍTICO: Garantiza que los productos serializados viajen de 1 en 1, con su identificación correcta.
     */
    private void validarReglaSerializacion(MovimientoInventarioRequestDTO dto, Producto producto) {
        if (Boolean.TRUE.equals(producto.getEsSerializado())) {
            if (dto.getCantidad() != 1) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_SERIALIZADO_CANTIDAD", "Los productos serializados deben moverse de a 1 unidad por transacción.");
            }
            if (dto.getIdItemSerial() == null) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_SERIAL_REQUERIDO", "Debe indicar el ItemSerial específico que se está moviendo.");
            }
            if (!itemSerialRepository.existsById(dto.getIdItemSerial())) {
                throw new BusinessException(HttpStatus.NOT_FOUND, "ERR_SERIAL_NOT_FOUND", "El ItemSerial especificado no existe.");
            }
        } else {
            if (dto.getIdItemSerial() != null) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_SERIAL_PROHIBIDO", "El producto no es serializado, no debe enviar un idItemSerial.");
            }
        }
    }

    private void verificarExistenciaLocal(Long idLocal) {
        if (!localRepository.existsById(idLocal)) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "ERR_LOCAL_NOT_FOUND", "El local especificado no existe en la base de datos.");
        }
    }

    /**
     * CRÍTICO: Previene el TransientPropertyValueException de Hibernate cortando
     * las relaciones instanciadas por MapStruct que tienen ID nulo.
     */
    private void limpiarCascaronesVacios(MovimientoInventarioRequestDTO dto, MovimientoInventario entidad) {
        if (dto.getIdLocalOrigen() == null) entidad.setLocalOrigen(null);
        if (dto.getIdLocalDestino() == null) entidad.setLocalDestino(null);
        if (dto.getIdItemSerial() == null) entidad.setItemSerial(null);
    }
}