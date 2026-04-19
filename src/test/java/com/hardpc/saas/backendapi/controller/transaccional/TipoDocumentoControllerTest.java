package com.hardpc.saas.backendapi.controller.transaccional;

import com.hardpc.saas.backendapi.entity.TipoDocumento;
import com.hardpc.saas.backendapi.service.transaccional.TipoDocumentoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TipoDocumentoControllerTest {

    @Mock
    private TipoDocumentoService service;

    @InjectMocks
    private TipoDocumentoController controller;

    /*TEST LISTAR*/

    @Test
    void listar_ok() {

        TipoDocumento tipo = new TipoDocumento();
        tipo.setIdTipoDocumento(1L);
        tipo.setNombre("Factura");

        when(service.listarTipoDocumentos()).thenReturn(List.of(tipo));

        ResponseEntity<List<TipoDocumento>> response = controller.listar();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("Factura", response.getBody().get(0).getNombre());

        verify(service, times(1)).listarTipoDocumentos();
    }

    /* TEST BUSCAR POR ID */

    @Test
    void buscarPorId_ok() {

        TipoDocumento tipo = new TipoDocumento();
        tipo.setIdTipoDocumento(1L);
        tipo.setNombre("Boleta");

        when(service.buscarPorId(1L)).thenReturn(tipo);

        ResponseEntity<TipoDocumento> response = controller.buscarPorId(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1L, response.getBody().getIdTipoDocumento());
        assertEquals("Boleta", response.getBody().getNombre());

        verify(service, times(1)).buscarPorId(1L);
    }

    /* TEST GUARDAR*/

    @Test
    void guardar_ok() {

        TipoDocumento tipo = new TipoDocumento();
        tipo.setIdTipoDocumento(1L);
        tipo.setNombre("Factura");

        when(service.guardarTipo(any())).thenReturn(tipo);

        ResponseEntity<TipoDocumento> response =
                controller.guardar(tipo);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Factura", response.getBody().getNombre());

        verify(service, times(1)).guardarTipo(tipo);
    }

    /* TEST ACTUALIZAR */

    @Test
    void actualizar_ok() {

        TipoDocumento tipo = new TipoDocumento();
        tipo.setIdTipoDocumento(1L);
        tipo.setNombre("Factura actualizada");

        when(service.actualizarTipoDocumento(eq(1L), any(TipoDocumento.class)))
                .thenReturn(tipo);

        ResponseEntity<TipoDocumento> response =
                controller.actualizar(1L, tipo);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Factura actualizada", response.getBody().getNombre());

        verify(service, times(1))
                .actualizarTipoDocumento(eq(1L), any(TipoDocumento.class));
    }

    /* TEST DELETE */
    void eliminar_ok() {

        doNothing().when(service).eliminarTipoDocumento(1L);

        ResponseEntity<Void> response = controller.eliminar(1L);

        assertEquals(204, response.getStatusCode().value());

        verify(service, times(1)).eliminarTipoDocumento(1L);
    }

}