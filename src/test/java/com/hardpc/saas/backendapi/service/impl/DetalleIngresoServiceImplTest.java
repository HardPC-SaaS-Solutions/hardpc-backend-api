package com.hardpc.saas.backendapi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hardpc.saas.backendapi.entity.DetalleIngreso;
import com.hardpc.saas.backendapi.repository.DetalleIngresoRepository;
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
class DetalleIngresoServiceImplTest {

    @Mock
    private DetalleIngresoRepository detalleIngresoRepository;

    @InjectMocks
    private DetalleIngresoServiceImpl detalleIngresoService;

    @Test
    void listarTodosRetornaDatosDelRepositorio() {
        DetalleIngreso di = new DetalleIngreso();
        di.setIdDetalleIngreso(1L);
        di.setCantidad(10);
        di.setPrecioCompraUnitario(new BigDecimal("100.00"));

        when(detalleIngresoRepository.findAll()).thenReturn(List.of(di));

        List<DetalleIngreso> resultado = detalleIngresoService.listarTodos();

        assertFalse(resultado.isEmpty());
        assertEquals(10, resultado.get(0).getCantidad());
        assertEquals(new BigDecimal("100.00"), resultado.get(0).getPrecioCompraUnitario());
        verify(detalleIngresoRepository).findAll();
    }

    @Test
    void buscarPorIdRetornaEntidadCuandoExiste() {
        DetalleIngreso di = new DetalleIngreso();
        di.setIdDetalleIngreso(1L);
        di.setCantidad(5);

        when(detalleIngresoRepository.findById(1L)).thenReturn(Optional.of(di));

        DetalleIngreso resultado = detalleIngresoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdDetalleIngreso());
        assertEquals(5, resultado.getCantidad());
    }

    @Test
    void buscarPorIdLanzaExcepcionCuandoNoExiste() {
        when(detalleIngresoRepository.findById(99L)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> detalleIngresoService.buscarPorId(99L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void crearFuerzaIdNuloAntesDeGuardar() {
        DetalleIngreso peticion = new DetalleIngreso();
        peticion.setIdDetalleIngreso(99L);
        peticion.setCantidad(20);

        when(detalleIngresoRepository.save(any(DetalleIngreso.class))).thenReturn(peticion);

        detalleIngresoService.crear(peticion);

        assertNull(peticion.getIdDetalleIngreso());
        assertEquals(20, peticion.getCantidad());
        verify(detalleIngresoRepository).save(peticion);
    }

    @Test
    void actualizarCambiaCamposYGuarda() {
        DetalleIngreso original = new DetalleIngreso();
        original.setIdDetalleIngreso(1L);
        original.setCantidad(10);
        original.setPrecioCompraUnitario(new BigDecimal("50.00"));

        DetalleIngreso nuevosDatos = new DetalleIngreso();
        nuevosDatos.setCantidad(15);
        nuevosDatos.setPrecioCompraUnitario(new BigDecimal("55.00"));

        when(detalleIngresoRepository.findById(1L)).thenReturn(Optional.of(original));
        when(detalleIngresoRepository.save(original)).thenReturn(original);

        DetalleIngreso actualizado = detalleIngresoService.actualizar(1L, nuevosDatos);

        assertEquals(15, actualizado.getCantidad());
        assertEquals(new BigDecimal("55.00"), actualizado.getPrecioCompraUnitario());
        verify(detalleIngresoRepository).save(original);
    }

    @Test
    void actualizarLanzaExcepcionCuandoNoExiste() {
        when(detalleIngresoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> detalleIngresoService.actualizar(99L, new DetalleIngreso()));
        verify(detalleIngresoRepository, never()).save(any());
    }

    @Test
    void eliminarBorraEntidadExistente() {
        DetalleIngreso existente = new DetalleIngreso();
        existente.setIdDetalleIngreso(1L);
        when(detalleIngresoRepository.findById(1L)).thenReturn(Optional.of(existente));

        detalleIngresoService.eliminar(1L);

        verify(detalleIngresoRepository).delete(existente);
    }
}