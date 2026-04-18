package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.Local;
import com.hardpc.saas.backendapi.service.LocalService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/local")
public class LocalController {

    private final LocalService service;

    public LocalController(LocalService service) {
        this.service = service;
    }

    @GetMapping
    public List<Local> listar() {
        return service.listar();
    }

    @PostMapping
    public Local crear(@RequestBody Local local) {
        return service.guardar(local);
    }

    @GetMapping("/{id}")
    public Local buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}