package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.dto.FormaPagoDTO;
import com.hardpc.saas.backendapi.entity.FormaPago;
import com.hardpc.saas.backendapi.mapper.MaestroMapper;
import com.hardpc.saas.backendapi.repository.FormaPagoRepository;
import com.hardpc.saas.backendapi.service.FormaPagoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FormaPagoServiceImpl implements FormaPagoService {

    private final FormaPagoRepository repository;
    private final MaestroMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<FormaPagoDTO> listarPaginado(String buscar, Pageable pageable) {
        Page<FormaPago> pagina = (buscar != null && !buscar.trim().isEmpty())
                ? repository.findByDescripcionContainingIgnoreCase(buscar, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormaPagoDTO> listarActivosParaCombo() {
        return mapper.toFormaPagoDTOList(repository.findByEstadoTrueOrderByDescripcionAsc());
    }

    @Override
    @Transactional(readOnly = true)
    public FormaPagoDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Forma de Pago no encontrada con el ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public FormaPagoDTO buscarPorDescripcion(String descripcion) {
        return repository.findByDescripcionIgnoreCase(descripcion)
                .map(mapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Forma de pago no encontrada con la descripción: " + descripcion));
    }

    @Override
    @Transactional
    public FormaPagoDTO crear(FormaPagoDTO dto) {
        if (repository.existsByDescripcionIgnoreCase(dto.getDescripcion())) {
            throw new IllegalArgumentException("Ya existe una forma de pago con la descripción: " + dto.getDescripcion());
        }

        FormaPago entidad = mapper.toEntity(dto);
        entidad.setIdFormaPago(null);
        entidad.setEstado(true);

        return mapper.toDTO(repository.save(entidad));
    }

    @Override
    @Transactional
    public FormaPagoDTO actualizar(Long id, FormaPagoDTO dto) {
        FormaPago existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Forma de Pago no encontrada con el ID: " + id));

        if (repository.existsByDescripcionIgnoreCaseAndIdFormaPagoNot(dto.getDescripcion(), id)) {
            throw new IllegalArgumentException("Ya existe otra forma de pago con la descripción: " + dto.getDescripcion());
        }

        mapper.updateEntity(dto, existente);

        return mapper.toDTO(repository.save(existente));
    }

    @Override
    @Transactional
    public void eliminarLogico(Long id) {
        FormaPago existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Forma de Pago no encontrada con el ID: " + id));
        existente.setEstado(false);
        repository.save(existente);
    }
}