package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.entity.Cliente;
import com.hardpc.saas.backendapi.repository.ClienteRepository;
import com.hardpc.saas.backendapi.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado con id: " + id));
    }

    @Override
    public Cliente crear(Cliente cliente) {
        // ID nulo para asegurar inserción
        cliente.setIdPersona(null);
        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente actualizar(Long id, Cliente cliente) {
        Cliente existente = buscarPorId(id);

        // 1. Actualizamos los campos heredados de Persona
        existente.setNumeroDocumento(cliente.getNumeroDocumento());
        existente.setTipoDocumento(cliente.getTipoDocumento());
        existente.setNombres(cliente.getNombres());
        existente.setApellidos(cliente.getApellidos());
        existente.setRazonSocial(cliente.getRazonSocial());
        existente.setTelefono(cliente.getTelefono());
        existente.setEmail(cliente.getEmail());
        existente.setDireccion(cliente.getDireccion());
        existente.setEstado(cliente.getEstado());

        // 2. Actualizamos el campo específico de Cliente
        existente.setTipoCliente(cliente.getTipoCliente());

        return clienteRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        Cliente existente = buscarPorId(id);
        clienteRepository.delete(existente);
    }
}