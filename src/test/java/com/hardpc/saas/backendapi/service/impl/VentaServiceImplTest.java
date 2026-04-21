package com.hardpc.saas.backendapi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hardpc.saas.backendapi.entity.Venta;
import com.hardpc.saas.backendapi.repository.VentaRepository;
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
class VentaServiceImplTest {

    @Mock
    private VentaRepository ventaRepository;

    @InjectMocks
    private VentaServiceImpl ventaService;

    // 1. findAllReturnsRepositoryData -> listarTodosRetornaDatosDelRepositorio
    @Test
    void listarTodosRetornaDatosDelRepositorio() {
        // 1. Preparación
        Venta v1 = new Venta();
        v1.setIdVenta(1L);
        v1.setSerieComprobante("F001");
        List<Venta> listaEsperada = List.of(v1);

        when(ventaRepository.findAll()).thenReturn(listaEsperada);

        // 2. Ejecución
        List<Venta> resultado = ventaService.listarTodos();

        // 3. Verificación
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("F001", resultado.get(0).getSerieComprobante());
        verify(ventaRepository).findAll();
    }

    // 2. findByIdReturnsEntityWhenExists -> buscarPorIdRetornaEntidadCuandoExiste
    @Test
    void buscarPorIdRetornaEntidadCuandoExiste() {
        // 1. Preparación
        Venta venta = new Venta();
        venta.setIdVenta(1L);
        venta.setSerieComprobante("B001");

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        // 2. Ejecución
        Venta resultado = ventaService.buscarPorId(1L);

        // 3. Verificación
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdVenta());
        verify(ventaRepository).findById(1L);
    }

    // 3. findByIdThrowsWhenNotExists -> buscarPorIdLanzaExcepcionCuandoNoExiste
    @Test
    void buscarPorIdLanzaExcepcionCuandoNoExiste() {
        // 1. Preparación
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        // 2. Ejecución y Verificación
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            ventaService.buscarPorId(99L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Venta no encontrada"));
        verify(ventaRepository).findById(99L);
    }

    // 4. saveForcesNullIdBeforePersisting -> crearFuerzaIdNuloAntesDeGuardar
    @Test
    void crearFuerzaIdNuloAntesDeGuardar() {
        // 1. Preparación
        Venta peticionConIdFalso = new Venta();
        peticionConIdFalso.setIdVenta(999L);
        peticionConIdFalso.setNumeroComprobante("00001234");

        when(ventaRepository.save(any(Venta.class))).thenReturn(peticionConIdFalso);

        // 2. Ejecución
        ventaService.crear(peticionConIdFalso);

        // 3. Verificación
        assertNull(peticionConIdFalso.getIdVenta());
        verify(ventaRepository).save(peticionConIdFalso);
    }

    // 5. updateChangesFieldsAndPersists -> actualizarCambiaCamposYGuarda
    @Test
    void actualizarCambiaCamposYGuarda() {
        // 1. Preparación
        Venta originalEnBD = new Venta();
        originalEnBD.setIdVenta(1L);
        originalEnBD.setTotalVenta(BigDecimal.valueOf(100.00));
        originalEnBD.setEstadoVenta("EMITIDO");

        Venta datosNuevos = new Venta();
        datosNuevos.setTotalVenta(BigDecimal.valueOf(150.00));
        datosNuevos.setEstadoVenta("ANULADO");

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(originalEnBD));
        when(ventaRepository.save(originalEnBD)).thenReturn(originalEnBD);

        // 2. Ejecución
        ventaService.actualizar(1L, datosNuevos);

        // 3. Verificación
        assertEquals(BigDecimal.valueOf(150.00), originalEnBD.getTotalVenta());
        assertEquals("ANULADO", originalEnBD.getEstadoVenta());
        verify(ventaRepository).save(originalEnBD);
    }

    // 6. updateThrowsWhenNotExists -> actualizarLanzaExcepcionCuandoNoExiste
    @Test
    void actualizarLanzaExcepcionCuandoNoExiste() {
        // 1. Preparación
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());
        Venta nuevosDatos = new Venta();

        // 2. Ejecución y Verificación
        assertThrows(ResponseStatusException.class, () -> {
            ventaService.actualizar(99L, nuevosDatos);
        });

        verify(ventaRepository, never()).save(any());
    }

    // 7. deleteRemovesExistingEntity -> eliminarBorraEntidadExistente
    @Test
    void eliminarBorraEntidadExistente() {
        // 1. Preparación
        Venta ventaExistente = new Venta();
        ventaExistente.setIdVenta(1L);

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(ventaExistente));

        // 2. Ejecución
        ventaService.eliminar(1L);

        // 3. Verificación
        verify(ventaRepository).findById(1L);
        verify(ventaRepository).delete(ventaExistente);
    }
}