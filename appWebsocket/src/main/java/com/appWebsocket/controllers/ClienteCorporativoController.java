package com.appWebsocket.controllers;

import com.appWebsocket.entities.ClienteCorporativo;
import com.appWebsocket.repositories.ClienteCorporativoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes-corporativos")
public class ClienteCorporativoController {

    private final ClienteCorporativoRepository repository;

    public ClienteCorporativoController(ClienteCorporativoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<ClienteCorporativo>> listar() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{ruc}")
    public ResponseEntity<?> buscarPorRuc(@PathVariable String ruc) {
        return repository.findByRuc(ruc)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> crear(@RequestBody ClienteCorporativo cliente) {
        try {
            if (repository.existsById(cliente.getRuc())) {
                return ResponseEntity.badRequest().body("El cliente corporativo con este RUC ya existe.");
            }
            return ResponseEntity.ok(repository.save(cliente));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{ruc}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizar(@PathVariable String ruc, @RequestBody ClienteCorporativo datos) {
        try {
            ClienteCorporativo cliente = repository.findById(ruc)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con RUC: " + ruc));

            cliente.setRazonSocial(datos.getRazonSocial());
            cliente.setEmailContacto(datos.getEmailContacto());
            cliente.setTelefonoContacto(datos.getTelefonoContacto());
            cliente.setRepresentanteLegal(datos.getRepresentanteLegal());
            cliente.setDireccionFiscal(datos.getDireccionFiscal());
            cliente.setLineaCreditoMaxima(datos.getLineaCreditoMaxima());
            cliente.setDiaFacturacion(datos.getDiaFacturacion());

            return ResponseEntity.ok(repository.save(cliente));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
