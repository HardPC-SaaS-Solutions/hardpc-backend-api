package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.Persona;
import java.util.List;

public interface PersonaService {
    List<Persona> listarTodos();
    Persona buscarPorId(Long id);
    void eliminar(Long id);
    // Nota: crear y actualizar se gestionan en las subclases Usuario y Cliente
}