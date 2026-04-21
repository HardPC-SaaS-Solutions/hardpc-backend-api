package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.entity.Proveedor;
import com.hardpc.saas.backendapi.repository.ProveedorRepository;
import com.hardpc.saas.backendapi.service.ProveedorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository repository;

    public ProveedorServiceImpl(ProveedorRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Proveedor> listarProveedores() {
        return repository.findAll();
    }

    @Override
    public Proveedor buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(()->new RuntimeException("Proveedor no encontrado"));
    }

    @Override
    public Proveedor guardarProveedor(Proveedor proveedor) {
        return repository.save(proveedor);
    }

    @Override
    public Proveedor actualizarProveedor(Long id, Proveedor proveedor) {
        Proveedor actualizado = buscarPorId(id);
        actualizado.setRuc(proveedor.getRuc());
        actualizado.setRazonSocial(proveedor.getRazonSocial());
        actualizado.setNombreComercial(proveedor.getNombreComercial());
        actualizado.setDireccion(proveedor.getDireccion());
        actualizado.setTelefono(proveedor.getTelefono());
        actualizado.setEmail(proveedor.getEmail());
        actualizado.setEstado(proveedor.getEstado());

        return repository.save(actualizado);
    }

    @Override
    public void deleteById(Long id) {
        Proveedor proveedor = buscarPorId(id);
        repository.delete(proveedor);

    }
}
