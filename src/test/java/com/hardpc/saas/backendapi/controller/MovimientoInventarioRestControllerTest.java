package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.MovimientoInventario;
import com.hardpc.saas.backendapi.service.MovimientoInventarioService;
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
class MovimientoInventarioRestControllerTest {

    @Mock
    private MovimientoInventarioService movimientoService;

    @InjectMocks
    private MovimientoInventarioRestController movimientoRestController;

    @Test
    void listarOk() {
        when(movimientoService.listarTodos()).thenReturn(List.of(new MovimientoInventario()));
        ResponseEntity<List<MovimientoInventario>> response = movimientoRestController.listar();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(movimientoService).listarTodos();
    }

    @Test
    void buscarPorIdOk() {
        MovimientoInventario mov = new MovimientoInventario();
        mov.setIdMovimiento(1L);
        when(movimientoService.buscarPorId(1L)).thenReturn(mov);

        ResponseEntity<MovimientoInventario> response = movimientoRestController.buscarPorId(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(movimientoService).buscarPorId(1L);
    }

    @Test
    void crearOk() {
        MovimientoInventario entrada = new MovimientoInventario();
        MovimientoInventario guardado = new MovimientoInventario();
        guardado.setIdMovimiento(1L);
        when(movimientoService.crear(entrada)).thenReturn(guardado);

        ResponseEntity<MovimientoInventario> response = movimientoRestController.crear(entrada);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/api/movimientos-inventario/1", response.getHeaders().getLocation().toString());
    }

    @Test
    void actualizarOk() {
        MovimientoInventario entrada = new MovimientoInventario();
        when(movimientoService.actualizar(1L, entrada)).thenReturn(entrada);

        ResponseEntity<MovimientoInventario> response = movimientoRestController.actualizar(1L, entrada);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void eliminarOk() {
        ResponseEntity<Void> response = movimientoRestController.eliminar(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(movimientoService).eliminar(1L);
    }
}