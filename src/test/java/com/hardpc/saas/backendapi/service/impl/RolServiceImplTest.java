package com.hardpc.saas.backendapi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hardpc.saas.backendapi.entity.Rol;
import com.hardpc.saas.backendapi.repository.RolRepository;
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
class RolServiceImplTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolServiceImpl rolService;

    @Test
    void listarTodosRetornaDatosDelRepositorio() {
        Rol r1 = new Rol();
        r1.setIdRol(1L);
        r1.setNombre("ADMIN");
        when(rolRepository.findAll()).thenReturn(List.of(r1));

        List<Rol> resultado = rolService.listarTodos();

        assertFalse(resultado.isEmpty());
        assertEquals("ADMIN", resultado.get(0).getNombre());
        verify(rolRepository).findAll();
    }

    @Test
    void buscarPorIdRetornaEntidadCuandoExiste() {
        Rol rol = new Rol();
        rol.setIdRol(1L);
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));

        Rol resultado = rolService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdRol());
    }

    @Test
    void buscarPorIdLanzaExcepcionCuandoNoExiste() {
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            rolService.buscarPorId(99L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(rolRepository).findById(99L);
    }

    @Test
    void crearFuerzaIdNuloAntesDeGuardar() {
        Rol peticion = new Rol();
        peticion.setIdRol(999L);
        when(rolRepository.save(any(Rol.class))).thenReturn(peticion);

        rolService.crear(peticion);

        assertNull(peticion.getIdRol());
        verify(rolRepository).save(peticion);
    }

    @Test
    void actualizarCambiaCamposYGuarda() {
        Rol original = new Rol();
        original.setIdRol(1L);
        original.setNombre("VENDEDOR");

        Rol nuevosDatos = new Rol();
        nuevosDatos.setNombre("SUPERVISOR");

        when(rolRepository.findById(1L)).thenReturn(Optional.of(original));
        when(rolRepository.save(original)).thenReturn(original);

        rolService.actualizar(1L, nuevosDatos);

        assertEquals("SUPERVISOR", original.getNombre());
        verify(rolRepository).save(original);
    }

    @Test
    void actualizarLanzaExcepcionCuandoNoExiste() {
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> {
            rolService.actualizar(99L, new Rol());
        });
        verify(rolRepository, never()).save(any());
    }

    @Test
    void eliminarBorraEntidadExistente() {
        Rol existente = new Rol();
        existente.setIdRol(1L);
        when(rolRepository.findById(1L)).thenReturn(Optional.of(existente));

        rolService.eliminar(1L);

        verify(rolRepository).delete(existente);
    }
}