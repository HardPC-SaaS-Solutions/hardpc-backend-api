package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.ItemSerial;
import com.hardpc.saas.backendapi.repository.ItemSerialRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ItemSerialServiceTest {

    private final ItemSerialRepository repository = Mockito.mock(ItemSerialRepository.class);
    private final ItemSerialService service = new ItemSerialService(repository);

    @Test
    void guardar() {
        ItemSerial item = new ItemSerial();
        item.setIdItemSerial(1L);

        Mockito.when(repository.save(item)).thenReturn(item);

        assertNotNull(service.guardar(item));
    }

    @Test
    void buscar() {
        ItemSerial item = new ItemSerial();
        item.setIdItemSerial(1L);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(item));

        assertNotNull(service.buscar(1L));
    }
}