package com.hardpc.saas.backendapi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hardpc.saas.backendapi.entity.Marca;
import com.hardpc.saas.backendapi.repository.MarcaRepository;
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
class MarcaServiceImplTest {

    @Mock
    private MarcaRepository marcaRepository;

    @InjectMocks
    private MarcaServiceImpl marcaService;

    @Test
    void listarTodosRetornaDatosDelRepositorio() {
        // 1. Preparación
        Marca m1 = new Marca();
        m1.setIdMarca(1L);
        m1.setNombre("Logitech");
        when(marcaRepository.findAll()).thenReturn(List.of(m1));

        // 2. Ejecución
        List<Marca> resultado = marcaService.listarTodos();

        // 3. Verificación
        assertFalse(resultado.isEmpty());
        assertEquals("Logitech", resultado.get(0).getNombre());
        verify(marcaRepository).findAll();
    }

    @Test
    void buscarPorIdRetornaEntidadCuandoExiste() {
        // 1. Preparación
        Marca marca = new Marca();
        marca.setIdMarca(1L);
        when(marcaRepository.findById(1L)).thenReturn(Optional.of(marca));

        // 2. Ejecución
        Marca resultado = marcaService.buscarPorId(1L);

        // 3. Verificación
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdMarca());
    }

    @Test
    void buscarPorIdLanzaExcepcionCuandoNoExiste() {
        // 1. Preparación
        when(marcaRepository.findById(99L)).thenReturn(Optional.empty());

        // 2. Ejecución y Verificación
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            marcaService.buscarPorId(99L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(marcaRepository).findById(99L);
    }

    @Test
    void crearFuerzaIdNuloAntesDeGuardar() {
        // 1. Preparación
        Marca peticion = new Marca();
        peticion.setIdMarca(999L);
        when(marcaRepository.save(any(Marca.class))).thenReturn(peticion);

        // 2. Ejecución
        marcaService.crear(peticion);

        // 3. Verificación
        assertNull(peticion.getIdMarca());
        verify(marcaRepository).save(peticion);
    }

    @Test
    void actualizarCambiaCamposYGuarda() {
        // 1. Preparación
        Marca original = new Marca();
        original.setIdMarca(1L);
        original.setNombre("Asus");

        Marca nuevosDatos = new Marca();
        nuevosDatos.setNombre("Asus ROG");

        when(marcaRepository.findById(1L)).thenReturn(Optional.of(original));
        when(marcaRepository.save(original)).thenReturn(original);

        // 2. Ejecución
        marcaService.actualizar(1L, nuevosDatos);

        // 3. Verificación
        assertEquals("Asus ROG", original.getNombre());
        verify(marcaRepository).save(original);
    }

    @Test
    void actualizarLanzaExcepcionCuandoNoExiste() {
        when(marcaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> {
            marcaService.actualizar(99L, new Marca());
        });
        verify(marcaRepository, never()).save(any());
    }

    @Test
    void eliminarBorraEntidadExistente() {
        Marca existente = new Marca();
        existente.setIdMarca(1L);
        when(marcaRepository.findById(1L)).thenReturn(Optional.of(existente));

        marcaService.eliminar(1L);

        verify(marcaRepository).delete(existente);
    }
}