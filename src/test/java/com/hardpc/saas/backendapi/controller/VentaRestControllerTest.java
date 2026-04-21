package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.Venta;
import com.hardpc.saas.backendapi.service.VentaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VentaRestControllerTest {

    @Mock
    private VentaService ventaService;

    @InjectMocks
    private VentaRestController ventaRestController;

    @Test
    void listarOk() {
        Venta v1 = new Venta();
        v1.setIdVenta(1L);
        v1.setTotalVenta(BigDecimal.valueOf(500.00));

        Venta v2 = new Venta();
        v2.setIdVenta(2L);
        v2.setTotalVenta(BigDecimal.valueOf(300.00));

        List<Venta> listaEsperada = List.of(v1, v2);
        when(ventaService.listarTodos()).thenReturn(listaEsperada);

        ResponseEntity<List<Venta>> response = ventaRestController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listaEsperada, response.getBody());
        verify(ventaService).listarTodos();
    }

    @Test
    void buscarPorIdOk() {
        Venta venta = new Venta();
        venta.setIdVenta(1L);
        venta.setNumeroComprobante("0000001");

        when(ventaService.buscarPorId(1L)).thenReturn(venta);

        ResponseEntity<Venta> response = ventaRestController.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(venta, response.getBody());
        verify(ventaService).buscarPorId(1L);
    }

    @Test
    void crearOk() {
        Venta peticionEntrada = new Venta();
        peticionEntrada.setSerieComprobante("F001");

        Venta ventaGuardada = new Venta();
        ventaGuardada.setIdVenta(1L);
        ventaGuardada.setSerieComprobante("F001");

        when(ventaService.crear(peticionEntrada)).thenReturn(ventaGuardada);

        ResponseEntity<Venta> response = ventaRestController.crear(peticionEntrada);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(ventaGuardada, response.getBody());
        assertEquals("/api/ventas/1", response.getHeaders().getLocation().toString());
        verify(ventaService).crear(peticionEntrada);
    }

    @Test
    void actualizarOk() {
        Venta peticionEntrada = new Venta();
        peticionEntrada.setEstadoVenta("ANULADO");

        Venta ventaActualizada = new Venta();
        ventaActualizada.setIdVenta(1L);
        ventaActualizada.setEstadoVenta("ANULADO");

        when(ventaService.actualizar(1L, peticionEntrada)).thenReturn(ventaActualizada);

        ResponseEntity<Venta> response = ventaRestController.actualizar(1L, peticionEntrada);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ventaActualizada, response.getBody());
        verify(ventaService).actualizar(1L, peticionEntrada);
    }

    @Test
    void eliminarOk() {
        ResponseEntity<Void> response = ventaRestController.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(ventaService).eliminar(1L);
    }
}