package com.appWebsocket.controllers;

import com.appWebsocket.entities.Rol;
import com.appWebsocket.repositories.RolRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolRepository rolRepository;

    public RolController(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @GetMapping
    public ResponseEntity<List<Rol>> listarRoles() {
        return ResponseEntity.ok(rolRepository.findAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> crearRol(@RequestBody Map<String, String> body) {
        try {
            String nombre = body.get("nombre");
            if (nombre == null || nombre.isBlank()) {
                return ResponseEntity.badRequest().body("El nombre del rol es requerido.");
            }
            nombre = nombre.trim().toUpperCase();
            if (!nombre.startsWith("ROLE_")) {
                nombre = "ROLE_" + nombre;
            }

            if (rolRepository.findByNombre(nombre).isPresent()) {
                return ResponseEntity.badRequest().body("El rol " + nombre + " ya existe.");
            }

            Rol rol = new Rol();
            rol.setNombre(nombre);
            Rol guardado = rolRepository.save(rol);

            return ResponseEntity.ok(guardado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizarRol(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        try {
            Rol rol = rolRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

            String nuevoNombre = body.get("nombre");
            if (nuevoNombre == null || nuevoNombre.isBlank()) {
                return ResponseEntity.badRequest().body("El nombre del rol es requerido.");
            }
            nuevoNombre = nuevoNombre.trim().toUpperCase();
            if (!nuevoNombre.startsWith("ROLE_")) {
                nuevoNombre = "ROLE_" + nuevoNombre;
            }

            // Evitar duplicados
            if (!rol.getNombre().equals(nuevoNombre) && rolRepository.findByNombre(nuevoNombre).isPresent()) {
                return ResponseEntity.badRequest().body("Ya existe otro rol con el nombre: " + nuevoNombre);
            }

            rol.setNombre(nuevoNombre);
            Rol actualizado = rolRepository.save(rol);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarRol(@PathVariable Integer id) {
        try {
            Rol rol = rolRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

            // Proteger roles fundamentales del sistema
            if ("ROLE_ADMIN".equals(rol.getNombre()) || "ROLE_OPERARIO".equals(rol.getNombre()) || "ROLE_REPARTIDOR".equals(rol.getNombre())) {
                return ResponseEntity.badRequest().body("No se puede eliminar el rol fundamental del sistema: " + rol.getNombre());
            }

            rolRepository.delete(rol);
            return ResponseEntity.ok("Rol eliminado exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

