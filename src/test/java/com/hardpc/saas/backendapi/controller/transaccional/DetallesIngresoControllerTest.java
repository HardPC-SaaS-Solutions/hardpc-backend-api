package com.hardpc.saas.backendapi.controller.transaccional;

import com.hardpc.saas.backendapi.entity.DetalleIngreso;
import com.hardpc.saas.backendapi.service.transaccional.DetalleIngresoService;
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
class DetallesIngresoControllerTest {

    @Mock                                    /*Simula el service (service falso)*/
    private DetalleIngresoService service;

    @InjectMocks
    private DetallesIngresoController controller; /*Crea un controller real, inyecta el servicio mockeado*/

    /*TEST DE LISTAR*/

    @Test
    void listar_ok() {

        DetalleIngreso detalle = new DetalleIngreso();

        detalle.setIdProducto(1L);
        detalle.setCantidad(5);                 /*Crea un objero simulado, falso*/

        when(service.listar()).thenReturn(List.of(detalle));  /*Define comportamiento*/

        ResponseEntity<List<DetalleIngreso>> response = controller.listar();  /*Ejecuta el controlador*/

        assertEquals(200, response.getStatusCode().value());    /*Verifica el HTTP 200 ok*/
        assertEquals(1,response.getBody().size());              /*verifica que tenga 1 elemento*/
        assertEquals(5,response.getBody().get(0).getCantidad());/*verifica que la cantidad (5)sea correcto*/

        verify(service,times(1)).listar();
    }

    /*TEST DE BUSCAR POR ID*/

    @Test
    void buscarPorId_ok() {

        DetalleIngreso detalle = new DetalleIngreso();
        detalle.setIdDetalleIngreso(1L);
        detalle.setCantidad(3);

        when(service.buscarPorId(1L)).thenReturn(detalle);

        ResponseEntity<DetalleIngreso> response = controller.buscarPorId(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(3, response.getBody().getCantidad());

        verify(service, times(1)).buscarPorId(1L);
    }

    /*TEST DE GUARDAR*/

    @Test
    void guardar_ok() {

        DetalleIngreso detalle = new DetalleIngreso();
        detalle.setIdDetalleIngreso(1L);
        detalle.setCantidad(5);
        detalle.setPrecioCompraUnitario(new BigDecimal("50.00"));

        when(service.guardarDetalle(any())).thenReturn(detalle);

        ResponseEntity<DetalleIngreso> response =
                controller.guardar(detalle);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(5, response.getBody().getCantidad());

        verify(service, times(1)).guardarDetalle(detalle);
    }


}