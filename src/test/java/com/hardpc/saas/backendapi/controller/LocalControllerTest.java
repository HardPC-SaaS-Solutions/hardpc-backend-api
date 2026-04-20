package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.Local;
import com.hardpc.saas.backendapi.service.LocalService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LocalControllerTest {

    private final LocalService service = Mockito.mock(LocalService.class);
    private final LocalController controller = new LocalController(service);

    @Test
    void listar() {
        Mockito.when(service.listar()).thenReturn(List.of(new Local()));

        List<Local> lista = controller.listar();

        assertFalse(lista.isEmpty());
    }

    @Test
    void crear() {
        Local local = new Local();

        Mockito.when(service.guardar(local)).thenReturn(local);

        Local resultado = controller.crear(local);

        assertNotNull(resultado);
    }
}