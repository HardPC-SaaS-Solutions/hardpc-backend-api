package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.dto.ProveedorRequestDTO;
import com.hardpc.saas.backendapi.dto.ProveedorResponseDTO;
import com.hardpc.saas.backendapi.entity.Proveedor;
import com.hardpc.saas.backendapi.exception.custom.BusinessException;
import com.hardpc.saas.backendapi.mapper.ProveedorMapper;
import com.hardpc.saas.backendapi.repository.ProveedorRepository;
import com.hardpc.saas.backendapi.service.ProveedorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository repository;
    private final ProveedorMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ProveedorResponseDTO> listarPaginado(String buscar, Pageable pageable) {
        Page<Proveedor> pagina = (buscar != null && !buscar.trim().isEmpty())
                ? repository.buscarPaginado(buscar, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorResponseDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public ProveedorResponseDTO crear(ProveedorRequestDTO dto) {
        validarUnicidad(dto, null);

        Proveedor entidad = mapper.toEntity(dto);
        entidad.setIdProveedor(null);
        entidad.setEstado(true);

        return mapper.toResponseDTO(repository.save(entidad));
    }

    @Override
    @Transactional
    public ProveedorResponseDTO actualizar(Long id, ProveedorRequestDTO dto) {
        Proveedor existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado con ID: " + id));

        validarUnicidad(dto, id);

        mapper.updateEntity(dto, existente);

        return mapper.toResponseDTO(repository.save(existente));
    }

    @Override
    @Transactional
    public void eliminarLogico(Long id) {
        Proveedor existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado con ID: " + id));
        existente.setEstado(false);
        repository.save(existente);
    }

    @Override
    @Transactional
    public void reactivar(Long id) {
        Proveedor existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado con ID: " + id));
        existente.setEstado(true);
        repository.save(existente);
    }

    // --- Métodos Privados de Reglas de Negocio ---

    private void validarUnicidad(ProveedorRequestDTO dto, Long idExistente) {
        // Validación de RUC
        boolean rucDuplicado = (idExistente == null)
                ? repository.existsByRuc(dto.getRuc())
                : repository.existsByRucAndIdProveedorNot(dto.getRuc(), idExistente);
        if (rucDuplicado) throw new BusinessException(HttpStatus.CONFLICT, "ERR_DUPLICATE_RUC", "El RUC ya se encuentra registrado.");

        // Validación de Razón Social
        boolean rsDuplicada = (idExistente == null)
                ? repository.existsByRazonSocialIgnoreCase(dto.getRazonSocial())
                : repository.existsByRazonSocialIgnoreCaseAndIdProveedorNot(dto.getRazonSocial(), idExistente);
        if (rsDuplicada) throw new BusinessException(HttpStatus.CONFLICT, "ERR_DUPLICATE_RAZON_SOCIAL", "La Razón Social ya se encuentra registrada.");

        // Validación de Email
        boolean emailDuplicado = (idExistente == null)
                ? repository.existsByEmailIgnoreCase(dto.getEmail())
                : repository.existsByEmailIgnoreCaseAndIdProveedorNot(dto.getEmail(), idExistente);
        if (emailDuplicado) throw new BusinessException(HttpStatus.CONFLICT, "ERR_DUPLICATE_EMAIL", "El correo electrónico ya está registrado en otro proveedor.");

        // Validación Opcional de Nombre Comercial
        if (dto.getNombreComercial() != null && !dto.getNombreComercial().trim().isEmpty()) {
            boolean ncDuplicado = (idExistente == null)
                    ? repository.existsByNombreComercialIgnoreCase(dto.getNombreComercial())
                    : repository.existsByNombreComercialIgnoreCaseAndIdProveedorNot(dto.getNombreComercial(), idExistente);
            if (ncDuplicado) throw new BusinessException(HttpStatus.CONFLICT, "ERR_DUPLICATE_NOMBRE_COMERCIAL", "El Nombre Comercial ya se encuentra registrado.");
        }
    }
}