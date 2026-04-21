package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.Cliente;
import com.hardpc.saas.backendapi.service.ClienteService;
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
class ClienteRestControllerTest {

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteRestController clienteRestController;

    @Test
    void listarOk() {
        when(clienteService.listarTodos()).thenReturn(List.of(new Cliente()));
        ResponseEntity<List<Cliente>> response = clienteRestController.listar();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(clienteService).listarTodos();
    }

    @Test
    void buscarPorIdOk() {
        Cliente cliente = new Cliente();
        cliente.setIdPersona(1L);
        when(clienteService.buscarPorId(1L)).thenReturn(cliente);

        ResponseEntity<Cliente> response = clienteRestController.buscarPorId(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(clienteService).buscarPorId(1L);
    }

    @Test
    void crearOk() {
        Cliente entrada = new Cliente();
        Cliente guardado = new Cliente();
        guardado.setIdPersona(1L);
        when(clienteService.crear(entrada)).thenReturn(guardado);

        ResponseEntity<Cliente> response = clienteRestController.crear(entrada);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/api/clientes/1", response.getHeaders().getLocation().toString());
    }

    @Test
    void actualizarOk() {
        Cliente entrada = new Cliente();
        when(clienteService.actualizar(1L, entrada)).thenReturn(entrada);

        ResponseEntity<Cliente> response = clienteRestController.actualizar(1L, entrada);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void eliminarOk() {
        ResponseEntity<Void> response = clienteRestController.eliminar(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(clienteService).eliminar(1L);
    }
}