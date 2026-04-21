package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.Proveedor;
import com.hardpc.saas.backendapi.service.ProveedorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProveedorRestControllerTest {

    @Mock
    private ProveedorService proveedorService;

    @InjectMocks
    private ProveedorRestController proveedorRestController;

    @Test
    void listarOk() {
        Proveedor p = new Proveedor();
        p.setRuc("20123456789");
        p.setRazonSocial("Distribuidora Global");
        when(proveedorService.listarTodos()).thenReturn(List.of(p));

        ResponseEntity<List<Proveedor>> response = proveedorRestController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("20123456789", response.getBody().get(0).getRuc());
        assertEquals("Distribuidora Global", response.getBody().get(0).getRazonSocial());
        verify(proveedorService).listarTodos();
    }

    @Test
    void buscarPorIdOk() {
        Proveedor p = new Proveedor();
        p.setIdProveedor(1L);
        p.setRazonSocial("Tech SAC");
        p.setEmail("ventas@tech.com");
        when(proveedorService.buscarPorId(1L)).thenReturn(p);

        ResponseEntity<Proveedor> response = proveedorRestController.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Tech SAC", response.getBody().getRazonSocial());
        assertEquals("ventas@tech.com", response.getBody().getEmail());
        verify(proveedorService).buscarPorId(1L);
    }

    @Test
    void crearOk() {
        Proveedor entrada = new Proveedor();
        entrada.setRuc("10123456789");
        entrada.setRazonSocial("Importaciones SAC");

        Proveedor guardado = new Proveedor();
        guardado.setIdProveedor(1L);
        guardado.setRuc("10123456789");
        guardado.setRazonSocial("Importaciones SAC");

        when(proveedorService.crear(entrada)).thenReturn(guardado);

        ResponseEntity<Proveedor> response = proveedorRestController.crear(entrada);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/api/proveedores/1", response.getHeaders().getLocation().toString());
        assertNotNull(response.getBody());
        assertEquals("10123456789", response.getBody().getRuc());
        assertEquals("Importaciones SAC", response.getBody().getRazonSocial());
    }

    @Test
    void actualizarOk() {
        Proveedor entrada = new Proveedor();
        entrada.setRazonSocial("Nueva SAC");
        entrada.setTelefono("999888777");
        when(proveedorService.actualizar(1L, entrada)).thenReturn(entrada);

        ResponseEntity<Proveedor> response = proveedorRestController.actualizar(1L, entrada);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Nueva SAC", response.getBody().getRazonSocial());
        assertEquals("999888777", response.getBody().getTelefono());
    }

    @Test
    void eliminarOk() {
        ResponseEntity<Void> response = proveedorRestController.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(proveedorService).eliminar(1L);
    }
}