package com.hardpc.saas.backendapi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hardpc.saas.backendapi.entity.Cliente;
import com.hardpc.saas.backendapi.repository.ClienteRepository;
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
class ClienteServiceImplTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    @Test
    void listarTodosRetornaDatosDelRepositorio() {
        Cliente c1 = new Cliente();
        c1.setIdPersona(1L);
        c1.setTipoCliente("MINORISTA");
        when(clienteRepository.findAll()).thenReturn(List.of(c1));

        List<Cliente> resultado = clienteService.listarTodos();

        assertFalse(resultado.isEmpty());
        assertEquals("MINORISTA", resultado.get(0).getTipoCliente());
        verify(clienteRepository).findAll();
    }

    @Test
    void buscarPorIdRetornaEntidadCuandoExiste() {
        Cliente cliente = new Cliente();
        cliente.setIdPersona(1L);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        Cliente resultado = clienteService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdPersona());
    }

    @Test
    void buscarPorIdLanzaExcepcionCuandoNoExiste() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            clienteService.buscarPorId(99L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(clienteRepository).findById(99L);
    }

    @Test
    void crearFuerzaIdNuloAntesDeGuardar() {
        Cliente peticion = new Cliente();
        peticion.setIdPersona(999L);
        peticion.setTipoCliente("MAYORISTA");
        when(clienteRepository.save(any(Cliente.class))).thenReturn(peticion);

        clienteService.crear(peticion);

        assertNull(peticion.getIdPersona());
        verify(clienteRepository).save(peticion);
    }

    @Test
    void actualizarCambiaCamposYGuarda() {
        Cliente original = new Cliente();
        original.setIdPersona(1L);
        original.setTipoCliente("MINORISTA");
        original.setNombres("Maria"); // Campo heredado

        Cliente nuevosDatos = new Cliente();
        nuevosDatos.setTipoCliente("MAYORISTA");
        nuevosDatos.setNombres("Maria Elena"); // Campo heredado actualizado

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(original));
        when(clienteRepository.save(original)).thenReturn(original);

        clienteService.actualizar(1L, nuevosDatos);

        assertEquals("MAYORISTA", original.getTipoCliente());
        assertEquals("Maria Elena", original.getNombres());
        verify(clienteRepository).save(original);
    }

    @Test
    void actualizarLanzaExcepcionCuandoNoExiste() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> {
            clienteService.actualizar(99L, new Cliente());
        });
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void eliminarBorraEntidadExistente() {
        Cliente existente = new Cliente();
        existente.setIdPersona(1L);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(existente));

        clienteService.eliminar(1L);

        verify(clienteRepository).delete(existente);
    }
}