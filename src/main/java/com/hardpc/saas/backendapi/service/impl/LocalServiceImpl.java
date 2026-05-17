package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.dto.LocalDTO;
import com.hardpc.saas.backendapi.entity.Local;
import com.hardpc.saas.backendapi.mapper.MaestroMapper;
import com.hardpc.saas.backendapi.repository.LocalRepository;
import com.hardpc.saas.backendapi.service.LocalService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocalServiceImpl implements LocalService {

    private final LocalRepository repository;
    private final MaestroMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<LocalDTO> listarPaginado(String buscar, Pageable pageable) {
        Page<Local> pagina = (buscar != null && !buscar.trim().isEmpty())
                ? repository.findByNombreContainingIgnoreCase(buscar, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocalDTO> listarActivosParaCombo() {
        return mapper.toLocalDTOList(repository.findByEstadoTrueOrderByNombreAsc());
    }

    @Override
    @Transactional(readOnly = true)
    public LocalDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Local no encontrado con el ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public LocalDTO buscarPorNombre(String nombre) {
        return repository.findByNombreIgnoreCase(nombre)
                .map(mapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Local no encontrado con el nombre: " + nombre));
    }

    @Override
    @Transactional
    public LocalDTO crear(LocalDTO dto) {
        validarReglasNegocio(dto, null);

        Local entidad = mapper.toEntity(dto);
        entidad.setIdLocal(null);
        entidad.setEstado(true);

        return mapper.toDTO(repository.save(entidad));
    }

    @Override
    @Transactional
    public LocalDTO actualizar(Long id, LocalDTO dto) {
        Local existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Local no encontrado con el ID: " + id));

        validarReglasNegocio(dto, id);
        mapper.updateEntity(dto, existente);

        return mapper.toDTO(repository.save(existente));
    }

    @Override
    @Transactional
    public void eliminarLogico(Long id) {
        Local existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Local no encontrado con el ID: " + id));
        existente.setEstado(false);
        repository.save(existente);
    }

    private void validarReglasNegocio(LocalDTO dto, Long idExistente) {
        boolean nombreDuplicado = (idExistente == null)
                ? repository.existsByNombreIgnoreCase(dto.getNombre())
                : repository.existsByNombreIgnoreCaseAndIdLocalNot(dto.getNombre(), idExistente);
        if (nombreDuplicado) throw new IllegalArgumentException("Ya existe un local con el nombre: " + dto.getNombre());

        boolean direccionDuplicada = (idExistente == null)
                ? repository.existsByDireccionIgnoreCase(dto.getDireccion())
                : repository.existsByDireccionIgnoreCaseAndIdLocalNot(dto.getDireccion(), idExistente);
        if (direccionDuplicada) throw new IllegalArgumentException("Ya existe otro local registrado en esta dirección exacta.");
    }
}