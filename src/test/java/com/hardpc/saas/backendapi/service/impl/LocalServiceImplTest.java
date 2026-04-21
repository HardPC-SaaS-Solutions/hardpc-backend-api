package com.hardpc.saas.backendapi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hardpc.saas.backendapi.entity.Local;
import com.hardpc.saas.backendapi.repository.LocalRepository;
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
class LocalServiceImplTest {

    @Mock
    private LocalRepository localRepository;

    @InjectMocks
    private LocalServiceImpl localService;

    @Test
    void listarTodosRetornaDatosDelRepositorio() {
        Local l1 = new Local();
        l1.setIdLocal(1L);
        l1.setNombre("Sede Principal");
        when(localRepository.findAll()).thenReturn(List.of(l1));

        List<Local> resultado = localService.listarTodos();

        assertFalse(resultado.isEmpty());
        assertEquals("Sede Principal", resultado.get(0).getNombre());
        verify(localRepository).findAll();
    }

    @Test
    void buscarPorIdRetornaEntidadCuandoExiste() {
        Local local = new Local();
        local.setIdLocal(1L);
        when(localRepository.findById(1L)).thenReturn(Optional.of(local));

        Local resultado = localService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdLocal());
    }

    @Test
    void buscarPorIdLanzaExcepcionCuandoNoExiste() {
        when(localRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            localService.buscarPorId(99L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(localRepository).findById(99L);
    }

    @Test
    void crearFuerzaIdNuloAntesDeGuardar() {
        Local peticion = new Local();
        peticion.setIdLocal(999L);
        when(localRepository.save(any(Local.class))).thenReturn(peticion);

        localService.crear(peticion);

        assertNull(peticion.getIdLocal());
        verify(localRepository).save(peticion);
    }

    @Test
    void actualizarCambiaCamposYGuarda() {
        Local original = new Local();
        original.setIdLocal(1L);
        original.setNombre("Sede Sur");

        Local nuevosDatos = new Local();
        nuevosDatos.setNombre("Sede Norte");

        when(localRepository.findById(1L)).thenReturn(Optional.of(original));
        when(localRepository.save(original)).thenReturn(original);

        localService.actualizar(1L, nuevosDatos);

        assertEquals("Sede Norte", original.getNombre());
        verify(localRepository).save(original);
    }

    @Test
    void actualizarLanzaExcepcionCuandoNoExiste() {
        when(localRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> {
            localService.actualizar(99L, new Local());
        });
        verify(localRepository, never()).save(any());
    }

    @Test
    void eliminarBorraEntidadExistente() {
        Local existente = new Local();
        existente.setIdLocal(1L);
        when(localRepository.findById(1L)).thenReturn(Optional.of(existente));

        localService.eliminar(1L);

        verify(localRepository).delete(existente);
    }
}