package com.hardpc.saas.backendapi.service.transaccional.impl;

import com.hardpc.saas.backendapi.entity.DetalleIngreso;
import com.hardpc.saas.backendapi.entity.IngresoCompra;
import com.hardpc.saas.backendapi.repository.transaccional.DetalleIngresoRepository;
import com.hardpc.saas.backendapi.repository.transaccional.IngresoCompraRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DetalleIngresoServiceImplTest {

    @Mock
    private DetalleIngresoRepository detalleRepository;

    @Mock
    private IngresoCompraRepository compraRepository;

    @InjectMocks
    private DetalleIngresoServiceImpl service;

    /*TEST LISTAR*/
    @Test
    void listar_ok() {

        DetalleIngreso detalle = new DetalleIngreso();
        detalle.setIdDetalleIngreso(1L);
        detalle.setCantidad(5);

        when(detalleRepository.findAll()).thenReturn(List.of(detalle));

        List<DetalleIngreso> resultado = service.listar(); /*simula la base de datos*/

        assertEquals(1, resultado.size());                  /*la lista tiene el tamaño correcto*/
        assertEquals(5, resultado.get(0).getCantidad());    /*los valores son los esperados*/

        verify(detalleRepository, times(1)).findAll();  /*el service llama al repositorio*/

    }

    /* TEST BUSCAR POR ID */
    @Test
    void buscarPorId_ok() {

        DetalleIngreso detalle = new DetalleIngreso();
        detalle.setIdDetalleIngreso(1L);
        detalle.setCantidad(3);

        when(detalleRepository.findById(1L))
                .thenReturn(Optional.of(detalle));

        DetalleIngreso resultado = service.buscarPorId(1L);

        assertEquals(3, resultado.getCantidad());
        assertEquals(1L, resultado.getIdDetalleIngreso());

        verify(detalleRepository, times(1)).findById(1L);
    }

    /* TEST GUARDAR DETALLE */
    @Test
    void guardarDetalle_ok() {

        // ingreso simulado
        IngresoCompra ingreso = new IngresoCompra();
        ingreso.setIdIngreso(1L);

        // detalle input
        DetalleIngreso detalle = new DetalleIngreso();
        detalle.setCantidad(5);
        detalle.setPrecioCompraUnitario(new BigDecimal("50.00"));

        // IMPORTANTE: simular relación
        detalle.setIngresoCompra(ingreso);

        when(compraRepository.findById(1L))
                .thenReturn(Optional.of(ingreso));

        when(detalleRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        DetalleIngreso resultado = service.guardarDetalle(detalle);

        assertEquals(5, resultado.getCantidad());
        assertEquals(1L, resultado.getIngresoCompra().getIdIngreso());

        verify(compraRepository, times(1)).findById(1L);
        verify(detalleRepository, times(1)).save(detalle);
    }

    /*TEST ERROR SI NO EXISTE INGRESO*/
    @Test
    void guardarDetalle_error_ingresoNoExiste() {

        IngresoCompra ingreso = new IngresoCompra();
        ingreso.setIdIngreso(99L);

        DetalleIngreso detalle = new DetalleIngreso();
        detalle.setIngresoCompra(ingreso);

        when(compraRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.guardarDetalle(detalle)
        );

        assertEquals("Ingreso no encontrado", ex.getMessage());

        verify(compraRepository, times(1)).findById(99L);
        verify(detalleRepository, never()).save(any());
    }

}