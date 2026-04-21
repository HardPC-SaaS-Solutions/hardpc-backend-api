package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.ItemSerial;
import com.hardpc.saas.backendapi.service.ItemSerialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/items-seriales")
@RequiredArgsConstructor
public class ItemSerialRestController {

    private final ItemSerialService itemSerialService;

    @GetMapping
    public ResponseEntity<List<ItemSerial>> listar() {
        return ResponseEntity.ok(itemSerialService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemSerial> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(itemSerialService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ItemSerial> crear(@Valid @RequestBody ItemSerial itemSerial) {
        ItemSerial creado = itemSerialService.crear(itemSerial);
        return ResponseEntity.created(URI.create("/api/items-seriales/" + creado.getIdItemSerial())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemSerial> actualizar(@PathVariable Long id, @Valid @RequestBody ItemSerial itemSerial) {
        return ResponseEntity.ok(itemSerialService.actualizar(id, itemSerial));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        itemSerialService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}