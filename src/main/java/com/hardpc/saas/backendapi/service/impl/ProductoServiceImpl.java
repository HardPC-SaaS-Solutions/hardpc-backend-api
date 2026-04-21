package com.hardpc.saas.backendapi.service.impl;

import com.hardpc.saas.backendapi.entity.Producto;
import com.hardpc.saas.backendapi.repository.ProductoRepository;
import com.hardpc.saas.backendapi.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado con id: " + id));
    }

    @Override
    public Producto crear(Producto producto) {
        producto.setIdProducto(null);
        return productoRepository.save(producto);
    }

    @Override
    public Producto actualizar(Long id, Producto producto) {
        Producto existente = buscarPorId(id);

        existente.setCodigoSku(producto.getCodigoSku());
        existente.setDescripcion(producto.getDescripcion());
        existente.setPrecioUsd(producto.getPrecioUsd());
        existente.setMesesGarantia(producto.getMesesGarantia());
        existente.setEsSerializado(producto.getEsSerializado());
        existente.setMarca(producto.getMarca());
        existente.setCategoria(producto.getCategoria());
        existente.setUnidadMedida(producto.getUnidadMedida());
        existente.setEstado(producto.getEstado());
        existente.setImagenUrl(producto.getImagenUrl());

        return productoRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        Producto existente = buscarPorId(id);
        productoRepository.delete(existente);
    }
}