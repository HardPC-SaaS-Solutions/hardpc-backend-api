package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.Persona;
import com.hardpc.saas.backendapi.service.PersonaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonaRestControllerTest {

    @Mock
    private PersonaService personaService;

    @InjectMocks
    private PersonaRestController personaRestController;

    @Test
    void listarOk() {
        Persona p = mock(Persona.class);
        when(personaService.listarTodos()).thenReturn(List.of(p));

        ResponseEntity<List<Persona>> response = personaRestController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(personaService).listarTodos();
    }

    @Test
    void buscarPorIdOk() {
        Persona p = mock(Persona.class);
        when(personaService.buscarPorId(1L)).thenReturn(p);

        ResponseEntity<Persona> response = personaRestController.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(personaService).buscarPorId(1L);
    }

    @Test
    void eliminarOk() {
        ResponseEntity<Void> response = personaRestController.eliminar(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(personaService).eliminar(1L);
    }
}