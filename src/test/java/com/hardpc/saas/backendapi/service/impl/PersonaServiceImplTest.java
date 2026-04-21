package com.hardpc.saas.backendapi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hardpc.saas.backendapi.entity.Persona;
import com.hardpc.saas.backendapi.repository.PersonaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PersonaServiceImplTest {

    @Mock
    private PersonaRepository personaRepository;

    @InjectMocks
    private PersonaServiceImpl personaService;

    @Test
    void listarTodosRetornaDatosDelRepositorio() {
        // Usamos un mock de la clase abstracta
        Persona p1 = mock(Persona.class);
        when(personaRepository.findAll()).thenReturn(List.of(p1));

        List<Persona> resultado = personaService.listarTodos();

        assertFalse(resultado.isEmpty());
        verify(personaRepository).findAll();
    }

    @Test
    void buscarPorIdRetornaEntidadCuandoExiste() {
        Persona p1 = mock(Persona.class);
        when(personaRepository.findById(1L)).thenReturn(Optional.of(p1));

        Persona resultado = personaService.buscarPorId(1L);

        assertNotNull(resultado);
        verify(personaRepository).findById(1L);
    }

    @Test
    void eliminarBorraEntidadExistente() {
        Persona p1 = mock(Persona.class);
        when(personaRepository.findById(1L)).thenReturn(Optional.of(p1));

        personaService.eliminar(1L);

        verify(personaRepository).delete(p1);
    }
}