package com.appWebsocket.controllers;

import com.appWebsocket.dtos.AgenciaRequestDTO;
import com.appWebsocket.entities.Agencia;
import com.appWebsocket.repositories.AgenciaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/agencias")
public class AgenciaController {

    private final AgenciaRepository agenciaRepository;

    public AgenciaController(AgenciaRepository agenciaRepository) {
        this.agenciaRepository = agenciaRepository;
    }

    @GetMapping
    public ResponseEntity<List<Agencia>> listarAgencias() {
        return ResponseEntity.ok(agenciaRepository.findByActivoTrue());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerAgencia(@PathVariable Integer id) {
        return agenciaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> crearAgencia(@RequestBody AgenciaRequestDTO request) {
        try {
            if (request.getNombre() == null || request.getNombre().isBlank()) {
                return ResponseEntity.badRequest().body("El nombre de la agencia es obligatorio.");
            }
            if (request.getDireccion() == null || request.getDireccion().isBlank()) {
                return ResponseEntity.badRequest().body("La dirección de la agencia es obligatoria.");
            }

            Agencia ag = new Agencia();
            ag.setNombre(request.getNombre().trim());
            ag.setDireccion(request.getDireccion().trim());
            ag.setCiudad(request.getCiudad() != null ? request.getCiudad().trim() : "Perú");
            ag.setTelefono(request.getTelefono());
            ag.setEmail(request.getEmail());
            ag.setLatitud(request.getLatitud() != null ? request.getLatitud() : new BigDecimal("-12.046374"));
            ag.setLongitud(request.getLongitud() != null ? request.getLongitud() : new BigDecimal("-77.042793"));
            ag.setCapacidadM3(request.getCapacidadM3() != null ? request.getCapacidadM3() : new BigDecimal("80.00"));
            ag.setEstado(request.getEstado() != null ? request.getEstado() : "ACTIVO");
            ag.setActivo(true);

            Agencia guardada = agenciaRepository.save(ag);
            return ResponseEntity.ok(guardada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizarAgencia(@PathVariable Integer id, @RequestBody AgenciaRequestDTO request) {
        try {
            Agencia ag = agenciaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Agencia no encontrada"));

            if (request.getNombre() != null) ag.setNombre(request.getNombre().trim());
            if (request.getDireccion() != null) ag.setDireccion(request.getDireccion().trim());
            if (request.getCiudad() != null) ag.setCiudad(request.getCiudad().trim());
            if (request.getTelefono() != null) ag.setTelefono(request.getTelefono());
            if (request.getEmail() != null) ag.setEmail(request.getEmail());
            if (request.getLatitud() != null) ag.setLatitud(request.getLatitud());
            if (request.getLongitud() != null) ag.setLongitud(request.getLongitud());
            if (request.getCapacidadM3() != null) ag.setCapacidadM3(request.getCapacidadM3());
            if (request.getEstado() != null) ag.setEstado(request.getEstado());

            Agencia actualizada = agenciaRepository.save(ag);
            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarAgencia(@PathVariable Integer id) {
        try {
            Agencia ag = agenciaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Agencia no encontrada"));

            // Eliminación Lógica (Soft Delete)
            ag.setActivo(false);
            ag.setEstado("CERRADA");
            agenciaRepository.save(ag);

            return ResponseEntity.ok("Agencia dada de baja lógicamente (Estado: CERRADA).");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
