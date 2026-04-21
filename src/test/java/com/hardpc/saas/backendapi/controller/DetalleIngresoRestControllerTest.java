package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.DetalleIngreso;
import com.hardpc.saas.backendapi.service.DetalleIngresoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DetalleIngresoRestControllerTest {

    @Mock
    private DetalleIngresoService detalleIngresoService;

    @InjectMocks
    private DetalleIngresoRestController detalleIngresoRestController;

    @Test
    void listarOk() {
        DetalleIngreso di = new DetalleIngreso();
        di.setCantidad(100);
        when(detalleIngresoService.listarTodos()).thenReturn(List.of(di));

        ResponseEntity<List<DetalleIngreso>> response = detalleIngresoRestController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(100, response.getBody().get(0).getCantidad());
        verify(detalleIngresoService).listarTodos();
    }

    @Test
    void buscarPorIdOk() {
        DetalleIngreso di = new DetalleIngreso();
        di.setIdDetalleIngreso(1L);
        di.setPrecioCompraUnitario(new BigDecimal("99.99"));
        when(detalleIngresoService.buscarPorId(1L)).thenReturn(di);

        ResponseEntity<DetalleIngreso> response = detalleIngresoRestController.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(new BigDecimal("99.99"), response.getBody().getPrecioCompraUnitario());
        verify(detalleIngresoService).buscarPorId(1L);
    }

    @Test
    void crearOk() {
        DetalleIngreso entrada = new DetalleIngreso();
        entrada.setCantidad(50);

        DetalleIngreso guardado = new DetalleIngreso();
        guardado.setIdDetalleIngreso(1L);
        guardado.setCantidad(50);

        when(detalleIngresoService.crear(entrada)).thenReturn(guardado);

        ResponseEntity<DetalleIngreso> response = detalleIngresoRestController.crear(entrada);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/api/detalles-ingresos/1", response.getHeaders().getLocation().toString());
        assertNotNull(response.getBody());
        assertEquals(50, response.getBody().getCantidad());
    }

    @Test
    void actualizarOk() {
        DetalleIngreso entrada = new DetalleIngreso();
        entrada.setCantidad(12);
        entrada.setPrecioCompraUnitario(new BigDecimal("10.50"));
        when(detalleIngresoService.actualizar(1L, entrada)).thenReturn(entrada);

        ResponseEntity<DetalleIngreso> response = detalleIngresoRestController.actualizar(1L, entrada);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(12, response.getBody().getCantidad());
        assertEquals(new BigDecimal("10.50"), response.getBody().getPrecioCompraUnitario());
    }

    @Test
    void eliminarOk() {
        ResponseEntity<Void> response = detalleIngresoRestController.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(detalleIngresoService).eliminar(1L);
    }
}