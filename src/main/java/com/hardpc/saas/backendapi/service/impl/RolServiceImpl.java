package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.dto.RolDTO;
import com.hardpc.saas.backendapi.entity.Rol;
import com.hardpc.saas.backendapi.enums.RolNombre;
import com.hardpc.saas.backendapi.mapper.MaestroMapper;
import com.hardpc.saas.backendapi.repository.RolRepository;
import com.hardpc.saas.backendapi.service.RolService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RolServiceImpl implements RolService {

    private final RolRepository repository;
    private final MaestroMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<RolDTO> listarPaginado(String buscar, Pageable pageable) {
        Page<Rol> pagina = (buscar != null && !buscar.trim().isEmpty())
                ? repository.findByDescripcionContainingIgnoreCase(buscar, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolDTO> listarActivosParaCombo() {
        return mapper.toRolDTOList(repository.findByEstadoTrueOrderByNombreAsc());
    }

    @Override
    @Transactional(readOnly = true)
    public RolDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con el ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public RolDTO buscarPorNombre(RolNombre nombre) {
        return repository.findByNombre(nombre)
                .map(mapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con el nombre: " + nombre));
    }

    @Override
    @Transactional
    public RolDTO crear(RolDTO dto) {
        if (repository.existsByNombre(dto.getNombre())) {
            throw new IllegalArgumentException("Ya existe el rol: " + dto.getNombre());
        }

        Rol entidad = mapper.toEntity(dto);
        entidad.setIdRol(null);
        entidad.setEstado(true);

        return mapper.toDTO(repository.save(entidad));
    }

    @Override
    @Transactional
    public RolDTO actualizar(Long id, RolDTO dto) {
        Rol existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con el ID: " + id));

        if (repository.existsByNombreAndIdRolNot(dto.getNombre(), id)) {
            throw new IllegalArgumentException("Ya existe otro rol con el nombre: " + dto.getNombre());
        }

        mapper.updateEntity(dto, existente);
        return mapper.toDTO(repository.save(existente));
    }

    @Override
    @Transactional
    public void eliminarLogico(Long id) {
        Rol existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con el ID: " + id));
        existente.setEstado(false);
        repository.save(existente);
    }

    @Override
    public List<String> listarRolesEnum() {
        return Arrays.stream(RolNombre.values())
                .map(Enum::name)
                .toList();
    }
}