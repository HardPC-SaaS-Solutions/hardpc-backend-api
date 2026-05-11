package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.dto.MarcaDTO;
import com.hardpc.saas.backendapi.entity.Marca;
import com.hardpc.saas.backendapi.mapper.MaestroMapper;
import com.hardpc.saas.backendapi.repository.MarcaRepository;
import com.hardpc.saas.backendapi.service.MarcaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarcaServiceImpl implements MarcaService {

    private final MarcaRepository repository;
    private final MaestroMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<MarcaDTO> listarPaginado(String buscar, Pageable pageable) {
        Page<Marca> pagina = (buscar != null && !buscar.trim().isEmpty())
                ? repository.findByNombreContainingIgnoreCase(buscar, pageable)
                : repository.findAll(pageable);

        return pagina.map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarcaDTO> listarActivosParaCombo() {
        return mapper.toMarcaDTOList(repository.findByEstadoTrueOrderByNombreAsc());
    }

    @Override
    @Transactional(readOnly = true)
    public MarcaDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Marca no encontrada con el ID: " + id));
    }

    @Override
    @Transactional
    public MarcaDTO crear(MarcaDTO dto) {
        if (repository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new IllegalArgumentException("Ya existe una marca con el nombre: " + dto.getNombre());
        }

        Marca entidad = mapper.toEntity(dto);
        entidad.setIdMarca(null);
        return mapper.toDTO(repository.save(entidad));
    }

    @Override
    @Transactional
    public MarcaDTO actualizar(Long id, MarcaDTO dto) {
        Marca existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Marca no encontrada con el ID: " + id));

        if (repository.existsByNombreIgnoreCaseAndIdMarcaNot(dto.getNombre(), id)) {
            throw new IllegalArgumentException("Ya existe otra marca con el nombre: " + dto.getNombre());
        }

        mapper.updateEntity(dto, existente);

        return mapper.toDTO(repository.save(existente));
    }

    @Override
    @Transactional
    public void eliminarLogico(Long id) {
        Marca existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Marca no encontrada con el ID: " + id));

        existente.setEstado(false);
        repository.save(existente);
    }
}