package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.Rol;
import com.hardpc.saas.backendapi.service.RolService;
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
class RolRestControllerTest {

    @Mock
    private RolService rolService;

    @InjectMocks
    private RolRestController rolRestController;

    @Test
    void listarOk() {
        when(rolService.listarTodos()).thenReturn(List.of(new Rol()));
        ResponseEntity<List<Rol>> response = rolRestController.listar();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(rolService).listarTodos();
    }

    @Test
    void buscarPorIdOk() {
        Rol rol = new Rol();
        rol.setIdRol(1L);
        when(rolService.buscarPorId(1L)).thenReturn(rol);

        ResponseEntity<Rol> response = rolRestController.buscarPorId(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(rolService).buscarPorId(1L);
    }

    @Test
    void crearOk() {
        Rol entrada = new Rol();
        Rol guardado = new Rol();
        guardado.setIdRol(1L);
        when(rolService.crear(entrada)).thenReturn(guardado);

        ResponseEntity<Rol> response = rolRestController.crear(entrada);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/api/roles/1", response.getHeaders().getLocation().toString());
    }

    @Test
    void actualizarOk() {
        Rol entrada = new Rol();
        when(rolService.actualizar(1L, entrada)).thenReturn(entrada);

        ResponseEntity<Rol> response = rolRestController.actualizar(1L, entrada);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void eliminarOk() {
        ResponseEntity<Void> response = rolRestController.eliminar(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(rolService).eliminar(1L);
    }
}