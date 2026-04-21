package com.hardpc.saas.backendapi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hardpc.saas.backendapi.entity.Proveedor;
import com.hardpc.saas.backendapi.repository.ProveedorRepository;
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
class ProveedorServiceImplTest {

    @Mock
    private ProveedorRepository proveedorRepository;

    @InjectMocks
    private ProveedorServiceImpl proveedorService;

    @Test
    void listarTodosRetornaDatosDelRepositorio() {
        Proveedor p = new Proveedor();
        p.setIdProveedor(1L);
        p.setRuc("20123456789");
        p.setRazonSocial("Tech SAC");
        p.setEstado(true);

        when(proveedorRepository.findAll()).thenReturn(List.of(p));

        List<Proveedor> resultado = proveedorService.listarTodos();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("20123456789", resultado.get(0).getRuc());
        assertEquals("Tech SAC", resultado.get(0).getRazonSocial());
        assertTrue(resultado.get(0).getEstado());
        verify(proveedorRepository).findAll();
    }

    @Test
    void buscarPorIdRetornaEntidadCuandoExiste() {
        Proveedor p = new Proveedor();
        p.setIdProveedor(1L);
        p.setRuc("10987654321");
        p.setEmail("contacto@tech.com");

        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(p));

        Proveedor resultado = proveedorService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdProveedor());
        assertEquals("10987654321", resultado.getRuc());
        assertEquals("contacto@tech.com", resultado.getEmail());
    }

    @Test
    void buscarPorIdLanzaExcepcionCuandoNoExiste() {
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> proveedorService.buscarPorId(99L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void crearFuerzaIdNuloAntesDeGuardar() {
        Proveedor peticion = new Proveedor();
        peticion.setIdProveedor(999L);
        peticion.setRuc("10123456789");
        peticion.setRazonSocial("Nueva Empresa EIRL");
        peticion.setTelefono("987654321");

        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(peticion);

        proveedorService.crear(peticion);

        assertNull(peticion.getIdProveedor());
        assertEquals("10123456789", peticion.getRuc());
        assertEquals("Nueva Empresa EIRL", peticion.getRazonSocial());
        verify(proveedorRepository).save(peticion);
    }

    @Test
    void actualizarCambiaCamposYGuarda() {
        Proveedor original = new Proveedor();
        original.setIdProveedor(1L);
        original.setRuc("20000000001");
        original.setRazonSocial("Vieja SAC");
        original.setEstado(true);

        Proveedor nuevosDatos = new Proveedor();
        nuevosDatos.setRuc("20000000002");
        nuevosDatos.setRazonSocial("Actualizada SAC");
        nuevosDatos.setEstado(false);

        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(original));
        when(proveedorRepository.save(original)).thenReturn(original);

        Proveedor actualizado = proveedorService.actualizar(1L, nuevosDatos);

        assertEquals("20000000002", actualizado.getRuc());
        assertEquals("Actualizada SAC", actualizado.getRazonSocial());
        assertFalse(actualizado.getEstado());
        verify(proveedorRepository).save(original);
    }

    @Test
    void actualizarLanzaExcepcionCuandoNoExiste() {
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> proveedorService.actualizar(99L, new Proveedor()));
        verify(proveedorRepository, never()).save(any());
    }

    @Test
    void eliminarBorraEntidadExistente() {
        Proveedor existente = new Proveedor();
        existente.setIdProveedor(1L);
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(existente));

        proveedorService.eliminar(1L);

        verify(proveedorRepository).delete(existente);
    }
}