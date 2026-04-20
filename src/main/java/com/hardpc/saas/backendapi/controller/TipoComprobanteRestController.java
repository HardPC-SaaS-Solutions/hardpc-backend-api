package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.entity.TipoComprobante;
import com.hardpc.saas.backendapi.service.TipoComprobanteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tipos-comprobante")
@RequiredArgsConstructor
public class TipoComprobanteRestController {

    private final TipoComprobanteService tipoComprobanteService;

    @GetMapping
    public ResponseEntity<List<TipoComprobante>> listar(){
        return ResponseEntity.ok(tipoComprobanteService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoComprobante> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(tipoComprobanteService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<TipoComprobante> crear(@RequestBody TipoComprobante tipoComprobante){
        TipoComprobante creada = tipoComprobanteService.crear(tipoComprobante);
        return ResponseEntity.created(URI.create("/api/tipos-comprobante/" + creada.getIdTipoComprobante())).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoComprobante> actualizar(@PathVariable Long id, @RequestBody TipoComprobante tipoComprobante){
        return ResponseEntity.ok(tipoComprobanteService.actualizar(id,tipoComprobante));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        tipoComprobanteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
