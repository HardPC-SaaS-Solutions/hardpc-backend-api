package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.entity.ItemSerial;
import com.hardpc.saas.backendapi.repository.ItemSerialRepository;
import com.hardpc.saas.backendapi.service.ItemSerialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemSerialServiceImpl implements ItemSerialService {

    private final ItemSerialRepository itemSerialRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ItemSerial> listarTodos() {
        return itemSerialRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemSerial buscarPorId(Long id) {
        return itemSerialRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item serial no encontrado con id: " + id));
    }

    @Override
    public ItemSerial crear(ItemSerial itemSerial) {
        itemSerial.setIdItemSerial(null);
        return itemSerialRepository.save(itemSerial);
    }

    @Override
    public ItemSerial actualizar(Long id, ItemSerial itemSerial) {
        ItemSerial existente = buscarPorId(id);

        existente.setNumeroSerie(itemSerial.getNumeroSerie());
        existente.setCondicion(itemSerial.getCondicion());
        existente.setEstadoDisponibilidad(itemSerial.getEstadoDisponibilidad());
        existente.setFechaFinGarantia(itemSerial.getFechaFinGarantia());
        existente.setProducto(itemSerial.getProducto());
        existente.setLocal(itemSerial.getLocal());
        existente.setIdDetalleIngreso(itemSerial.getIdDetalleIngreso());

        return itemSerialRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        ItemSerial existente = buscarPorId(id);
        itemSerialRepository.delete(existente);
    }
}