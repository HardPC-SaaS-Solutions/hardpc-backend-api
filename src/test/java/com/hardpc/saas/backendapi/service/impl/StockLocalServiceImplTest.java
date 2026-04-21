package com.hardpc.saas.backendapi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hardpc.saas.backendapi.entity.StockLocal;
import com.hardpc.saas.backendapi.repository.StockLocalRepository;
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
class StockLocalServiceImplTest {

    @Mock
    private StockLocalRepository stockLocalRepository;

    @InjectMocks
    private StockLocalServiceImpl stockLocalService;

    @Test
    void listarTodosRetornaDatosDelRepositorio() {
        StockLocal sl = new StockLocal();
        sl.setIdStockLocal(1L);
        when(stockLocalRepository.findAll()).thenReturn(List.of(sl));

        List<StockLocal> resultado = stockLocalService.listarTodos();

        assertFalse(resultado.isEmpty());
        verify(stockLocalRepository).findAll();
    }

    @Test
    void buscarPorIdRetornaEntidadCuandoExiste() {
        StockLocal sl = new StockLocal();
        sl.setIdStockLocal(1L);
        when(stockLocalRepository.findById(1L)).thenReturn(Optional.of(sl));

        StockLocal resultado = stockLocalService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdStockLocal());
    }

    @Test
    void buscarPorIdLanzaExcepcionCuandoNoExiste() {
        when(stockLocalRepository.findById(99L)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> stockLocalService.buscarPorId(99L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void crearFuerzaIdNuloAntesDeGuardar() {
        StockLocal sl = new StockLocal();
        sl.setIdStockLocal(99L);
        when(stockLocalRepository.save(any(StockLocal.class))).thenReturn(sl);

        stockLocalService.crear(sl);

        assertNull(sl.getIdStockLocal());
        verify(stockLocalRepository).save(sl);
    }

    @Test
    void actualizarCambiaCamposYGuarda() {
        StockLocal original = new StockLocal();
        original.setIdStockLocal(1L);
        original.setCantidadActual(10);

        StockLocal nuevosDatos = new StockLocal();
        nuevosDatos.setCantidadActual(20);

        when(stockLocalRepository.findById(1L)).thenReturn(Optional.of(original));
        when(stockLocalRepository.save(original)).thenReturn(original);

        stockLocalService.actualizar(1L, nuevosDatos);

        assertEquals(20, original.getCantidadActual());
        verify(stockLocalRepository).save(original);
    }

    @Test
    void actualizarLanzaExcepcionCuandoNoExiste() {
        when(stockLocalRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> stockLocalService.actualizar(99L, new StockLocal()));
        verify(stockLocalRepository, never()).save(any());
    }

    @Test
    void eliminarBorraEntidadExistente() {
        StockLocal sl = new StockLocal();
        sl.setIdStockLocal(1L);
        when(stockLocalRepository.findById(1L)).thenReturn(Optional.of(sl));

        stockLocalService.eliminar(1L);

        verify(stockLocalRepository).delete(sl);
    }
}