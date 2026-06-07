package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.dto.UsuarioRequestDTO;
import com.hardpc.saas.backendapi.dto.UsuarioResponseDTO;
import com.hardpc.saas.backendapi.entity.Usuario;
import com.hardpc.saas.backendapi.exception.custom.BusinessException;
import com.hardpc.saas.backendapi.mapper.UsuarioMapper;
import com.hardpc.saas.backendapi.repository.UsuarioRepository;
import com.hardpc.saas.backendapi.service.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;
    private final UsuarioMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarPaginado(String buscar, Long idRol, Pageable pageable) {
        // Delegamos toda la lógica de filtrado dinámico al repositorio
        Page<Usuario> pagina = repository.buscarPaginadoAvanzado(buscar, idRol, pageable);
        return pagina.map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public UsuarioResponseDTO crear(UsuarioRequestDTO dto) {
        validarUnicidad(dto, null);

        // Validar contraseña obligatoria en creación
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "ERR_USER_PWD_REQUIRED", "La contraseña es obligatoria al crear un usuario.");
        }

        Usuario entidad = mapper.toEntity(dto);
        entidad.setIdPersona(null);
        entidad.setEstado(true);
        // Encriptar Password
        entidad.setPassword(passwordEncoder.encode(dto.getPassword()));

        return mapper.toResponseDTO(repository.save(entidad));
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO dto) {
        Usuario existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));

        validarUnicidad(dto, id);

        mapper.updateEntity(dto, existente);

        // Lógica de Password Segura: Solo encriptar si el admin envía una nueva
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            existente.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return mapper.toResponseDTO(repository.save(existente));
    }

    @Override
    @Transactional
    public void eliminarLogico(Long id) {
        Usuario existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
        existente.setEstado(false);
        repository.save(existente);
    }

    @Override
    @Transactional
    public void reactivar(Long id) {
        Usuario existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
        existente.setEstado(true);
        repository.save(existente);
    }

    // --- Métodos Privados de Reglas de Negocio ---
    private void validarUnicidad(UsuarioRequestDTO dto, Long idExistente) {
        boolean userDuplicado = (idExistente == null)
                ? repository.existsByUsername(dto.getUsername())
                : repository.existsByUsernameAndIdPersonaNot(dto.getUsername(), idExistente);
        if (userDuplicado) throw new BusinessException(HttpStatus.CONFLICT, "ERR_DUPLICATE_USERNAME", "El nombre de usuario ya está en uso.");

        boolean emailDuplicado = (idExistente == null)
                ? repository.existsByEmail(dto.getEmail())
                : repository.existsByEmailAndIdPersonaNot(dto.getEmail(), idExistente);
        if (emailDuplicado) throw new BusinessException(HttpStatus.CONFLICT, "ERR_DUPLICATE_EMAIL", "El correo electrónico ya está registrado.");

        boolean docDuplicado = (idExistente == null)
                ? repository.existsByNumeroDocumento(dto.getNumeroDocumento())
                : repository.existsByNumeroDocumentoAndIdPersonaNot(dto.getNumeroDocumento(), idExistente);
        if (docDuplicado) throw new BusinessException(HttpStatus.CONFLICT, "ERR_DUPLICATE_DOCUMENT", "El número de documento ya está registrado.");
    }
}