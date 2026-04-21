package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.Categoria;
import com.hardpc.saas.backendapi.service.CategoriaService;
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
class CategoriaRestControllerTest {

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private CategoriaRestController categoriaRestController;

    @Test
    void listarOk() {
        when(categoriaService.listarTodos()).thenReturn(List.of(new Categoria()));
        ResponseEntity<List<Categoria>> response = categoriaRestController.listar();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(categoriaService).listarTodos();
    }

    @Test
    void buscarPorIdOk() {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(1L);
        when(categoriaService.buscarPorId(1L)).thenReturn(categoria);

        ResponseEntity<Categoria> response = categoriaRestController.buscarPorId(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(categoriaService).buscarPorId(1L);
    }

    @Test
    void crearOk() {
        Categoria entrada = new Categoria();
        Categoria guardada = new Categoria();
        guardada.setIdCategoria(1L);
        when(categoriaService.crear(entrada)).thenReturn(guardada);

        ResponseEntity<Categoria> response = categoriaRestController.crear(entrada);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/api/categorias/1", response.getHeaders().getLocation().toString());
    }

    @Test
    void actualizarOk() {
        Categoria entrada = new Categoria();
        when(categoriaService.actualizar(1L, entrada)).thenReturn(entrada);

        ResponseEntity<Categoria> response = categoriaRestController.actualizar(1L, entrada);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void eliminarOk() {
        ResponseEntity<Void> response = categoriaRestController.eliminar(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(categoriaService).eliminar(1L);
    }
}