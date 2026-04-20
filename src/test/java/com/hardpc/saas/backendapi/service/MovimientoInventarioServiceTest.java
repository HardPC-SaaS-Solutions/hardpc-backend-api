package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.MovimientoInventario;
import com.hardpc.saas.backendapi.repository.MovimientoInventarioRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class MovimientoInventarioServiceTest {

    private final MovimientoInventarioRepository repository = Mockito.mock(MovimientoInventarioRepository.class);
    private final MovimientoInventarioService service = new MovimientoInventarioService(repository);

    @Test
    void guardar() {
        MovimientoInventario mov = new MovimientoInventario();

        Mockito.when(repository.save(mov)).thenReturn(mov);

        assertNotNull(service.guardar(mov));
    }
}