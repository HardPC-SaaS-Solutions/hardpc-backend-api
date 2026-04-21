package com.hardpc.saas.backendapi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hardpc.saas.backendapi.entity.MovimientoInventario;
import com.hardpc.saas.backendapi.repository.MovimientoInventarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class MovimientoInventarioServiceImplTest {

    @Mock
    private MovimientoInventarioRepository movimientoRepository;

    @InjectMocks
    private MovimientoInventarioServiceImpl movimientoService;

    @Test
    void listarTodosRetornaDatosDelRepositorio() {
        when(movimientoRepository.findAll()).thenReturn(List.of(new MovimientoInventario()));
        List<MovimientoInventario> resultado = movimientoService.listarTodos();
        assertFalse(resultado.isEmpty());
        verify(movimientoRepository).findAll();
    }

    @Test
    void buscarPorIdRetornaEntidadCuandoExiste() {
        MovimientoInventario mov = new MovimientoInventario();
        mov.setIdMovimiento(1L);
        when(movimientoRepository.findById(1L)).thenReturn(Optional.of(mov));
        MovimientoInventario resultado = movimientoService.buscarPorId(1L);
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdMovimiento());
    }

    @Test
    void buscarPorIdLanzaExcepcionCuandoNoExiste() {
        when(movimientoRepository.findById(99L)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> movimientoService.buscarPorId(99L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("no encontrado"));
    }

    @Test
    void crearFuerzaIdNuloAntesDeGuardar() {
        MovimientoInventario mov = new MovimientoInventario();
        mov.setIdMovimiento(99L);
        when(movimientoRepository.save(any(MovimientoInventario.class))).thenReturn(mov);
        movimientoService.crear(mov);
        assertNull(mov.getIdMovimiento());
        verify(movimientoRepository).save(mov);
    }

    @Test
    void actualizarCambiaCamposYGuarda() {
        MovimientoInventario original = new MovimientoInventario();
        original.setIdMovimiento(1L);
        original.setTipoMovimiento("ENTRADA");

        MovimientoInventario nuevosDatos = new MovimientoInventario();
        nuevosDatos.setTipoMovimiento("TRASLADO");

        when(movimientoRepository.findById(1L)).thenReturn(Optional.of(original));
        when(movimientoRepository.save(original)).thenReturn(original);

        movimientoService.actualizar(1L, nuevosDatos);
        assertEquals("TRASLADO", original.getTipoMovimiento());
        verify(movimientoRepository).save(original);
    }

    @Test
    void actualizarLanzaExcepcionCuandoNoExiste() {
        when(movimientoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> movimientoService.actualizar(99L, new MovimientoInventario()));
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    void eliminarBorraEntidadExistente() {
        MovimientoInventario mov = new MovimientoInventario();
        mov.setIdMovimiento(1L);
        when(movimientoRepository.findById(1L)).thenReturn(Optional.of(mov));
        movimientoService.eliminar(1L);
        verify(movimientoRepository).delete(mov);
    }
}