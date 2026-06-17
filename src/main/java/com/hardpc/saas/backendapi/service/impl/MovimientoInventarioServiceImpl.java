package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.dto.MovimientoInventarioRequestDTO;
import com.hardpc.saas.backendapi.dto.MovimientoInventarioResponseDTO;
import com.hardpc.saas.backendapi.entity.ItemSerial;
import com.hardpc.saas.backendapi.entity.MovimientoInventario;
import com.hardpc.saas.backendapi.entity.Producto;
import com.hardpc.saas.backendapi.enums.EstadoDisponibilidad;
import com.hardpc.saas.backendapi.enums.TipoMovimiento;
import com.hardpc.saas.backendapi.exception.custom.BusinessException;
import com.hardpc.saas.backendapi.mapper.MovimientoInventarioMapper;
import com.hardpc.saas.backendapi.repository.*;
import com.hardpc.saas.backendapi.service.MovimientoInventarioService;
import com.hardpc.saas.backendapi.service.StockLocalService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.hardpc.saas.backendapi.security.CustomUserDetails;
import com.hardpc.saas.backendapi.entity.Usuario;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MovimientoInventarioServiceImpl implements MovimientoInventarioService {

    private final MovimientoInventarioRepository repository;
    private final ProductoRepository productoRepository;
    private final LocalRepository localRepository;
    private final ItemSerialRepository itemSerialRepository;
    private final UsuarioRepository usuarioRepository;
    private final StockLocalService stockLocalService;
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
        // --- FIX DE SEGURIDAD: Extraer el usuario autenticado del JWT ---
        // (Nota: Como este método a veces es llamado internamente por Venta/Compra,
        // validamos si ya hay un usuario en el DTO (inyectado por el orquestador padre).
        // Si es un ajuste manual directo por API, lo sacamos del Token).
        Long idUsuarioReal;
        if (dto.getIdUsuario() != null) {
            idUsuarioReal = dto.getIdUsuario();
            if (!usuarioRepository.existsById(idUsuarioReal)) {
                throw new BusinessException(HttpStatus.NOT_FOUND, "ERR_USUARIO_NOT_FOUND", "El usuario responsable no existe.");
            }
        } else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            idUsuarioReal = userDetails.getUsuario().getIdPersona();
        }
        // -----------------------------------------------------------------

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

        // --- FIX DE SEGURIDAD: Asignar la referencia del usuario real ---
        entidad.setUsuario(usuarioRepository.getReferenceById(idUsuarioReal));

        // 5. Limpieza de Cascarones (TransientPropertyValueException Prevention)
        limpiarCascaronesVacios(dto, entidad);

        MovimientoInventario guardado = repository.save(entidad);

        // --- BLOQUEO TRANSACCIONAL: ACTUALIZACIÓN SÍNCRONA DE STOCK FÍSICO ---

        // NOTA: Ignoramos los productos serializados, ellos no mueven cantidades aquí, se gestionan por ItemSerial
        if (!Boolean.TRUE.equals(producto.getEsSerializado())) {

            if (dto.getTipoMovimiento() == TipoMovimiento.TRASLADO) {
                // Traslado = 2 Operaciones atómicas. Primero sale del origen, luego entra al destino.
                stockLocalService.actualizarStock(dto.getIdProducto(), dto.getIdLocalOrigen(), dto.getCantidad(), TipoMovimiento.SALIDA);
                stockLocalService.actualizarStock(dto.getIdProducto(), dto.getIdLocalDestino(), dto.getCantidad(), TipoMovimiento.ENTRADA);
            } else {
                // Entrada o Salida estándar = 1 Operación atómica.
                Long idLocalAfectado = (dto.getIdLocalOrigen() != null) ? dto.getIdLocalOrigen() : dto.getIdLocalDestino();
                stockLocalService.actualizarStock(dto.getIdProducto(), idLocalAfectado, dto.getCantidad(), dto.getTipoMovimiento());
            }
        } else {
            // --- FIX: LÓGICA SERIALIZADA ---
            // Si es un traslado, debemos cambiar la ubicación física de la máquina
            if (dto.getTipoMovimiento() == TipoMovimiento.TRASLADO) {
                ItemSerial item = itemSerialRepository.findById(dto.getIdItemSerial()).get();
                // Movemos el ItemSerial al nuevo local
                item.setLocal(localRepository.getReferenceById(dto.getIdLocalDestino()));
                itemSerialRepository.save(item);
            }
        }

        return mapper.toResponseDTO(guardado);
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

            // --- EL GRAN BLINDAJE QUE DETECTASTE ---
            // Traemos el item serial completo para auditar su estado real en el almacén
            ItemSerial item = itemSerialRepository.findById(dto.getIdItemSerial())
                    .orElseThrow(() -> new EntityNotFoundException("El ItemSerial especificado no existe."));

            // 1. Validar que el serial pertenezca al producto seleccionado
            if (!item.getProducto().getIdProducto().equals(producto.getIdProducto())) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_SERIAL_INCORRECTO", "El serial no pertenece al producto seleccionado.");
            }

            // 2. Control de Ubicación Física Obligatoria para SALIDAS y TRASLADOS
            if (dto.getTipoMovimiento() == TipoMovimiento.TRASLADO || dto.getTipoMovimiento() == TipoMovimiento.SALIDA) {
                if (!item.getLocal().getIdLocal().equals(dto.getIdLocalOrigen())) {
                    throw new BusinessException(HttpStatus.CONFLICT, "ERR_SERIAL_UBICACION_INCORRECTA",
                            String.format("Inconsistencia de Almacén: El número de serie '%s' no se encuentra en el local origen seleccionado (Se encuentra en: %s).",
                                    item.getNumeroSerie(), item.getLocal().getNombre()));
                }
            }

            // 3. Control de Estado: No puedes trasladar una laptop que ya fue VENDIDA o dada de baja
            if (dto.getTipoMovimiento() == TipoMovimiento.TRASLADO && item.getEstadoDisponibilidad() != EstadoDisponibilidad.DISPONIBLE) {
                throw new BusinessException(HttpStatus.CONFLICT, "ERR_SERIAL_ESTADO_INVALIDO",
                        "No se puede trasladar el producto porque su estado actual es: " + item.getEstadoDisponibilidad());
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