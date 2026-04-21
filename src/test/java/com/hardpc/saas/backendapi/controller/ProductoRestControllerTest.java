package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.Producto;
import com.hardpc.saas.backendapi.service.ProductoService;
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
class ProductoRestControllerTest {

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoRestController productoRestController;

    @Test
    void listarOk() {
        when(productoService.listarTodos()).thenReturn(List.of(new Producto()));
        ResponseEntity<List<Producto>> response = productoRestController.listar();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productoService).listarTodos();
    }

    @Test
    void buscarPorIdOk() {
        Producto producto = new Producto();
        producto.setIdProducto(1L);
        when(productoService.buscarPorId(1L)).thenReturn(producto);

        ResponseEntity<Producto> response = productoRestController.buscarPorId(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productoService).buscarPorId(1L);
    }

    @Test
    void crearOk() {
        Producto entrada = new Producto();
        Producto guardada = new Producto();
        guardada.setIdProducto(1L);
        when(productoService.crear(entrada)).thenReturn(guardada);

        ResponseEntity<Producto> response = productoRestController.crear(entrada);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/api/productos/1", response.getHeaders().getLocation().toString());
    }

    @Test
    void actualizarOk() {
        Producto entrada = new Producto();
        when(productoService.actualizar(1L, entrada)).thenReturn(entrada);

        ResponseEntity<Producto> response = productoRestController.actualizar(1L, entrada);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void eliminarOk() {
        ResponseEntity<Void> response = productoRestController.eliminar(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(productoService).eliminar(1L);
    }
}