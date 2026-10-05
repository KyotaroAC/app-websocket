package com.appWebsocket.controllers;

import com.appWebsocket.dtos.ManifiestoRequest;
import com.appWebsocket.entities.Manifiesto;
import com.appWebsocket.services.FlotaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flota")
public class FlotaController {

    private final FlotaService flotaService;

    public FlotaController(FlotaService flotaService) {
        this.flotaService = flotaService;
    }

    @PostMapping("/manifiestos/crear")
    public ResponseEntity<?> crearManifiesto(@RequestBody ManifiestoRequest request) {
        try {
            Manifiesto nuevoManifiesto = flotaService.crearManifiesto(request);
            return ResponseEntity.ok(nuevoManifiesto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/manifiestos")
    public ResponseEntity<?> listarManifiestos() {
        try {
            return ResponseEntity.ok(flotaService.listarManifiestos());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/vehiculos")
    public ResponseEntity<?> obtenerVehiculos() {
        try {
            return ResponseEntity.ok(flotaService.obtenerTodosLosVehiculos());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/vehiculos/disponibles")
    public ResponseEntity<?> obtenerVehiculosDisponibles() {
        try {
            return ResponseEntity.ok(flotaService.obtenerVehiculosDisponibles());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/conductores")
    public ResponseEntity<?> obtenerConductores() {
        try {
            return ResponseEntity.ok(flotaService.obtenerConductoresDisponibles());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/agencias")
    public ResponseEntity<?> obtenerAgencias() {
        try {
            return ResponseEntity.ok(flotaService.obtenerAgencias());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/manifiestos/{id}/recepcionar")
    public ResponseEntity<?> recepcionarManifiesto(@PathVariable Integer id) {
        try {
            Manifiesto manifiesto = flotaService.recepcionarManifiesto(id);
            return ResponseEntity.ok(manifiesto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}