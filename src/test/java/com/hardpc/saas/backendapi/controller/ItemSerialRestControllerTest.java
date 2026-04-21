package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.ItemSerial;
import com.hardpc.saas.backendapi.service.ItemSerialService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemSerialRestControllerTest {

    @Mock
    private ItemSerialService itemSerialService;

    @InjectMocks
    private ItemSerialRestController itemSerialRestController;

    @Test
    void listarOk() {
        when(itemSerialService.listarTodos()).thenReturn(List.of(new ItemSerial()));
        ResponseEntity<List<ItemSerial>> response = itemSerialRestController.listar();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(itemSerialService).listarTodos();
    }

    @Test
    void buscarPorIdOk() {
        ItemSerial item = new ItemSerial();
        item.setIdItemSerial(1L);
        when(itemSerialService.buscarPorId(1L)).thenReturn(item);

        ResponseEntity<ItemSerial> response = itemSerialRestController.buscarPorId(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(itemSerialService).buscarPorId(1L);
    }

    @Test
    void crearOk() {
        ItemSerial entrada = new ItemSerial();
        ItemSerial guardado = new ItemSerial();
        guardado.setIdItemSerial(1L);
        when(itemSerialService.crear(entrada)).thenReturn(guardado);

        ResponseEntity<ItemSerial> response = itemSerialRestController.crear(entrada);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/api/items-seriales/1", response.getHeaders().getLocation().toString());
    }

    @Test
    void actualizarOk() {
        ItemSerial entrada = new ItemSerial();
        when(itemSerialService.actualizar(1L, entrada)).thenReturn(entrada);

        ResponseEntity<ItemSerial> response = itemSerialRestController.actualizar(1L, entrada);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void eliminarOk() {
        ResponseEntity<Void> response = itemSerialRestController.eliminar(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(itemSerialService).eliminar(1L);
    }
}