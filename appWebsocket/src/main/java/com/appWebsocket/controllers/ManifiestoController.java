package com.appWebsocket.controllers;

import com.appWebsocket.dtos.ManifiestoRequest;
import com.appWebsocket.entities.Manifiesto;
import com.appWebsocket.services.FlotaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manifiestos")
public class ManifiestoController {

    private final FlotaService flotaService;

    public ManifiestoController(FlotaService flotaService) {
        this.flotaService = flotaService;
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearManifiesto(@RequestBody ManifiestoRequest request) {
        try {
            Manifiesto nuevoManifiesto = flotaService.crearManifiesto(request);
            return ResponseEntity.ok(nuevoManifiesto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            return ResponseEntity.ok(flotaService.listarManifiestos());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}