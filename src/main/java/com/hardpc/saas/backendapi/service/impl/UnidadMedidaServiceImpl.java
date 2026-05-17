package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.dto.UnidadMedidaDTO;
import com.hardpc.saas.backendapi.entity.UnidadMedida;
import com.hardpc.saas.backendapi.mapper.MaestroMapper;
import com.hardpc.saas.backendapi.repository.UnidadMedidaRepository;
import com.hardpc.saas.backendapi.service.UnidadMedidaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnidadMedidaServiceImpl implements UnidadMedidaService {

    private final UnidadMedidaRepository repository;
    private final MaestroMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<UnidadMedidaDTO> listarPaginado(String buscar, Pageable pageable) {
        Page<UnidadMedida> pagina = (buscar != null && !buscar.trim().isEmpty())
                ? repository.findByDescripcionContainingIgnoreCase(buscar, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnidadMedidaDTO> listarActivosParaCombo() {
        return mapper.toUnidadMedidaDTOList(repository.findByEstadoTrueOrderByDescripcionAsc());
    }

    @Override
    @Transactional(readOnly = true)
    public UnidadMedidaDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Unidad de Medida no encontrada con el ID: " + id));
    }

    @Override
    @Transactional
    public UnidadMedidaDTO crear(UnidadMedidaDTO dto) {
        if (repository.existsByDescripcionIgnoreCase(dto.getDescripcion())) {
            throw new IllegalArgumentException("Ya existe una unidad con la descripción: " + dto.getDescripcion());
        }
        if (repository.existsByAbreviaturaIgnoreCase(dto.getAbreviatura())) {
            throw new IllegalArgumentException("Ya existe una unidad con la abreviatura: " + dto.getAbreviatura());
        }

        UnidadMedida entidad = mapper.toEntity(dto);
        entidad.setIdUnidadMedida(null);
        entidad.setEstado(true);

        return mapper.toDTO(repository.save(entidad));
    }

    @Override
    @Transactional
    public UnidadMedidaDTO actualizar(Long id, UnidadMedidaDTO dto) {
        UnidadMedida existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Unidad de Medida no encontrada con el ID: " + id));

        if (repository.existsByDescripcionIgnoreCaseAndIdUnidadMedidaNot(dto.getDescripcion(), id)) {
            throw new IllegalArgumentException("Ya existe otra unidad con la descripción: " + dto.getDescripcion());
        }
        if (repository.existsByAbreviaturaIgnoreCaseAndIdUnidadMedidaNot(dto.getAbreviatura(), id)) {
            throw new IllegalArgumentException("Ya existe otra unidad con la abreviatura: " + dto.getAbreviatura());
        }

        mapper.updateEntity(dto, existente);

        return mapper.toDTO(repository.save(existente));
    }

    @Override
    @Transactional
    public void eliminarLogico(Long id) {
        UnidadMedida existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Unidad de Medida no encontrada con el ID: " + id));
        existente.setEstado(false);
        repository.save(existente);
    }
}