package com.hardpc.saas.backendapi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hardpc.saas.backendapi.entity.ItemSerial;
import com.hardpc.saas.backendapi.repository.ItemSerialRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ItemSerialServiceImplTest {

    @Mock
    private ItemSerialRepository itemSerialRepository;

    @InjectMocks
    private ItemSerialServiceImpl itemSerialService;

    @Test
    void listarTodosRetornaDatosDelRepositorio() {
        ItemSerial item = new ItemSerial();
        item.setIdItemSerial(1L);
        when(itemSerialRepository.findAll()).thenReturn(List.of(item));

        List<ItemSerial> resultado = itemSerialService.listarTodos();

        assertFalse(resultado.isEmpty());
        verify(itemSerialRepository).findAll();
    }

    @Test
    void buscarPorIdRetornaEntidadCuandoExiste() {
        ItemSerial item = new ItemSerial();
        item.setIdItemSerial(1L);
        when(itemSerialRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemSerial resultado = itemSerialService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdItemSerial());
    }

    @Test
    void buscarPorIdLanzaExcepcionCuandoNoExiste() {
        when(itemSerialRepository.findById(99L)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> itemSerialService.buscarPorId(99L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void crearFuerzaIdNuloAntesDeGuardar() {
        ItemSerial item = new ItemSerial();
        item.setIdItemSerial(99L);
        when(itemSerialRepository.save(any(ItemSerial.class))).thenReturn(item);

        itemSerialService.crear(item);

        assertNull(item.getIdItemSerial());
        verify(itemSerialRepository).save(item);
    }

    @Test
    void actualizarCambiaCamposYGuarda() {
        ItemSerial original = new ItemSerial();
        original.setIdItemSerial(1L);
        original.setNumeroSerie("ABC-123");

        ItemSerial nuevosDatos = new ItemSerial();
        nuevosDatos.setNumeroSerie("XYZ-789");

        when(itemSerialRepository.findById(1L)).thenReturn(Optional.of(original));
        when(itemSerialRepository.save(original)).thenReturn(original);

        itemSerialService.actualizar(1L, nuevosDatos);

        assertEquals("XYZ-789", original.getNumeroSerie());
        verify(itemSerialRepository).save(original);
    }

    @Test
    void actualizarLanzaExcepcionCuandoNoExiste() {
        when(itemSerialRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> itemSerialService.actualizar(99L, new ItemSerial()));
    }

    @Test
    void eliminarBorraEntidadExistente() {
        ItemSerial item = new ItemSerial();
        item.setIdItemSerial(1L);
        when(itemSerialRepository.findById(1L)).thenReturn(Optional.of(item));

        itemSerialService.eliminar(1L);

        verify(itemSerialRepository).delete(item);
    }
}