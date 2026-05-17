package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.dto.TipoDocumentoDTO;
import com.hardpc.saas.backendapi.entity.TipoDocumento;
import com.hardpc.saas.backendapi.mapper.MaestroMapper;
import com.hardpc.saas.backendapi.repository.TipoDocumentoRepository;
import com.hardpc.saas.backendapi.service.TipoDocumentoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoDocumentoServiceImpl implements TipoDocumentoService {

    private final TipoDocumentoRepository repository;
    private final MaestroMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<TipoDocumentoDTO> listarPaginado(String buscar, Pageable pageable) {
        Page<TipoDocumento> pagina = (buscar != null && !buscar.trim().isEmpty())
                ? repository.findByNombreContainingIgnoreCase(buscar, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoDocumentoDTO> listarActivosParaCombo() {
        return mapper.toTipoDocumentoDTOList(repository.findByEstadoTrueOrderByNombreAsc());
    }

    @Override
    @Transactional(readOnly = true)
    public TipoDocumentoDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de Documento no encontrado con el ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public TipoDocumentoDTO buscarPorAbreviatura(String abreviatura) {
        return repository.findByAbreviaturaIgnoreCase(abreviatura)
                .map(mapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de Documento no encontrado con la abreviatura: " + abreviatura));
    }

    @Override
    @Transactional
    public TipoDocumentoDTO crear(TipoDocumentoDTO dto) {
        validarReglasNegocio(dto, null);

        TipoDocumento entidad = mapper.toEntity(dto);
        entidad.setIdTipoDocumento(null);
        entidad.setEstado(true);

        return mapper.toDTO(repository.save(entidad));
    }

    @Override
    @Transactional
    public TipoDocumentoDTO actualizar(Long id, TipoDocumentoDTO dto) {
        TipoDocumento existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de Documento no encontrado con el ID: " + id));

        validarReglasNegocio(dto, id);
        mapper.updateEntity(dto, existente);

        return mapper.toDTO(repository.save(existente));
    }

    @Override
    @Transactional
    public void eliminarLogico(Long id) {
        TipoDocumento existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de Documento no encontrado con el ID: " + id));
        existente.setEstado(false);
        repository.save(existente);
    }

    private void validarReglasNegocio(TipoDocumentoDTO dto, Long idExistente) {
        // 1. Validaciones de Duplicidad
        boolean nombreDuplicado = (idExistente == null)
                ? repository.existsByNombreIgnoreCase(dto.getNombre())
                : repository.existsByNombreIgnoreCaseAndIdTipoDocumentoNot(dto.getNombre(), idExistente);
        if (nombreDuplicado) throw new IllegalArgumentException("Ya existe un documento con el nombre: " + dto.getNombre());

        boolean abreviaturaDuplicada = (idExistente == null)
                ? repository.existsByAbreviaturaIgnoreCase(dto.getAbreviatura())
                : repository.existsByAbreviaturaIgnoreCaseAndIdTipoDocumentoNot(dto.getAbreviatura(), idExistente);
        if (abreviaturaDuplicada) throw new IllegalArgumentException("Ya existe un documento con la abreviatura: " + dto.getAbreviatura());

        // 2. Lógica Específica del Dominio (Contabilidad Perú)
        String abrevUpper = dto.getAbreviatura().toUpperCase();
        if (abrevUpper.equals("DNI") && dto.getLongitudExacta() != 8) {
            throw new IllegalArgumentException("Un DNI peruano debe tener una longitud exacta de 8 dígitos.");
        }
        if (abrevUpper.equals("RUC") && dto.getLongitudExacta() != 11) {
            throw new IllegalArgumentException("Un RUC peruano debe tener una longitud exacta de 11 dígitos.");
        }
    }
}