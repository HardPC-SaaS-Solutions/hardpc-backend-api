package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.dto.ClienteRequestDTO;
import com.hardpc.saas.backendapi.dto.ClienteResponseDTO;
import com.hardpc.saas.backendapi.entity.Cliente;
import com.hardpc.saas.backendapi.enums.TipoCliente;
import com.hardpc.saas.backendapi.exception.custom.BusinessException;
import com.hardpc.saas.backendapi.mapper.ClienteMapper;
import com.hardpc.saas.backendapi.repository.ClienteRepository;
import com.hardpc.saas.backendapi.service.ClienteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository repository;
    private final ClienteMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteResponseDTO> listarPaginado(String buscar, TipoCliente tipoCliente, Pageable pageable) {
        Page<Cliente> pagina = repository.buscarPaginadoAvanzado(buscar, tipoCliente, pageable);
        return pagina.map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public ClienteResponseDTO crear(ClienteRequestDTO dto) {
        validarEstructuraCliente(dto);
        validarUnicidad(dto, null);

        Cliente entidad = mapper.toEntity(dto);
        entidad.setIdPersona(null);
        entidad.setEstado(true);

        return mapper.toResponseDTO(repository.save(entidad));
    }

    @Override
    @Transactional
    public ClienteResponseDTO actualizar(Long id, ClienteRequestDTO dto) {
        Cliente existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + id));

        validarEstructuraCliente(dto);
        validarUnicidad(dto, id);

        mapper.updateEntity(dto, existente);

        return mapper.toResponseDTO(repository.save(existente));
    }

    @Override
    @Transactional
    public void eliminarLogico(Long id) {
        Cliente existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + id));
        existente.setEstado(false);
        repository.save(existente);
    }

    @Override
    @Transactional
    public void reactivar(Long id) {
        Cliente existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + id));
        existente.setEstado(true);
        repository.save(existente);
    }

    // --- Métodos Privados de Reglas de Negocio ---

    private void validarUnicidad(ClienteRequestDTO dto, Long idExistente) {
        boolean emailDuplicado = (idExistente == null)
                ? repository.existsByEmail(dto.getEmail())
                : repository.existsByEmailAndIdPersonaNot(dto.getEmail(), idExistente);
        if (emailDuplicado) {
            throw new BusinessException(HttpStatus.CONFLICT, "ERR_DUPLICATE_EMAIL", "El correo electrónico ya está registrado en otro cliente.");
        }

        boolean docDuplicado = (idExistente == null)
                ? repository.existsByNumeroDocumento(dto.getNumeroDocumento())
                : repository.existsByNumeroDocumentoAndIdPersonaNot(dto.getNumeroDocumento(), idExistente);
        if (docDuplicado) {
            throw new BusinessException(HttpStatus.CONFLICT, "ERR_DUPLICATE_DOCUMENT", "El número de documento ya está registrado.");
        }
    }

    private void validarEstructuraCliente(ClienteRequestDTO dto) {
        if (dto.getTipoCliente() == TipoCliente.EMPRESA) {
            if (dto.getRazonSocial() == null || dto.getRazonSocial().trim().isEmpty()) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_CLIENTE_EMPRESA_INVALID", "La Razón Social es obligatoria para un cliente de tipo EMPRESA.");
            }
            // 🧹 SANITIZACIÓN: Si es empresa, limpiamos los datos de persona natural
            dto.setNombres(null);
            dto.setApellidos(null);

        } else if (dto.getTipoCliente() == TipoCliente.PERSONA_NATURAL) {
            if (dto.getNombres() == null || dto.getNombres().trim().isEmpty() ||
                    dto.getApellidos() == null || dto.getApellidos().trim().isEmpty()) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_CLIENTE_NATURAL_INVALID", "Nombres y Apellidos son obligatorios para un cliente de tipo PERSONA NATURAL.");
            }
            // 🧹 SANITIZACIÓN: Si es persona natural, limpiamos la razón social
            dto.setRazonSocial(null);
        }
    }
}