package com.hardpc.saas.backendapi.service.transaccional.impl;

import com.hardpc.saas.backendapi.entity.TipoDocumento;
import com.hardpc.saas.backendapi.repository.transaccional.TipoDocumentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TipoDocumentoServiceImplTest {

    @Mock
    private TipoDocumentoRepository repository;

    @InjectMocks
    private TipoDocumentoServiceImpl service;

    // TEST: GUARDAR

    @Test
    void guardar_ok() {

        TipoDocumento tipo = new TipoDocumento();
        tipo.setNombre("DNI");

        when(repository.save(any())).thenReturn(tipo);

        TipoDocumento resultado = service.guardarTipo(tipo);

        assertEquals("DNI", resultado.getNombre());

        verify(repository, times(1)).save(tipo);
    }

    // TEST LISTAR

    @Test
    void listar_ok() {

        TipoDocumento tipo = new TipoDocumento();
        tipo.setIdTipoDocumento(1L);
        tipo.setNombre("DNI");

        when(repository.findAll()).thenReturn(List.of(tipo));

        List<TipoDocumento> resultado = service.listarTipoDocumentos();

        assertEquals(1, resultado.size());
        assertEquals("DNI", resultado.get(0).getNombre());

        verify(repository, times(1)).findAll();
    }

    // TEST BUSCAR POR ID
    @Test
    void buscarPorId_ok() {

        TipoDocumento tipo = new TipoDocumento();
        tipo.setIdTipoDocumento(1L);
        tipo.setNombre("DNI");

        when(repository.findById(1L))
                .thenReturn(Optional.of(tipo));

        TipoDocumento resultado = service.buscarPorId(1L);

        assertEquals(1L, resultado.getIdTipoDocumento());
        assertEquals("DNI", resultado.getNombre());

        verify(repository, times(1)).findById(1L);
    }

    // TEST ACTUALIZAR

    @Test
    void actualizar_ok() {

        TipoDocumento existente = new TipoDocumento();
        existente.setIdTipoDocumento(1L);
        existente.setNombre("DNI");

        TipoDocumento nuevo = new TipoDocumento();
        nuevo.setNombre("PASAPORTE");
        nuevo.setLongitudExacta(12);
        nuevo.setEstado(true);

        when(repository.findById(1L))
                .thenReturn(Optional.of(existente));

        when(repository.save(any())).thenReturn(existente);

        TipoDocumento resultado =
                service.actualizarTipoDocumento(1L, nuevo);

        assertEquals("PASAPORTE", resultado.getNombre());
        assertEquals(12, resultado.getLongitudExacta());
        assertEquals(true, resultado.getEstado());

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(existente);
    }

    // TEST DELETE

    @Test
    void eliminar_ok() {

        TipoDocumento tipo = new TipoDocumento();
        tipo.setIdTipoDocumento(1L);

        when(repository.findById(1L))
                .thenReturn(Optional.of(tipo));

        doNothing().when(repository).delete(tipo);

        service.eliminarTipoDocumento(1L);

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).delete(tipo);
    }

    // TEST ERROR NO ENCONTRADO

    @Test
    void buscarPorId_error() {

        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.buscarPorId(99L)
        );

        assertEquals("TipoDocumento no encontrado", ex.getMessage());

        verify(repository, times(1)).findById(99L);
    }

}