package com.hardpc.saas.backendapi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hardpc.saas.backendapi.entity.Producto;
import com.hardpc.saas.backendapi.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Test
    void listarTodosRetornaDatosDelRepositorio() {
        Producto p1 = new Producto();
        p1.setIdProducto(1L);
        p1.setCodigoSku("SKU-001");
        when(productoRepository.findAll()).thenReturn(List.of(p1));

        List<Producto> resultado = productoService.listarTodos();

        assertFalse(resultado.isEmpty());
        assertEquals("SKU-001", resultado.get(0).getCodigoSku());
        verify(productoRepository).findAll();
    }

    @Test
    void buscarPorIdRetornaEntidadCuandoExiste() {
        Producto producto = new Producto();
        producto.setIdProducto(1L);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Producto resultado = productoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdProducto());
    }

    @Test
    void buscarPorIdLanzaExcepcionCuandoNoExiste() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            productoService.buscarPorId(99L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(productoRepository).findById(99L);
    }

    @Test
    void crearFuerzaIdNuloAntesDeGuardar() {
        Producto peticion = new Producto();
        peticion.setIdProducto(999L);
        peticion.setPrecioUsd(BigDecimal.valueOf(100));
        when(productoRepository.save(any(Producto.class))).thenReturn(peticion);

        productoService.crear(peticion);

        assertNull(peticion.getIdProducto());
        verify(productoRepository).save(peticion);
    }

    @Test
    void actualizarCambiaCamposYGuarda() {
        Producto original = new Producto();
        original.setIdProducto(1L);
        original.setDescripcion("Laptop Antigua");

        Producto nuevosDatos = new Producto();
        nuevosDatos.setDescripcion("Laptop Nueva");

        when(productoRepository.findById(1L)).thenReturn(Optional.of(original));
        when(productoRepository.save(original)).thenReturn(original);

        productoService.actualizar(1L, nuevosDatos);

        assertEquals("Laptop Nueva", original.getDescripcion());
        verify(productoRepository).save(original);
    }

    @Test
    void actualizarLanzaExcepcionCuandoNoExiste() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> {
            productoService.actualizar(99L, new Producto());
        });
        verify(productoRepository, never()).save(any());
    }

    @Test
    void eliminarBorraEntidadExistente() {
        Producto existente = new Producto();
        existente.setIdProducto(1L);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));

        productoService.eliminar(1L);

        verify(productoRepository).delete(existente);
    }
}