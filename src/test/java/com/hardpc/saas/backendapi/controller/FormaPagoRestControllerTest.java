package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.FormaPago;
import com.hardpc.saas.backendapi.service.FormaPagoService;
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
class FormaPagoRestControllerTest {

    @Mock
    private FormaPagoService formaPagoService;

    @InjectMocks
    private FormaPagoRestController formaPagoRestController;

    @Test
    void listarOk() {
        FormaPago f1 = new FormaPago();
        f1.setIdFormaPago(1L);
        f1.setDescripcion("Yape");

        FormaPago f2 = new FormaPago();
        f2.setIdFormaPago(2L);
        f2.setDescripcion("Plin");

        List<FormaPago> listaEsperada = List.of(f1, f2);
        when(formaPagoService.listarTodos()).thenReturn(listaEsperada);

        ResponseEntity<List<FormaPago>> response = formaPagoRestController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listaEsperada, response.getBody());
        verify(formaPagoService).listarTodos();
    }

    @Test
    void buscarPorIdOk() {
        FormaPago formaPago = new FormaPago();
        formaPago.setIdFormaPago(1L);
        formaPago.setDescripcion("Efectivo");

        when(formaPagoService.buscarPorId(1L)).thenReturn(formaPago);

        ResponseEntity<FormaPago> response = formaPagoRestController.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(formaPago, response.getBody());
        verify(formaPagoService).buscarPorId(1L);
    }

    @Test
    void crearOk() {
        FormaPago peticionEntrada = new FormaPago();
        peticionEntrada.setDescripcion("Tarjeta Visa");

        FormaPago formaPagoGuardada = new FormaPago();
        formaPagoGuardada.setIdFormaPago(1L);
        formaPagoGuardada.setDescripcion("Tarjeta Visa");

        when(formaPagoService.crear(peticionEntrada)).thenReturn(formaPagoGuardada);

        ResponseEntity<FormaPago> response = formaPagoRestController.crear(peticionEntrada);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(formaPagoGuardada, response.getBody());
        assertEquals("/api/formas-pago/1", response.getHeaders().getLocation().toString());

        verify(formaPagoService).crear(peticionEntrada);
    }

    @Test
    void actualizarOk() {
        FormaPago peticionEntrada = new FormaPago();
        peticionEntrada.setDescripcion("Yape Actualizado");

        FormaPago formaPagoActualizada = new FormaPago();
        formaPagoActualizada.setIdFormaPago(1L);
        formaPagoActualizada.setDescripcion("Yape Actualizado");

        when(formaPagoService.actualizar(1L, peticionEntrada)).thenReturn(formaPagoActualizada);

        ResponseEntity<FormaPago> response = formaPagoRestController.actualizar(1L, peticionEntrada);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(formaPagoActualizada, response.getBody());

        verify(formaPagoService).actualizar(1L, peticionEntrada);
    }

    @Test
    void eliminarOk() {
        ResponseEntity<Void> response = formaPagoRestController.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(formaPagoService).eliminar(1L);
    }
}