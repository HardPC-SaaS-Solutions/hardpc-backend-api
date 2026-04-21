package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.TipoComprobante;
import com.hardpc.saas.backendapi.service.TipoComprobanteService;
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
class TipoComprobanteRestControllerTest {

    @Mock
    private TipoComprobanteService tipoComprobanteService;

    @InjectMocks
    private TipoComprobanteRestController tipoComprobanteRestController;

    @Test
    void listarOk() {
        TipoComprobante tc1 = new TipoComprobante();
        tc1.setIdTipoComprobante(1L);
        tc1.setDescripcion("Boleta");

        TipoComprobante tc2 = new TipoComprobante();
        tc2.setIdTipoComprobante(2L);
        tc2.setDescripcion("Factura");

        List<TipoComprobante> listaEsperada = List.of(tc1, tc2);
        when(tipoComprobanteService.listarTodos()).thenReturn(listaEsperada);

        ResponseEntity<List<TipoComprobante>> response = tipoComprobanteRestController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listaEsperada, response.getBody());
        verify(tipoComprobanteService).listarTodos();
    }

    @Test
    void buscarPorIdOk() {
        TipoComprobante tc = new TipoComprobante();
        tc.setIdTipoComprobante(1L);
        tc.setDescripcion("Factura");

        when(tipoComprobanteService.buscarPorId(1L)).thenReturn(tc);

        ResponseEntity<TipoComprobante> response = tipoComprobanteRestController.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tc, response.getBody());
        verify(tipoComprobanteService).buscarPorId(1L);
    }

    @Test
    void crearOk() {
        TipoComprobante peticionEntrada = new TipoComprobante();
        peticionEntrada.setDescripcion("Nota de Crédito");

        TipoComprobante tcGuardado = new TipoComprobante();
        tcGuardado.setIdTipoComprobante(1L);
        tcGuardado.setDescripcion("Nota de Crédito");

        when(tipoComprobanteService.crear(peticionEntrada)).thenReturn(tcGuardado);

        ResponseEntity<TipoComprobante> response = tipoComprobanteRestController.crear(peticionEntrada);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(tcGuardado, response.getBody());
        assertEquals("/api/tipos-comprobantes/1", response.getHeaders().getLocation().toString());
        verify(tipoComprobanteService).crear(peticionEntrada);
    }

    @Test
    void actualizarOk() {
        TipoComprobante peticionEntrada = new TipoComprobante();
        peticionEntrada.setDescripcion("Boleta Actualizada");

        TipoComprobante tcActualizado = new TipoComprobante();
        tcActualizado.setIdTipoComprobante(1L);
        tcActualizado.setDescripcion("Boleta Actualizada");

        when(tipoComprobanteService.actualizar(1L, peticionEntrada)).thenReturn(tcActualizado);

        ResponseEntity<TipoComprobante> response = tipoComprobanteRestController.actualizar(1L, peticionEntrada);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tcActualizado, response.getBody());
        verify(tipoComprobanteService).actualizar(1L, peticionEntrada);
    }

    @Test
    void eliminarOk() {
        ResponseEntity<Void> response = tipoComprobanteRestController.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(tipoComprobanteService).eliminar(1L);
    }
}