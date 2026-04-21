package com.hardpc.saas.backendapi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hardpc.saas.backendapi.entity.IngresoCompra;
import com.hardpc.saas.backendapi.repository.IngresoCompraRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class IngresoCompraServiceImplTest {

    @Mock
    private IngresoCompraRepository ingresoCompraRepository;

    @InjectMocks
    private IngresoCompraServiceImpl ingresoCompraService;

    @Test
    void listarTodosRetornaDatosDelRepositorio() {
        IngresoCompra ic = new IngresoCompra();
        ic.setIdIngreso(1L);
        ic.setSerieComprobante("F001");
        ic.setNumeroComprobante("000123");
        ic.setTotalCompra(new BigDecimal("1500.50"));
        ic.setEstadoIngreso("REGISTRADO");

        when(ingresoCompraRepository.findAll()).thenReturn(List.of(ic));

        List<IngresoCompra> resultado = ingresoCompraService.listarTodos();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("F001", resultado.get(0).getSerieComprobante());
        assertEquals(new BigDecimal("1500.50"), resultado.get(0).getTotalCompra());
        assertEquals("REGISTRADO", resultado.get(0).getEstadoIngreso());
        verify(ingresoCompraRepository).findAll();
    }

    @Test
    void buscarPorIdRetornaEntidadCuandoExiste() {
        IngresoCompra ic = new IngresoCompra();
        ic.setIdIngreso(1L);
        ic.setNumeroComprobante("000123");
        ic.setImpuesto(new BigDecimal("18.00"));

        when(ingresoCompraRepository.findById(1L)).thenReturn(Optional.of(ic));

        IngresoCompra resultado = ingresoCompraService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdIngreso());
        assertEquals("000123", resultado.getNumeroComprobante());
        assertEquals(new BigDecimal("18.00"), resultado.getImpuesto());
    }

    @Test
    void buscarPorIdLanzaExcepcionCuandoNoExiste() {
        when(ingresoCompraRepository.findById(99L)).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> ingresoCompraService.buscarPorId(99L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void crearFuerzaIdNuloAntesDeGuardar() {
        IngresoCompra peticion = new IngresoCompra();
        peticion.setIdIngreso(999L);
        peticion.setSerieComprobante("B002");
        peticion.setEstadoIngreso("REGISTRADO");
        peticion.setFechaIngreso(LocalDateTime.now());

        when(ingresoCompraRepository.save(any(IngresoCompra.class))).thenReturn(peticion);

        ingresoCompraService.crear(peticion);

        assertNull(peticion.getIdIngreso());
        assertEquals("B002", peticion.getSerieComprobante());
        assertEquals("REGISTRADO", peticion.getEstadoIngreso());
        verify(ingresoCompraRepository).save(peticion);
    }

    @Test
    void actualizarCambiaCamposYGuarda() {
        IngresoCompra original = new IngresoCompra();
        original.setIdIngreso(1L);
        original.setEstadoIngreso("REGISTRADO");
        original.setTotalCompra(new BigDecimal("100.00"));

        IngresoCompra nuevosDatos = new IngresoCompra();
        nuevosDatos.setEstadoIngreso("ANULADO");
        nuevosDatos.setTotalCompra(new BigDecimal("0.00"));

        when(ingresoCompraRepository.findById(1L)).thenReturn(Optional.of(original));
        when(ingresoCompraRepository.save(original)).thenReturn(original);

        IngresoCompra actualizado = ingresoCompraService.actualizar(1L, nuevosDatos);

        assertEquals("ANULADO", actualizado.getEstadoIngreso());
        assertEquals(new BigDecimal("0.00"), actualizado.getTotalCompra());
        verify(ingresoCompraRepository).save(original);
    }

    @Test
    void actualizarLanzaExcepcionCuandoNoExiste() {
        when(ingresoCompraRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> ingresoCompraService.actualizar(99L, new IngresoCompra()));
        verify(ingresoCompraRepository, never()).save(any());
    }

    @Test
    void eliminarBorraEntidadExistente() {
        IngresoCompra existente = new IngresoCompra();
        existente.setIdIngreso(1L);
        when(ingresoCompraRepository.findById(1L)).thenReturn(Optional.of(existente));

        ingresoCompraService.eliminar(1L);

        verify(ingresoCompraRepository).delete(existente);
    }
}