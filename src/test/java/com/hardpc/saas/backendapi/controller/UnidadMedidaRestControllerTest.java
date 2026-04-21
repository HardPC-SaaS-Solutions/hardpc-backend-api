package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.UnidadMedida;
import com.hardpc.saas.backendapi.service.UnidadMedidaService;
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
class UnidadMedidaRestControllerTest {

    @Mock
    private UnidadMedidaService unidadMedidaService;

    @InjectMocks
    private UnidadMedidaRestController unidadMedidaRestController;

    @Test
    void listarOk() {
        when(unidadMedidaService.listarTodos()).thenReturn(List.of(new UnidadMedida()));
        ResponseEntity<List<UnidadMedida>> response = unidadMedidaRestController.listar();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(unidadMedidaService).listarTodos();
    }

    @Test
    void buscarPorIdOk() {
        UnidadMedida unidad = new UnidadMedida();
        unidad.setIdUnidadMedida(1L);
        when(unidadMedidaService.buscarPorId(1L)).thenReturn(unidad);

        ResponseEntity<UnidadMedida> response = unidadMedidaRestController.buscarPorId(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(unidadMedidaService).buscarPorId(1L);
    }

    @Test
    void crearOk() {
        UnidadMedida entrada = new UnidadMedida();
        UnidadMedida guardada = new UnidadMedida();
        guardada.setIdUnidadMedida(1L);
        when(unidadMedidaService.crear(entrada)).thenReturn(guardada);

        ResponseEntity<UnidadMedida> response = unidadMedidaRestController.crear(entrada);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/api/unidades-medida/1", response.getHeaders().getLocation().toString());
    }

    @Test
    void actualizarOk() {
        UnidadMedida entrada = new UnidadMedida();
        when(unidadMedidaService.actualizar(1L, entrada)).thenReturn(entrada);

        ResponseEntity<UnidadMedida> response = unidadMedidaRestController.actualizar(1L, entrada);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void eliminarOk() {
        ResponseEntity<Void> response = unidadMedidaRestController.eliminar(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(unidadMedidaService).eliminar(1L);
    }
}