package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.IngresoCompra;
import com.hardpc.saas.backendapi.service.IngresoCompraService;
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
class IngresoCompraRestControllerTest {

    @Mock
    private IngresoCompraService ingresoCompraService;

    @InjectMocks
    private IngresoCompraRestController ingresoCompraRestController;

    @Test
    void listarOk() {
        IngresoCompra ic = new IngresoCompra();
        ic.setSerieComprobante("B001");
        ic.setTotalCompra(new BigDecimal("500.00"));
        when(ingresoCompraService.listarTodos()).thenReturn(List.of(ic));

        ResponseEntity<List<IngresoCompra>> response = ingresoCompraRestController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("B001", response.getBody().get(0).getSerieComprobante());
        assertEquals(new BigDecimal("500.00"), response.getBody().get(0).getTotalCompra());
        verify(ingresoCompraService).listarTodos();
    }

    @Test
    void buscarPorIdOk() {
        IngresoCompra ic = new IngresoCompra();
        ic.setIdIngreso(1L);
        ic.setTotalCompra(new BigDecimal("2500.00"));
        ic.setEstadoIngreso("REGISTRADO");
        when(ingresoCompraService.buscarPorId(1L)).thenReturn(ic);

        ResponseEntity<IngresoCompra> response = ingresoCompraRestController.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(new BigDecimal("2500.00"), response.getBody().getTotalCompra());
        assertEquals("REGISTRADO", response.getBody().getEstadoIngreso());
        verify(ingresoCompraService).buscarPorId(1L);
    }

    @Test
    void crearOk() {
        IngresoCompra entrada = new IngresoCompra();
        entrada.setEstadoIngreso("REGISTRADO");
        entrada.setSerieComprobante("F001");

        IngresoCompra guardado = new IngresoCompra();
        guardado.setIdIngreso(1L);
        guardado.setEstadoIngreso("REGISTRADO");
        guardado.setSerieComprobante("F001");

        when(ingresoCompraService.crear(entrada)).thenReturn(guardado);

        ResponseEntity<IngresoCompra> response = ingresoCompraRestController.crear(entrada);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/api/ingresos-compras/1", response.getHeaders().getLocation().toString());
        assertNotNull(response.getBody());
        assertEquals("REGISTRADO", response.getBody().getEstadoIngreso());
        assertEquals("F001", response.getBody().getSerieComprobante());
    }

    @Test
    void actualizarOk() {
        IngresoCompra entrada = new IngresoCompra();
        entrada.setEstadoIngreso("PROCESADO");
        entrada.setTotalCompra(new BigDecimal("1000.00"));
        when(ingresoCompraService.actualizar(1L, entrada)).thenReturn(entrada);

        ResponseEntity<IngresoCompra> response = ingresoCompraRestController.actualizar(1L, entrada);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("PROCESADO", response.getBody().getEstadoIngreso());
        assertEquals(new BigDecimal("1000.00"), response.getBody().getTotalCompra());
    }

    @Test
    void eliminarOk() {
        ResponseEntity<Void> response = ingresoCompraRestController.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(ingresoCompraService).eliminar(1L);
    }
}