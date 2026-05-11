package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.dto.CategoriaDTO;
import com.hardpc.saas.backendapi.entity.Categoria;
import com.hardpc.saas.backendapi.mapper.MaestroMapper;
import com.hardpc.saas.backendapi.repository.CategoriaRepository;
import com.hardpc.saas.backendapi.service.CategoriaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository repository;
    private final MaestroMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<CategoriaDTO> listarPaginado(String buscar, Pageable pageable) {
        Page<Categoria> pagina = (buscar != null && !buscar.trim().isEmpty())
                ? repository.findByNombreContainingIgnoreCase(buscar, pageable)
                : repository.findAll(pageable);

        return pagina.map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> listarActivosParaCombo() {
        return mapper.toCategoriaDTOList(repository.findByEstadoTrueOrderByNombreAsc());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con el ID: " + id));
    }

    @Override
    @Transactional
    public CategoriaDTO crear(CategoriaDTO dto) {
        if (repository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + dto.getNombre());
        }

        Categoria entidad = mapper.toEntity(dto);
        entidad.setIdCategoria(null);
        return mapper.toDTO(repository.save(entidad));
    }

    @Override
    @Transactional
    public CategoriaDTO actualizar(Long id, CategoriaDTO dto) {
        Categoria existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con el ID: " + id));

        if (repository.existsByNombreIgnoreCaseAndIdCategoriaNot(dto.getNombre(), id)) {
            throw new IllegalArgumentException("Ya existe otra categoría con el nombre: " + dto.getNombre());
        }

        mapper.updateEntity(dto, existente);

        return mapper.toDTO(repository.save(existente));
    }

    @Override
    @Transactional
    public void eliminarLogico(Long id) {
        Categoria existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con el ID: " + id));

        existente.setEstado(false);
        repository.save(existente);
    }
}