package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.DetalleVenta;
import com.hardpc.saas.backendapi.service.DetalleVentaService;
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
class DetalleVentaRestControllerTest {

    @Mock
    private DetalleVentaService detalleVentaService;

    @InjectMocks
    private DetalleVentaRestController detalleVentaRestController;

    @Test
    void listarOk() {
        when(detalleVentaService.listarTodos()).thenReturn(List.of(new DetalleVenta()));
        ResponseEntity<List<DetalleVenta>> response = detalleVentaRestController.listar();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(detalleVentaService).listarTodos();
    }

    @Test
    void buscarPorIdOk() {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setIdDetalleVenta(1L);
        when(detalleVentaService.buscarPorId(1L)).thenReturn(detalle);

        ResponseEntity<DetalleVenta> response = detalleVentaRestController.buscarPorId(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(detalleVentaService).buscarPorId(1L);
    }

    @Test
    void crearOk() {
        DetalleVenta entrada = new DetalleVenta();
        DetalleVenta guardada = new DetalleVenta();
        guardada.setIdDetalleVenta(1L);
        when(detalleVentaService.crear(entrada)).thenReturn(guardada);

        ResponseEntity<DetalleVenta> response = detalleVentaRestController.crear(entrada);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/api/detalles-venta/1", response.getHeaders().getLocation().toString());
    }

    @Test
    void actualizarOk() {
        DetalleVenta entrada = new DetalleVenta();
        when(detalleVentaService.actualizar(1L, entrada)).thenReturn(entrada);

        ResponseEntity<DetalleVenta> response = detalleVentaRestController.actualizar(1L, entrada);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void eliminarOk() {
        ResponseEntity<Void> response = detalleVentaRestController.eliminar(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(detalleVentaService).eliminar(1L);
    }
}