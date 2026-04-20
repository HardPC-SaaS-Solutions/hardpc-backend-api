package com.hardpc.saas.backendapi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hardpc.saas.backendapi.entity.FormaPago;
import com.hardpc.saas.backendapi.repository.FormaPagoRepository;
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
class FormaPagoServiceImplTest {

    @Mock
    private FormaPagoRepository formaPagoRepository;

    @InjectMocks
    private FormaPagoServiceImpl formaPagoService;

    @Test
    void listarTodosRetornaDatosDelRepositorio() {
        // 1. Preparación: Creamos datos ficticios
        FormaPago f1 = new FormaPago();
        f1.setIdFormaPago(1L);
        f1.setDescripcion("Yape");
        List<FormaPago> listaEsperada = List.of(f1);

        when(formaPagoRepository.findAll()).thenReturn(listaEsperada);

        // 2. Ejecución: Llamamos al método del servicio
        List<FormaPago> resultado = formaPagoService.listarTodos();

        // 3. Verificación: Validamos que la lista no esté vacía y contenga lo esperado
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("Yape", resultado.get(0).getDescripcion());
        verify(formaPagoRepository).findAll();
    }

    @Test
    void buscarPorIdRetornaEntidadCuandoExiste() {
        // 1. Preparación: Simulamos que existe una forma de pago en la BD
        FormaPago formaPago = new FormaPago();
        formaPago.setIdFormaPago(1L);
        formaPago.setDescripcion("Efectivo");

        when(formaPagoRepository.findById(1L)).thenReturn(Optional.of(formaPago));

        // 2. Ejecución: Buscamos por el ID existente
        FormaPago resultado = formaPagoService.buscarPorId(1L);

        // 3. Verificación: Validamos que el objeto devuelto sea el correcto
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdFormaPago());
        verify(formaPagoRepository).findById(1L);
    }

    @Test
    void buscarPorIdLanzaExcepcionCuandoNoExiste() {
        // 1. Preparación
        when(formaPagoRepository.findById(99L)).thenReturn(Optional.empty());

        // 2. Ejecución
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            formaPagoService.buscarPorId(99L);
        });

        // 3. Verificaciones
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Forma de pago no encontrada"));

        verify(formaPagoRepository).findById(99L);
    }


    @Test
    void crearFuerzaIdNuloAntesDeGuardar() {
        // 1. Preparación: Petición con un ID malicioso que debe ser ignorado
        FormaPago peticionConIdFalso = new FormaPago();
        peticionConIdFalso.setIdFormaPago(999L);
        peticionConIdFalso.setDescripcion("Plin");

        when(formaPagoRepository.save(any(FormaPago.class))).thenReturn(peticionConIdFalso);

        // 2. Ejecución: Intentamos crear la entidad
        formaPagoService.crear(peticionConIdFalso);

        // 3. Verificación: El ID debe haberse limpiado (null) antes de ir al repositorio
        assertNull(peticionConIdFalso.getIdFormaPago());
        verify(formaPagoRepository).save(peticionConIdFalso);
    }

    @Test
    void actualizarCambiaCamposYGuarda() {
        // 1. Preparación: Datos originales en BD y datos nuevos para actualizar
        FormaPago originalEnBD = new FormaPago();
        originalEnBD.setIdFormaPago(1L);
        originalEnBD.setDescripcion("Viejo");

        FormaPago datosNuevos = new FormaPago();
        datosNuevos.setDescripcion("Nuevo");
        datosNuevos.setEstado(false);

        when(formaPagoRepository.findById(1L)).thenReturn(Optional.of(originalEnBD));
        when(formaPagoRepository.save(originalEnBD)).thenReturn(originalEnBD);

        // 2. Ejecución: Actualizamos el registro con ID 1
        formaPagoService.actualizar(1L, datosNuevos);

        // 3. Verificación: Los campos del objeto original deben haber cambiado
        assertEquals("Nuevo", originalEnBD.getDescripcion());
        assertFalse(originalEnBD.getEstado());
        verify(formaPagoRepository).save(originalEnBD);
    }

    @Test
    void actualizarLanzaExcepcionCuandoNoExiste() {
        // 1. Preparación: El registro a actualizar no existe en la BD
        when(formaPagoRepository.findById(99L)).thenReturn(Optional.empty());
        FormaPago nuevosDatos = new FormaPago();

        // 2. Ejecución y Verificación: Debe lanzar excepción y nunca llamar al método save
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            formaPagoService.actualizar(99L, nuevosDatos);
        });

        // 3. Validaciones adicionales: status y mensaje
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Forma de pago no encontrada"));
        verify(formaPagoRepository, never()).save(any());
    }

    @Test
    void eliminarBorraEntidadExistente() {
        // 1. Preparación: Entidad que vamos a borrar
        FormaPago formaPagoExistente = new FormaPago();
        formaPagoExistente.setIdFormaPago(1L);

        when(formaPagoRepository.findById(1L)).thenReturn(Optional.of(formaPagoExistente));

        // 2. Ejecución: Eliminamos por ID
        formaPagoService.eliminar(1L);

        // 3. Verificación: Validamos que se buscó y luego se llamó al borrado
        verify(formaPagoRepository).findById(1L);
        verify(formaPagoRepository).delete(formaPagoExistente);
    }
}