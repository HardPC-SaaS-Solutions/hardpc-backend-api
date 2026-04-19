package com.hardpc.saas.backendapi.controller.transaccional;


import com.hardpc.saas.backendapi.entity.TipoDocumento;
import com.hardpc.saas.backendapi.service.transaccional.TipoDocumentoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-documento")
public class TipoDocumentoController {

    private final TipoDocumentoService tipoDocumento;

    public TipoDocumentoController(TipoDocumentoService tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    @PostMapping
    public ResponseEntity<TipoDocumento> guardar(@RequestBody @Valid TipoDocumento tipo) {
        return ResponseEntity.ok(tipoDocumento.guardarTipo(tipo));
    }

    @GetMapping
    public ResponseEntity<List<TipoDocumento>> listar() {
        return ResponseEntity.ok(tipoDocumento.listarTipoDocumentos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoDocumento> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tipoDocumento.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoDocumento> actualizar(@PathVariable Long id,
                                                    @Valid @RequestBody TipoDocumento tipo) {
        return ResponseEntity.ok(tipoDocumento.actualizarTipoDocumento(id, tipo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tipoDocumento.eliminarTipoDocumento(id);
        return ResponseEntity.noContent().build();
    }

}
