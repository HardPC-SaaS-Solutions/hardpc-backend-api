package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.Local;
import com.hardpc.saas.backendapi.service.LocalService;
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
class LocalRestControllerTest {

    @Mock
    private LocalService localService;

    @InjectMocks
    private LocalRestController localRestController;

    @Test
    void listarOk() {
        when(localService.listarTodos()).thenReturn(List.of(new Local()));
        ResponseEntity<List<Local>> response = localRestController.listar();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(localService).listarTodos();
    }

    @Test
    void buscarPorIdOk() {
        Local local = new Local();
        local.setIdLocal(1L);
        when(localService.buscarPorId(1L)).thenReturn(local);

        ResponseEntity<Local> response = localRestController.buscarPorId(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(localService).buscarPorId(1L);
    }

    @Test
    void crearOk() {
        Local entrada = new Local();
        Local guardado = new Local();
        guardado.setIdLocal(1L);
        when(localService.crear(entrada)).thenReturn(guardado);

        ResponseEntity<Local> response = localRestController.crear(entrada);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/api/locales/1", response.getHeaders().getLocation().toString());
    }

    @Test
    void actualizarOk() {
        Local entrada = new Local();
        when(localService.actualizar(1L, entrada)).thenReturn(entrada);

        ResponseEntity<Local> response = localRestController.actualizar(1L, entrada);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void eliminarOk() {
        ResponseEntity<Void> response = localRestController.eliminar(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(localService).eliminar(1L);
    }
}