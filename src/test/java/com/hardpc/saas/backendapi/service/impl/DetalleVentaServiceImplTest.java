package com.hardpc.saas.backendapi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hardpc.saas.backendapi.entity.DetalleVenta;
import com.hardpc.saas.backendapi.repository.DetalleVentaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class DetalleVentaServiceImplTest {

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @InjectMocks
    private DetalleVentaServiceImpl detalleVentaService;

    @Test
    void listarTodosRetornaDatosDelRepositorio() {
        // 1. Preparación
        DetalleVenta d1 = new DetalleVenta();
        d1.setIdDetalleVenta(1L);
        d1.setCantidad(2);
        when(detalleVentaRepository.findAll()).thenReturn(List.of(d1));

        // 2. Ejecución
        List<DetalleVenta> resultado = detalleVentaService.listarTodos();

        // 3. Verificación
        assertFalse(resultado.isEmpty());
        assertEquals(2, resultado.get(0).getCantidad());
        verify(detalleVentaRepository).findAll();
    }

    @Test
    void buscarPorIdRetornaEntidadCuandoExiste() {
        // 1. Preparación
        DetalleVenta detalle = new DetalleVenta();
        detalle.setIdDetalleVenta(1L);
        when(detalleVentaRepository.findById(1L)).thenReturn(Optional.of(detalle));

        // 2. Ejecución
        DetalleVenta resultado = detalleVentaService.buscarPorId(1L);

        // 3. Verificación
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdDetalleVenta());
    }

    @Test
    void buscarPorIdLanzaExcepcionCuandoNoExiste() {
        // 1. Preparación
        when(detalleVentaRepository.findById(99L)).thenReturn(Optional.empty());

        // 2. Ejecución y Verificación
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            detalleVentaService.buscarPorId(99L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(detalleVentaRepository).findById(99L);
    }

    @Test
    void crearFuerzaIdNuloAntesDeGuardar() {
        // 1. Preparación
        DetalleVenta peticion = new DetalleVenta();
        peticion.setIdDetalleVenta(999L);
        when(detalleVentaRepository.save(any(DetalleVenta.class))).thenReturn(peticion);

        // 2. Ejecución
        detalleVentaService.crear(peticion);

        // 3. Verificación
        assertNull(peticion.getIdDetalleVenta());
        verify(detalleVentaRepository).save(peticion);
    }

    @Test
    void actualizarCambiaCamposYGuarda() {
        // 1. Preparación
        DetalleVenta original = new DetalleVenta();
        original.setIdDetalleVenta(1L);
        original.setPrecioVentaUnitario(BigDecimal.valueOf(10.0));

        DetalleVenta nuevosDatos = new DetalleVenta();
        nuevosDatos.setPrecioVentaUnitario(BigDecimal.valueOf(20.0));

        when(detalleVentaRepository.findById(1L)).thenReturn(Optional.of(original));
        when(detalleVentaRepository.save(original)).thenReturn(original);

        // 2. Ejecución
        detalleVentaService.actualizar(1L, nuevosDatos);

        // 3. Verificación
        assertEquals(BigDecimal.valueOf(20.0), original.getPrecioVentaUnitario());
        verify(detalleVentaRepository).save(original);
    }

    @Test
    void actualizarLanzaExcepcionCuandoNoExiste() {
        when(detalleVentaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> {
            detalleVentaService.actualizar(99L, new DetalleVenta());
        });
        verify(detalleVentaRepository, never()).save(any());
    }

    @Test
    void eliminarBorraEntidadExistente() {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setIdDetalleVenta(1L);
        when(detalleVentaRepository.findById(1L)).thenReturn(Optional.of(detalle));

        detalleVentaService.eliminar(1L);

        verify(detalleVentaRepository).delete(detalle);
    }
}