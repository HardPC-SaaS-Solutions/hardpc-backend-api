package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.Usuario;
import com.hardpc.saas.backendapi.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioRestControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioRestController usuarioRestController;

    @Test
    void listarOk() {
        when(usuarioService.listarTodos()).thenReturn(List.of(new Usuario()));
        ResponseEntity<List<Usuario>> response = usuarioRestController.listar();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(usuarioService).listarTodos();
    }

    @Test
    void buscarPorIdOk() {
        Usuario usuario = new Usuario();
        usuario.setIdPersona(1L);
        when(usuarioService.buscarPorId(1L)).thenReturn(usuario);

        ResponseEntity<Usuario> response = usuarioRestController.buscarPorId(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(usuarioService).buscarPorId(1L);
    }

    @Test
    void crearOk() {
        Usuario entrada = new Usuario();
        Usuario guardado = new Usuario();
        guardado.setIdPersona(1L);
        when(usuarioService.crear(entrada)).thenReturn(guardado);

        ResponseEntity<Usuario> response = usuarioRestController.crear(entrada);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/api/usuarios/1", response.getHeaders().getLocation().toString());
    }

    @Test
    void actualizarOk() {
        Usuario entrada = new Usuario();
        when(usuarioService.actualizar(1L, entrada)).thenReturn(entrada);

        ResponseEntity<Usuario> response = usuarioRestController.actualizar(1L, entrada);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void eliminarOk() {
        ResponseEntity<Void> response = usuarioRestController.eliminar(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(usuarioService).eliminar(1L);
    }
}