package com.hardpc.saas.backendapi.controller.transaccional;

import com.hardpc.saas.backendapi.entity.Proveedor;
import com.hardpc.saas.backendapi.service.transaccional.ProveedorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProveedorControllerTest {

    @Mock
    private ProveedorService service;

    @InjectMocks
    private ProveedorController controller;

    /* TEST DE LISTAR */

    @Test
    void listar_ok() {

        Proveedor proveedor = new Proveedor();
        proveedor.setIdProveedor(1L);
        proveedor.setNombreComercial("TechStore");

        when(service.listarProveedores()).thenReturn(List.of(proveedor));

        ResponseEntity<List<Proveedor>> response = controller.listar();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("TechStore", response.getBody().get(0).getNombreComercial());

        verify(service, times(1)).listarProveedores();
    }

    /* TEST DE BUSCAR POR ID */

    @Test
    void buscarPorId_ok() {

        Proveedor proveedor = new Proveedor();
        proveedor.setIdProveedor(1L);
        proveedor.setNombreComercial("TechStore");

        when(service.buscarPorId(1L)).thenReturn(proveedor);

        ResponseEntity<Proveedor> response = controller.buscarPorId(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1L, response.getBody().getIdProveedor());
        assertEquals("TechStore", response.getBody().getNombreComercial());

        verify(service, times(1)).buscarPorId(1L);
    }

    /* TEST GUARDAR*/

    @Test
    void guardar_ok() {

        Proveedor proveedor = new Proveedor();
        proveedor.setIdProveedor(1L);
        proveedor.setNombreComercial("TechStore");

        when(service.guardarProveedor(any())).thenReturn(proveedor);

        ResponseEntity<Proveedor> response =
                controller.guardar(proveedor);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("TechStore", response.getBody().getNombreComercial());

        verify(service, times(1)).guardarProveedor(proveedor);
    }

    /*TEST ACTUALIZAR*/

    @Test
    void actualizar_ok() {

        Proveedor proveedor = new Proveedor();
        proveedor.setIdProveedor(1L);
        proveedor.setNombreComercial("UpdatedStore");

        when(service.actualizarProveedor(eq(1L), any(Proveedor.class))).thenReturn(proveedor);

        ResponseEntity<Proveedor> response =
                controller.actualizar(1L, proveedor);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("UpdatedStore", response.getBody().getNombreComercial());

        verify(service).actualizarProveedor(eq(1L), any(Proveedor.class));
    }

    /*TEST ELIMINAR */

    @Test
    void eliminar_ok() {

        doNothing().when(service).deleteById(1L);

        ResponseEntity<Void> response = controller.eliminar(1L);

        assertEquals(204, response.getStatusCode().value());

        verify(service, times(1)).deleteById(1L);
    }
}