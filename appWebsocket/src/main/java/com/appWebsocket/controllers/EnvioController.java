package com.appWebsocket.controllers;

import com.appWebsocket.dtos.EnvioRequest;
import com.appWebsocket.dtos.TrackingDetalleDTO;
import com.appWebsocket.entities.Envio;
import com.appWebsocket.services.EnvioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/envios")
public class EnvioController {

    private final EnvioService envioService;

    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarEnvio(@RequestBody EnvioRequest request) {
        try {
            Envio nuevoEnvio = envioService.registrarEnvio(request);
            return ResponseEntity.ok(nuevoEnvio);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/pendientes")
    public ResponseEntity<?> obtenerPendientes(@RequestParam(required = false) Integer idAgencia) {
        try {
            List<Envio> pendientes = envioService.obtenerPendientesPorAgencia(idAgencia);
            return ResponseEntity.ok(pendientes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/tracking/{codigoTracking}")
    public ResponseEntity<?> consultarTracking(@PathVariable String codigoTracking) {
        try {
            TrackingDetalleDTO detalle = envioService.obtenerDetalleTracking(codigoTracking);
            return ResponseEntity.ok(detalle);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/todos")
    public ResponseEntity<?> obtenerTodos() {
        try {
            return ResponseEntity.ok(envioService.obtenerTodos());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/buscar-dni/{dni}")
    public ResponseEntity<?> buscarPorDni(@PathVariable String dni) {
        try {
            return ResponseEntity.ok(envioService.buscarPorDni(dni));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{codigoTracking}/estado")
    public ResponseEntity<?> actualizarEstado(
            @PathVariable String codigoTracking,
            @RequestBody Map<String, String> body) {
        try {
            String nuevoEstado = body.get("nuevoEstado");
            String observacion = body.get("observacion");
            Envio actualizado = envioService.actualizarEstado(codigoTracking, nuevoEstado, observacion);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}