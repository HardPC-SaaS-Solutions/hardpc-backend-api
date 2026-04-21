package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.Marca;
import com.hardpc.saas.backendapi.service.MarcaService;
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
class MarcaRestControllerTest {

    @Mock
    private MarcaService marcaService;

    @InjectMocks
    private MarcaRestController marcaRestController;

    @Test
    void listarOk() {
        when(marcaService.listarTodos()).thenReturn(List.of(new Marca()));
        ResponseEntity<List<Marca>> response = marcaRestController.listar();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(marcaService).listarTodos();
    }

    @Test
    void buscarPorIdOk() {
        Marca marca = new Marca();
        marca.setIdMarca(1L);
        when(marcaService.buscarPorId(1L)).thenReturn(marca);

        ResponseEntity<Marca> response = marcaRestController.buscarPorId(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(marcaService).buscarPorId(1L);
    }

    @Test
    void crearOk() {
        Marca entrada = new Marca();
        Marca guardada = new Marca();
        guardada.setIdMarca(1L);
        when(marcaService.crear(entrada)).thenReturn(guardada);

        ResponseEntity<Marca> response = marcaRestController.crear(entrada);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/api/marcas/1", response.getHeaders().getLocation().toString());
    }

    @Test
    void actualizarOk() {
        Marca entrada = new Marca();
        when(marcaService.actualizar(1L, entrada)).thenReturn(entrada);

        ResponseEntity<Marca> response = marcaRestController.actualizar(1L, entrada);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void eliminarOk() {
        ResponseEntity<Void> response = marcaRestController.eliminar(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(marcaService).eliminar(1L);
    }
}