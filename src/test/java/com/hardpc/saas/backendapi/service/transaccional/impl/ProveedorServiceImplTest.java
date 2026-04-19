package com.hardpc.saas.backendapi.service.transaccional.impl;

import com.hardpc.saas.backendapi.entity.Proveedor;
import com.hardpc.saas.backendapi.repository.transaccional.ProveedorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProveedorServiceImplTest {

    @Mock
    private ProveedorRepository repository;

    @InjectMocks
    private ProveedorServiceImpl service;

    /* TEST LISTAR */

    @Test
    void listar_ok() {

        Proveedor proveedor = new Proveedor();
        proveedor.setIdProveedor(1L);
        proveedor.setNombreComercial("TechStore");

        when(repository.findAll()).thenReturn(List.of(proveedor));

        List<Proveedor> resultado = service.listarProveedores();

        assertEquals(1, resultado.size());
        assertEquals("TechStore", resultado.get(0).getNombreComercial());

        verify(repository, times(1)).findAll();
    }

    /* TEST BUSCAR POR ID */

    @Test
    void buscarPorId_ok() {

        Proveedor proveedor = new Proveedor();
        proveedor.setIdProveedor(1L);
        proveedor.setNombreComercial("TechStore");

        when(repository.findById(1L))
                .thenReturn(Optional.of(proveedor));

        Proveedor resultado = service.buscarPorId(1L);

        assertEquals(1L, resultado.getIdProveedor());
        assertEquals("TechStore", resultado.getNombreComercial());

        verify(repository, times(1)).findById(1L);
    }

    /* TEST GUARDAR*/

    @Test
    void guardar_ok() {

        Proveedor proveedor = new Proveedor();
        proveedor.setNombreComercial("TechStore");

        when(repository.save(any())).thenReturn(proveedor);

        Proveedor resultado = service.guardarProveedor(proveedor);

        assertEquals("TechStore", resultado.getNombreComercial());

        verify(repository, times(1)).save(proveedor);
    }

    /* TEST ACTUALIZAR*/

    @Test
    void actualizar_ok() {

        Proveedor existente = new Proveedor();
        existente.setIdProveedor(1L);
        existente.setNombreComercial("OldName");

        Proveedor nuevo = new Proveedor();
        nuevo.setRazonSocial("Nueva Razon");
        nuevo.setNombreComercial("NewName");
        nuevo.setRuc("123");
        nuevo.setDireccion("Av Peru");
        nuevo.setTelefono("999");
        nuevo.setEmail("test@test.com");
        nuevo.setEstado(true);

        when(repository.findById(1L))
                .thenReturn(Optional.of(existente));

        when(repository.save(any())).thenReturn(existente);

        Proveedor resultado = service.actualizarProveedor(1L, nuevo);

        assertEquals("NewName", resultado.getNombreComercial());
        assertEquals("Nueva Razon", resultado.getRazonSocial());
        assertEquals("123", resultado.getRuc());

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(existente);
    }

    /* TEST DELETE */

    @Test
    void delete_ok() {

        Proveedor proveedor = new Proveedor();
        proveedor.setIdProveedor(1L);

        when(repository.findById(1L))
                .thenReturn(Optional.of(proveedor));

        doNothing().when(repository).delete(proveedor);

        service.deleteById(1L);

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).delete(proveedor);
    }

    /* TEST ERROR NO ENCONTRADO */
    @Test
    void buscarPorId_error() {

        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.buscarPorId(99L)
        );

        assertEquals("Proveedor no encontrado", ex.getMessage());

        verify(repository, times(1)).findById(99L);
    }

}