package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.entity.Proveedor;
import com.hardpc.saas.backendapi.repository.ProveedorRepository;
import com.hardpc.saas.backendapi.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Proveedor> listarTodos() {
        return proveedorRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Proveedor buscarPorId(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado con id: " + id));
    }

    @Override
    public Proveedor crear(Proveedor proveedor) {
        proveedor.setIdProveedor(null);
        return proveedorRepository.save(proveedor);
    }

    @Override
    public Proveedor actualizar(Long id, Proveedor proveedor) {
        Proveedor existente = buscarPorId(id);
        existente.setRuc(proveedor.getRuc());
        existente.setRazonSocial(proveedor.getRazonSocial());
        existente.setNombreComercial(proveedor.getNombreComercial());
        existente.setDireccion(proveedor.getDireccion());
        existente.setTelefono(proveedor.getTelefono());
        existente.setEmail(proveedor.getEmail());
        existente.setEstado(proveedor.getEstado());
        return proveedorRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        Proveedor existente = buscarPorId(id);
        proveedorRepository.delete(existente);
    }
}