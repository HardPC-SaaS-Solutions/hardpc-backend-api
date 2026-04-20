package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.Local;
import com.hardpc.saas.backendapi.repository.LocalRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LocalServiceTest {

    private final LocalRepository repository = Mockito.mock(LocalRepository.class);
    private final LocalService service = new LocalService(repository);

    @Test
    void guardar() {
        Local local = new Local();
        local.setIdLocal(1L);

        Mockito.when(repository.save(local)).thenReturn(local);

        Local resultado = service.guardar(local);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdLocal());
    }

    @Test
    void buscar() {
        Local local = new Local();
        local.setIdLocal(1L);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(local));

        Local resultado = service.buscar(1L);

        assertNotNull(resultado);
    }

    @Test
    void eliminar() {
        service.eliminar(1L);

        Mockito.verify(repository).deleteById(1L);
    }
}