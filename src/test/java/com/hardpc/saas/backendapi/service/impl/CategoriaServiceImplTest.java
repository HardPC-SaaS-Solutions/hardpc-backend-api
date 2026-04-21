package com.hardpc.saas.backendapi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hardpc.saas.backendapi.entity.Categoria;
import com.hardpc.saas.backendapi.repository.CategoriaRepository;
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
class CategoriaServiceImplTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaServiceImpl categoriaService;

    @Test
    void listarTodosRetornaDatosDelRepositorio() {
        // 1. Preparación
        Categoria c1 = new Categoria();
        c1.setIdCategoria(1L);
        c1.setNombre("Laptops");
        when(categoriaRepository.findAll()).thenReturn(List.of(c1));

        // 2. Ejecución
        List<Categoria> resultado = categoriaService.listarTodos();

        // 3. Verificación
        assertFalse(resultado.isEmpty());
        assertEquals("Laptops", resultado.get(0).getNombre());
        verify(categoriaRepository).findAll();
    }

    @Test
    void buscarPorIdRetornaEntidadCuandoExiste() {
        // 1. Preparación
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(1L);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        // 2. Ejecución
        Categoria resultado = categoriaService.buscarPorId(1L);

        // 3. Verificación
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdCategoria());
    }

    @Test
    void buscarPorIdLanzaExcepcionCuandoNoExiste() {
        // 1. Preparación
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        // 2. Ejecución y Verificación
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            categoriaService.buscarPorId(99L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(categoriaRepository).findById(99L);
    }

    @Test
    void crearFuerzaIdNuloAntesDeGuardar() {
        // 1. Preparación
        Categoria peticion = new Categoria();
        peticion.setIdCategoria(999L);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(peticion);

        // 2. Ejecución
        categoriaService.crear(peticion);

        // 3. Verificación
        assertNull(peticion.getIdCategoria());
        verify(categoriaRepository).save(peticion);
    }

    @Test
    void actualizarCambiaCamposYGuarda() {
        // 1. Preparación
        Categoria original = new Categoria();
        original.setIdCategoria(1L);
        original.setNombre("Antiguo");

        Categoria nuevosDatos = new Categoria();
        nuevosDatos.setNombre("Nuevo");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(original));
        when(categoriaRepository.save(original)).thenReturn(original);

        // 2. Ejecución
        categoriaService.actualizar(1L, nuevosDatos);

        // 3. Verificación
        assertEquals("Nuevo", original.getNombre());
        verify(categoriaRepository).save(original);
    }

    @Test
    void actualizarLanzaExcepcionCuandoNoExiste() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> {
            categoriaService.actualizar(99L, new Categoria());
        });
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void eliminarBorraEntidadExistente() {
        Categoria existente = new Categoria();
        existente.setIdCategoria(1L);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(existente));

        categoriaService.eliminar(1L);

        verify(categoriaRepository).delete(existente);
    }
}