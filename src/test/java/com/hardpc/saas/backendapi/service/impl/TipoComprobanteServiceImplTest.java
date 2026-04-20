package com.hardpc.saas.backendapi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hardpc.saas.backendapi.entity.TipoComprobante;
import com.hardpc.saas.backendapi.repository.TipoComprobanteRepository;
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
class TipoComprobanteServiceImplTest {

    @Mock
    private TipoComprobanteRepository tipoComprobanteRepository;

    @InjectMocks
    private TipoComprobanteServiceImpl tipoComprobanteService;

    // 1. findAllReturnsRepositoryData
    @Test
    void listarTodosRetornaDatosDelRepositorio() {
        // 1. Preparación
        TipoComprobante tc1 = new TipoComprobante();
        tc1.setIdTipoComprobante(1L);
        tc1.setDescripcion("Boleta");
        List<TipoComprobante> listaEsperada = List.of(tc1);

        when(tipoComprobanteRepository.findAll()).thenReturn(listaEsperada);

        // 2. Ejecución
        List<TipoComprobante> resultado = tipoComprobanteService.listarTodos();

        // 3. Verificación
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("Boleta", resultado.get(0).getDescripcion());
        verify(tipoComprobanteRepository).findAll();
    }

    // 2. findByIdReturnsEntityWhenExists
    @Test
    void buscarPorIdRetornaEntidadCuandoExiste() {
        // 1. Preparación
        TipoComprobante tc = new TipoComprobante();
        tc.setIdTipoComprobante(1L);
        tc.setDescripcion("Factura");

        when(tipoComprobanteRepository.findById(1L)).thenReturn(Optional.of(tc));

        // 2. Ejecución
        TipoComprobante resultado = tipoComprobanteService.buscarPorId(1L);

        // 3. Verificación
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdTipoComprobante());
        verify(tipoComprobanteRepository).findById(1L);
    }

    // 3. findByIdThrowsWhenNotExists
    @Test
    void buscarPorIdLanzaExcepcionCuandoNoExiste() {
        // 1. Preparación
        when(tipoComprobanteRepository.findById(99L)).thenReturn(Optional.empty());

        // 2. Ejecución y Verificación
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            tipoComprobanteService.buscarPorId(99L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Tipo de comprobante no encontrado"));
        verify(tipoComprobanteRepository).findById(99L);
    }

    // 4. saveForcesNullIdBeforePersisting
    @Test
    void crearFuerzaIdNuloAntesDeGuardar() {
        // 1. Preparación
        TipoComprobante peticionConIdFalso = new TipoComprobante();
        peticionConIdFalso.setIdTipoComprobante(999L);
        peticionConIdFalso.setDescripcion("Ticket");

        when(tipoComprobanteRepository.save(any(TipoComprobante.class))).thenReturn(peticionConIdFalso);

        // 2. Ejecución
        tipoComprobanteService.crear(peticionConIdFalso);

        // 3. Verificación
        assertNull(peticionConIdFalso.getIdTipoComprobante());
        verify(tipoComprobanteRepository).save(peticionConIdFalso);
    }

    // 5. updateChangesFieldsAndPersists
    @Test
    void actualizarCambiaCamposYGuarda() {
        // 1. Preparación
        TipoComprobante originalEnBD = new TipoComprobante();
        originalEnBD.setIdTipoComprobante(1L);
        originalEnBD.setDescripcion("Viejo");
        originalEnBD.setCodigoSunat("00");

        TipoComprobante datosNuevos = new TipoComprobante();
        datosNuevos.setDescripcion("Nuevo");
        datosNuevos.setCodigoSunat("01");
        datosNuevos.setEstado(false);

        when(tipoComprobanteRepository.findById(1L)).thenReturn(Optional.of(originalEnBD));
        when(tipoComprobanteRepository.save(originalEnBD)).thenReturn(originalEnBD);

        // 2. Ejecución
        tipoComprobanteService.actualizar(1L, datosNuevos);

        // 3. Verificación
        assertEquals("Nuevo", originalEnBD.getDescripcion());
        assertEquals("01", originalEnBD.getCodigoSunat());
        assertFalse(originalEnBD.getEstado());
        verify(tipoComprobanteRepository).save(originalEnBD);
    }

    // 6. updateThrowsWhenNotExists
    @Test
    void actualizarLanzaExcepcionCuandoNoExiste() {
        // 1. Preparación
        when(tipoComprobanteRepository.findById(99L)).thenReturn(Optional.empty());
        TipoComprobante nuevosDatos = new TipoComprobante();

        // 2. Ejecución y Verificación
        assertThrows(ResponseStatusException.class, () -> {
            tipoComprobanteService.actualizar(99L, nuevosDatos);
        });

        verify(tipoComprobanteRepository, never()).save(any());
    }

    // 7. deleteRemovesExistingEntity
    @Test
    void eliminarBorraEntidadExistente() {
        // 1. Preparación
        TipoComprobante tcExistente = new TipoComprobante();
        tcExistente.setIdTipoComprobante(1L);

        when(tipoComprobanteRepository.findById(1L)).thenReturn(Optional.of(tcExistente));

        // 2. Ejecución
        tipoComprobanteService.eliminar(1L);

        // 3. Verificación
        verify(tipoComprobanteRepository).findById(1L);
        verify(tipoComprobanteRepository).delete(tcExistente);
    }
}