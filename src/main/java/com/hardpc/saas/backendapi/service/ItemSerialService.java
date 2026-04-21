package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.ItemSerial;
import java.util.List;

public interface ItemSerialService {
    List<ItemSerial> listarTodos();
    ItemSerial buscarPorId(Long id);
    ItemSerial crear(ItemSerial itemSerial);
    ItemSerial actualizar(Long id, ItemSerial itemSerial);
    void eliminar(Long id);
}