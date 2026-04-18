package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.ItemSerial;
import com.hardpc.saas.backendapi.service.ItemSerialService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/item-serial")
public class ItemSerialController {

    private final ItemSerialService service;

    public ItemSerialController(ItemSerialService service) {
        this.service = service;
    }

    @GetMapping
    public List<ItemSerial> listar() {
        return service.listar();
    }

    @PostMapping
    public ItemSerial crear(@RequestBody ItemSerial item) {
        return service.guardar(item);
    }

    @GetMapping("/{id}")
    public ItemSerial buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}