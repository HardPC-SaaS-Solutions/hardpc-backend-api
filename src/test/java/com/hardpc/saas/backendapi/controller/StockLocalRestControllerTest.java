package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.StockLocal;
import com.hardpc.saas.backendapi.service.StockLocalService;
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
class StockLocalRestControllerTest {

    @Mock
    private StockLocalService stockLocalService;

    @InjectMocks
    private StockLocalRestController stockLocalRestController;

    @Test
    void listarOk() {
        when(stockLocalService.listarTodos()).thenReturn(List.of(new StockLocal()));
        ResponseEntity<List<StockLocal>> response = stockLocalRestController.listar();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(stockLocalService).listarTodos();
    }

    @Test
    void buscarPorIdOk() {
        StockLocal sl = new StockLocal();
        sl.setIdStockLocal(1L);
        when(stockLocalService.buscarPorId(1L)).thenReturn(sl);

        ResponseEntity<StockLocal> response = stockLocalRestController.buscarPorId(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(stockLocalService).buscarPorId(1L);
    }

    @Test
    void crearOk() {
        StockLocal entrada = new StockLocal();
        StockLocal guardado = new StockLocal();
        guardado.setIdStockLocal(1L);
        when(stockLocalService.crear(entrada)).thenReturn(guardado);

        ResponseEntity<StockLocal> response = stockLocalRestController.crear(entrada);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/api/stocks-locales/1", response.getHeaders().getLocation().toString());
    }

    @Test
    void actualizarOk() {
        StockLocal entrada = new StockLocal();
        when(stockLocalService.actualizar(1L, entrada)).thenReturn(entrada);

        ResponseEntity<StockLocal> response = stockLocalRestController.actualizar(1L, entrada);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void eliminarOk() {
        ResponseEntity<Void> response = stockLocalRestController.eliminar(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(stockLocalService).eliminar(1L);
    }
}