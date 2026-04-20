package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.entity.Producto;
import com.hardpc.saas.backendapi.repository.ProductoRepository;
import com.hardpc.saas.backendapi.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    @Override
    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    @Override
    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    @Override
    public Producto actualizar(Long id, Producto producto) {
        Producto existente = productoRepository.findById(id).orElse(null);

        if (existente != null) {
            existente.setDescripcion(producto.getDescripcion());
            existente.setPrecioSoles(producto.getPrecioSoles());
            existente.setMesesGarantia(producto.getMesesGarantia());
            existente.setEsSerializado(producto.getEsSerializado());
            existente.setMarca(producto.getMarca());
            existente.setCategoria(producto.getCategoria());
            existente.setUnidadMedida(producto.getUnidadMedida());
            existente.setEstado(producto.getEstado());
            existente.setImagenUrl(producto.getImagenUrl());

            return productoRepository.save(existente);
        }

        return null;
    }

    @Override
    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }
}
