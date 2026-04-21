package com.hardpc.saas.backendapi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hardpc.saas.backendapi.entity.Usuario;
import com.hardpc.saas.backendapi.repository.UsuarioRepository;
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
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    void listarTodosRetornaDatosDelRepositorio() {
        Usuario u1 = new Usuario();
        u1.setIdPersona(1L);
        u1.setUsername("admin");
        when(usuarioRepository.findAll()).thenReturn(List.of(u1));

        List<Usuario> resultado = usuarioService.listarTodos();

        assertFalse(resultado.isEmpty());
        assertEquals("admin", resultado.get(0).getUsername());
        verify(usuarioRepository).findAll();
    }

    @Test
    void buscarPorIdRetornaEntidadCuandoExiste() {
        Usuario usuario = new Usuario();
        usuario.setIdPersona(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdPersona());
    }

    @Test
    void buscarPorIdLanzaExcepcionCuandoNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            usuarioService.buscarPorId(99L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(usuarioRepository).findById(99L);
    }

    @Test
    void crearFuerzaIdNuloAntesDeGuardar() {
        Usuario peticion = new Usuario();
        peticion.setIdPersona(999L);
        peticion.setUsername("nuevo_user");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(peticion);

        usuarioService.crear(peticion);

        assertNull(peticion.getIdPersona());
        verify(usuarioRepository).save(peticion);
    }

    @Test
    void actualizarCambiaCamposYGuarda() {
        Usuario original = new Usuario();
        original.setIdPersona(1L);
        original.setUsername("old_user");
        original.setNombres("Juan"); // Campo heredado

        Usuario nuevosDatos = new Usuario();
        nuevosDatos.setUsername("new_user");
        nuevosDatos.setNombres("Juan Carlos");
        nuevosDatos.setAvatarUrl("http://imagen.com/avatar.jpg"); // Actualizando campo heredado

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(original));
        when(usuarioRepository.save(original)).thenReturn(original);

        usuarioService.actualizar(1L, nuevosDatos);

        assertEquals("new_user", original.getUsername());
        assertEquals("Juan Carlos", original.getNombres());
        assertEquals("http://imagen.com/avatar.jpg", original.getAvatarUrl());
        verify(usuarioRepository).save(original);
    }

    @Test
    void actualizarLanzaExcepcionCuandoNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> {
            usuarioService.actualizar(99L, new Usuario());
        });
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void eliminarBorraEntidadExistente() {
        Usuario existente = new Usuario();
        existente.setIdPersona(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));

        usuarioService.eliminar(1L);

        verify(usuarioRepository).delete(existente);
    }
}