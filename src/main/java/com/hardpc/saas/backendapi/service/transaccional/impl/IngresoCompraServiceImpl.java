package com.hardpc.saas.backendapi.service.transaccional.impl;

import com.hardpc.saas.backendapi.entity.IngresoCompra;
import com.hardpc.saas.backendapi.entity.Proveedor;
import com.hardpc.saas.backendapi.repository.transaccional.IngresoCompraRepository;
import com.hardpc.saas.backendapi.repository.transaccional.ProveedorRepository;
import com.hardpc.saas.backendapi.service.transaccional.IngresoCompraService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngresoCompraServiceImpl implements IngresoCompraService {

    private final IngresoCompraRepository ingresoCompraRepository;
    private final ProveedorRepository proveedorRepository;

    public IngresoCompraServiceImpl(IngresoCompraRepository ingresoCompraRepository, ProveedorRepository proveedorRepository) {
        this.ingresoCompraRepository = ingresoCompraRepository;
        this.proveedorRepository = proveedorRepository;
    }

    @Override
    public List<IngresoCompra> listarIngresoCompra() {
        return ingresoCompraRepository.findAll();
    }

    @Override
    public IngresoCompra buscarPorId(Long id) {
        return ingresoCompraRepository.findById(id).orElseThrow(()->new RuntimeException("Ingreso no encontrado"));
    }

    @Override
    @Transactional
    public IngresoCompra guardarCompra(IngresoCompra ingreso) {

        /*VALIDAR EL PROVEEDOR*/
        Proveedor proveedor = proveedorRepository.findById(
                ingreso.getProveedor().getIdProveedor()).orElseThrow(()->new RuntimeException("Proveedor no encontrado"));

        ingreso.setProveedor(proveedor);

        /*ASIGNAR RELACIONES A LOS DETALLES*/

        if(ingreso.getDetalles()!=null){
            ingreso.getDetalles().forEach(det ->{
                det.setIngresoCompra(ingreso);
            });
        }

        return ingresoCompraRepository.save(ingreso);
    }

    @Override
    public void eliminarPorId(Long id) {
        IngresoCompra ingreso = buscarPorId(id);
        ingresoCompraRepository.delete(ingreso);

    }
}
