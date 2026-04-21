package com.hardpc.saas.backendapi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hardpc.saas.backendapi.entity.TipoDocumento;
import com.hardpc.saas.backendapi.repository.TipoDocumentoRepository;
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
class TipoDocumentoServiceImplTest {

    @Mock
    private TipoDocumentoRepository tipoDocumentoRepository;

    @InjectMocks
    private TipoDocumentoServiceImpl tipoDocumentoService;

    @Test
    void listarTodosRetornaDatosDelRepositorio() {
        TipoDocumento td = new TipoDocumento();
        td.setIdTipoDocumento(1L);
        td.setNombre("DNI");
        td.setLongitudExacta(8);
        td.setEstado(true);

        when(tipoDocumentoRepository.findAll()).thenReturn(List.of(td));

        List<TipoDocumento> resultado = tipoDocumentoService.listarTodos();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("DNI", resultado.get(0).getNombre());
        assertEquals(8, resultado.get(0).getLongitudExacta());
        assertTrue(resultado.get(0).getEstado());
        verify(tipoDocumentoRepository).findAll();
    }

    @Test
    void buscarPorIdRetornaEntidadCuandoExiste() {
        TipoDocumento td = new TipoDocumento();
        td.setIdTipoDocumento(1L);
        td.setNombre("RUC");
        td.setLongitudExacta(11);

        when(tipoDocumentoRepository.findById(1L)).thenReturn(Optional.of(td));

        TipoDocumento resultado = tipoDocumentoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdTipoDocumento());
        assertEquals("RUC", resultado.getNombre());
        assertEquals(11, resultado.getLongitudExacta());
    }

    @Test
    void buscarPorIdLanzaExcepcionCuandoNoExiste() {
        when(tipoDocumentoRepository.findById(99L)).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> tipoDocumentoService.buscarPorId(99L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void crearFuerzaIdNuloAntesDeGuardar() {
        TipoDocumento peticion = new TipoDocumento();
        peticion.setIdTipoDocumento(999L);
        peticion.setNombre("PASAPORTE");
        peticion.setLongitudExacta(12);

        when(tipoDocumentoRepository.save(any(TipoDocumento.class))).thenReturn(peticion);

        tipoDocumentoService.crear(peticion);

        assertNull(peticion.getIdTipoDocumento());
        assertEquals("PASAPORTE", peticion.getNombre());
        verify(tipoDocumentoRepository).save(peticion);
    }

    @Test
    void actualizarCambiaCamposYGuarda() {
        TipoDocumento original = new TipoDocumento();
        original.setIdTipoDocumento(1L);
        original.setNombre("RUC");
        original.setLongitudExacta(11);
        original.setEstado(true);

        TipoDocumento nuevosDatos = new TipoDocumento();
        nuevosDatos.setNombre("DNI");
        nuevosDatos.setLongitudExacta(8);
        nuevosDatos.setEstado(false);

        when(tipoDocumentoRepository.findById(1L)).thenReturn(Optional.of(original));
        when(tipoDocumentoRepository.save(original)).thenReturn(original);

        TipoDocumento actualizado = tipoDocumentoService.actualizar(1L, nuevosDatos);

        assertEquals("DNI", actualizado.getNombre());
        assertEquals(8, actualizado.getLongitudExacta());
        assertFalse(actualizado.getEstado());
        verify(tipoDocumentoRepository).save(original);
    }

    @Test
    void actualizarLanzaExcepcionCuandoNoExiste() {
        when(tipoDocumentoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> tipoDocumentoService.actualizar(99L, new TipoDocumento()));
        verify(tipoDocumentoRepository, never()).save(any());
    }

    @Test
    void eliminarBorraEntidadExistente() {
        TipoDocumento existente = new TipoDocumento();
        existente.setIdTipoDocumento(1L);
        when(tipoDocumentoRepository.findById(1L)).thenReturn(Optional.of(existente));

        tipoDocumentoService.eliminar(1L);

        verify(tipoDocumentoRepository).delete(existente);
    }
}