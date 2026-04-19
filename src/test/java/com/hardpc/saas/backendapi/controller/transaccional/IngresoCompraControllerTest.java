package com.hardpc.saas.backendapi.controller.transaccional;

import com.hardpc.saas.backendapi.entity.IngresoCompra;
import com.hardpc.saas.backendapi.service.transaccional.IngresoCompraService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class IngresoCompraControllerTest {

    @Mock
    private IngresoCompraService service;

    @InjectMocks
    private IngresoCompraController controller;

    /*TEST DE LISTAR*/
    @Test
    void listar_ok() {

        IngresoCompra ingreso = new IngresoCompra();
        ingreso.setIdIngreso(1L);
        ingreso.setSerieComprobante("F001");
        ingreso.setTotalCompra(new BigDecimal("100.00"));

        when(service.listarIngresoCompra()).thenReturn(List.of(ingreso));

        ResponseEntity<List<IngresoCompra>> response = controller.listar();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("F001", response.getBody().get(0).getSerieComprobante());

        verify(service, times(1)).listarIngresoCompra();
    }

    /* TEST DE BUSCAR POR ID */
    @Test
    void buscarPorId_ok() {

        IngresoCompra ingreso = new IngresoCompra();
        ingreso.setIdIngreso(1L);
        ingreso.setSerieComprobante("F001");

        when(service.buscarPorId(1L)).thenReturn(ingreso);

        ResponseEntity<IngresoCompra> response = controller.buscarPorId(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1L, response.getBody().getIdIngreso());
        assertEquals("F001", response.getBody().getSerieComprobante());

        verify(service, times(1)).buscarPorId(1L);
    }

    /* TEST DE GUARDAR */

    @Test
    void guardar_ok() {

        IngresoCompra ingreso = new IngresoCompra();
        ingreso.setIdIngreso(1L);
        ingreso.setSerieComprobante("F001");

        when(service.guardarCompra(any())).thenReturn(ingreso);

        ResponseEntity<IngresoCompra> response =
                controller.guardar(ingreso);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("F001", response.getBody().getSerieComprobante());

        verify(service, times(1)).guardarCompra(ingreso);
    }

    /*TEST DE ELIMINAR*/

    @Test
    void eliminar_ok() {

        doNothing().when(service).eliminarPorId(1L);

        ResponseEntity<Void> response = controller.eliminar(1L);

        assertEquals(204, response.getStatusCode().value());

        verify(service, times(1)).eliminarPorId(1L);
    }

}