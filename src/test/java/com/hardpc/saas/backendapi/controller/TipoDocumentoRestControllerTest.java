package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.TipoDocumento;
import com.hardpc.saas.backendapi.service.TipoDocumentoService;
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
class TipoDocumentoRestControllerTest {

    @Mock
    private TipoDocumentoService tipoDocumentoService;

    @InjectMocks
    private TipoDocumentoRestController tipoDocumentoRestController;

    @Test
    void listarOk() {
        TipoDocumento td = new TipoDocumento();
        td.setNombre("DNI");
        when(tipoDocumentoService.listarTodos()).thenReturn(List.of(td));

        ResponseEntity<List<TipoDocumento>> response = tipoDocumentoRestController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("DNI", response.getBody().get(0).getNombre());
        verify(tipoDocumentoService).listarTodos();
    }

    @Test
    void buscarPorIdOk() {
        TipoDocumento td = new TipoDocumento();
        td.setIdTipoDocumento(1L);
        td.setNombre("RUC");
        td.setLongitudExacta(11);
        when(tipoDocumentoService.buscarPorId(1L)).thenReturn(td);

        ResponseEntity<TipoDocumento> response = tipoDocumentoRestController.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("RUC", response.getBody().getNombre());
        assertEquals(11, response.getBody().getLongitudExacta());
        verify(tipoDocumentoService).buscarPorId(1L);
    }

    @Test
    void crearOk() {
        TipoDocumento entrada = new TipoDocumento();
        entrada.setNombre("CE");

        TipoDocumento guardado = new TipoDocumento();
        guardado.setIdTipoDocumento(1L);
        guardado.setNombre("CE");

        when(tipoDocumentoService.crear(entrada)).thenReturn(guardado);

        ResponseEntity<TipoDocumento> response = tipoDocumentoRestController.crear(entrada);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/api/tipos-documento/1", response.getHeaders().getLocation().toString());
        assertNotNull(response.getBody());
        assertEquals("CE", response.getBody().getNombre());
    }

    @Test
    void actualizarOk() {
        TipoDocumento entrada = new TipoDocumento();
        entrada.setNombre("PASAPORTE");
        when(tipoDocumentoService.actualizar(1L, entrada)).thenReturn(entrada);

        ResponseEntity<TipoDocumento> response = tipoDocumentoRestController.actualizar(1L, entrada);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("PASAPORTE", response.getBody().getNombre());
    }

    @Test
    void eliminarOk() {
        ResponseEntity<Void> response = tipoDocumentoRestController.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(tipoDocumentoService).eliminar(1L);
    }
}