package com.appWebsocket.controllers;

import com.appWebsocket.dtos.VehiculoRequestDTO;
import com.appWebsocket.entities.Vehiculo;
import com.appWebsocket.repositories.VehiculoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoRepository vehiculoRepository;

    public VehiculoController(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Vehiculo>> listarVehiculos() {
        return ResponseEntity.ok(vehiculoRepository.findByActivoTrue());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerVehiculo(@PathVariable Integer id) {
        return vehiculoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> crearVehiculo(@RequestBody VehiculoRequestDTO request) {
        try {
            if (request.getPlaca() == null || request.getPlaca().isBlank()) {
                return ResponseEntity.badRequest().body("La placa del vehículo es obligatoria.");
            }
            if (vehiculoRepository.existsByPlaca(request.getPlaca().trim().toUpperCase())) {
                return ResponseEntity.badRequest().body("Ya existe un vehículo registrado con la placa: " + request.getPlaca());
            }

            Vehiculo v = new Vehiculo();
            v.setPlaca(request.getPlaca().trim().toUpperCase());
            v.setMarca(request.getMarca() != null ? request.getMarca().trim() : "Genérico");
            v.setModelo(request.getModelo() != null ? request.getModelo().trim() : "Estándar");
            v.setTipoVehiculo(request.getTipoVehiculo() != null ? request.getTipoVehiculo() : "CAMION_FURGON");
            v.setCapacidadKg(request.getCapacidadKg() != null ? request.getCapacidadKg() : new BigDecimal("5000.00"));
            v.setKilometraje(request.getKilometraje() != null ? request.getKilometraje() : 0);
            v.setVencimientoSoat(request.getVencimientoSoat());
            v.setVencimientoRevision(request.getVencimientoRevision());
            v.setEstado(request.getEstado() != null ? request.getEstado() : "DISPONIBLE");
            v.setActivo(true);

            Vehiculo guardado = vehiculoRepository.save(v);
            return ResponseEntity.ok(guardado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizarVehiculo(@PathVariable Integer id, @RequestBody VehiculoRequestDTO request) {
        try {
            Vehiculo v = vehiculoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

            if (request.getMarca() != null) v.setMarca(request.getMarca().trim());
            if (request.getModelo() != null) v.setModelo(request.getModelo().trim());
            if (request.getTipoVehiculo() != null) v.setTipoVehiculo(request.getTipoVehiculo());
            if (request.getCapacidadKg() != null) v.setCapacidadKg(request.getCapacidadKg());
            if (request.getKilometraje() != null) v.setKilometraje(request.getKilometraje());
            if (request.getVencimientoSoat() != null) v.setVencimientoSoat(request.getVencimientoSoat());
            if (request.getVencimientoRevision() != null) v.setVencimientoRevision(request.getVencimientoRevision());
            if (request.getEstado() != null) v.setEstado(request.getEstado());

            Vehiculo actualizado = vehiculoRepository.save(v);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarVehiculo(@PathVariable Integer id) {
        try {
            Vehiculo v = vehiculoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

            // Baja lógica: preserva el historial de manifiestos y kárdex
            v.setActivo(false);
            v.setEstado("DADO_DE_BAJA");
            vehiculoRepository.save(v);

            return ResponseEntity.ok("Vehículo dado de baja lógicamente (Estado: DADO_DE_BAJA).");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
