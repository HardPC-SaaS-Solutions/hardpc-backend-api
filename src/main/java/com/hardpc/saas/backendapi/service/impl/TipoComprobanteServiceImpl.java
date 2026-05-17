package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.dto.TipoComprobanteDTO;
import com.hardpc.saas.backendapi.entity.TipoComprobante;
import com.hardpc.saas.backendapi.mapper.MaestroMapper;
import com.hardpc.saas.backendapi.repository.TipoComprobanteRepository;
import com.hardpc.saas.backendapi.service.TipoComprobanteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoComprobanteServiceImpl implements TipoComprobanteService {

    private final TipoComprobanteRepository repository;
    private final MaestroMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<TipoComprobanteDTO> listarPaginado(String buscar, Pageable pageable) {
        Page<TipoComprobante> pagina = (buscar != null && !buscar.trim().isEmpty())
                ? repository.findByDescripcionContainingIgnoreCase(buscar, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoComprobanteDTO> listarActivosParaCombo() {
        return mapper.toTipoComprobanteDTOList(repository.findByEstadoTrueOrderByDescripcionAsc());
    }

    @Override
    @Transactional(readOnly = true)
    public TipoComprobanteDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de Comprobante no encontrado con el ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public TipoComprobanteDTO buscarPorCodigoSunat(String codigoSunat) {
        return repository.findByCodigoSunatIgnoreCase(codigoSunat)
                .map(mapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Comprobante no encontrado con el código SUNAT: " + codigoSunat));
    }

    @Override
    @Transactional
    public TipoComprobanteDTO crear(TipoComprobanteDTO dto) {
        validarReglasNegocio(dto, null);

        TipoComprobante entidad = mapper.toEntity(dto);
        entidad.setIdTipoComprobante(null);
        entidad.setEstado(true);

        return mapper.toDTO(repository.save(entidad));
    }

    @Override
    @Transactional
    public TipoComprobanteDTO actualizar(Long id, TipoComprobanteDTO dto) {
        TipoComprobante existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de Comprobante no encontrado con el ID: " + id));

        validarReglasNegocio(dto, id);
        mapper.updateEntity(dto, existente);

        return mapper.toDTO(repository.save(existente));
    }

    @Override
    @Transactional
    public void eliminarLogico(Long id) {
        TipoComprobante existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de Comprobante no encontrado con el ID: " + id));
        existente.setEstado(false);
        repository.save(existente);
    }

    // Método privado centralizado de validaciones
    private void validarReglasNegocio(TipoComprobanteDTO dto, Long idExistente) {
        boolean descDuplicada = (idExistente == null)
                ? repository.existsByDescripcionIgnoreCase(dto.getDescripcion())
                : repository.existsByDescripcionIgnoreCaseAndIdTipoComprobanteNot(dto.getDescripcion(), idExistente);

        if (descDuplicada) throw new IllegalArgumentException("Ya existe un comprobante con la descripción: " + dto.getDescripcion());

        boolean codigoDuplicado = (idExistente == null)
                ? repository.existsByCodigoSunatIgnoreCase(dto.getCodigoSunat())
                : repository.existsByCodigoSunatIgnoreCaseAndIdTipoComprobanteNot(dto.getCodigoSunat(), idExistente);

        if (codigoDuplicado) throw new IllegalArgumentException("Ya existe un comprobante con el código SUNAT: " + dto.getCodigoSunat());
    }
}