package com.hardpc.saas.backendapi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hardpc.saas.backendapi.entity.UnidadMedida;
import com.hardpc.saas.backendapi.repository.UnidadMedidaRepository;
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
class UnidadMedidaServiceImplTest {

    @Mock
    private UnidadMedidaRepository unidadMedidaRepository;

    @InjectMocks
    private UnidadMedidaServiceImpl unidadMedidaService;

    @Test
    void listarTodosRetornaDatosDelRepositorio() {
        UnidadMedida u1 = new UnidadMedida();
        u1.setIdUnidadMedida(1L);
        u1.setAbreviatura("UND");
        when(unidadMedidaRepository.findAll()).thenReturn(List.of(u1));

        List<UnidadMedida> resultado = unidadMedidaService.listarTodos();

        assertFalse(resultado.isEmpty());
        assertEquals("UND", resultado.get(0).getAbreviatura());
        verify(unidadMedidaRepository).findAll();
    }

    @Test
    void buscarPorIdRetornaEntidadCuandoExiste() {
        UnidadMedida unidad = new UnidadMedida();
        unidad.setIdUnidadMedida(1L);
        when(unidadMedidaRepository.findById(1L)).thenReturn(Optional.of(unidad));

        UnidadMedida resultado = unidadMedidaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdUnidadMedida());
    }

    @Test
    void buscarPorIdLanzaExcepcionCuandoNoExiste() {
        when(unidadMedidaRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            unidadMedidaService.buscarPorId(99L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(unidadMedidaRepository).findById(99L);
    }

    @Test
    void crearFuerzaIdNuloAntesDeGuardar() {
        UnidadMedida peticion = new UnidadMedida();
        peticion.setIdUnidadMedida(999L);
        when(unidadMedidaRepository.save(any(UnidadMedida.class))).thenReturn(peticion);

        unidadMedidaService.crear(peticion);

        assertNull(peticion.getIdUnidadMedida());
        verify(unidadMedidaRepository).save(peticion);
    }

    @Test
    void actualizarCambiaCamposYGuarda() {
        UnidadMedida original = new UnidadMedida();
        original.setIdUnidadMedida(1L);
        original.setAbreviatura("U");

        UnidadMedida nuevosDatos = new UnidadMedida();
        nuevosDatos.setAbreviatura("UND");

        when(unidadMedidaRepository.findById(1L)).thenReturn(Optional.of(original));
        when(unidadMedidaRepository.save(original)).thenReturn(original);

        unidadMedidaService.actualizar(1L, nuevosDatos);

        assertEquals("UND", original.getAbreviatura());
        verify(unidadMedidaRepository).save(original);
    }

    @Test
    void actualizarLanzaExcepcionCuandoNoExiste() {
        when(unidadMedidaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> {
            unidadMedidaService.actualizar(99L, new UnidadMedida());
        });
        verify(unidadMedidaRepository, never()).save(any());
    }

    @Test
    void eliminarBorraEntidadExistente() {
        UnidadMedida existente = new UnidadMedida();
        existente.setIdUnidadMedida(1L);
        when(unidadMedidaRepository.findById(1L)).thenReturn(Optional.of(existente));

        unidadMedidaService.eliminar(1L);

        verify(unidadMedidaRepository).delete(existente);
    }
}