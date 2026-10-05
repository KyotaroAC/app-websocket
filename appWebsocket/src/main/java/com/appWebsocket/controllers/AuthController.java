package com.appWebsocket.controllers;

import com.appWebsocket.dtos.AuthRequest;
import com.appWebsocket.dtos.AuthResponse;
import com.appWebsocket.dtos.CambioPasswordRequest;
import com.appWebsocket.dtos.RegistroRequest;
import com.appWebsocket.entities.Agencia;
import com.appWebsocket.entities.Rol;
import com.appWebsocket.entities.Usuario;
import com.appWebsocket.repositories.AgenciaRepository;
import com.appWebsocket.repositories.RolRepository;
import com.appWebsocket.repositories.UsuarioRepository;
import com.appWebsocket.security.JwtProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UsuarioRepository usuarioRepository;
    private final AgenciaRepository agenciaRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtProvider jwtProvider,
                          UsuarioRepository usuarioRepository, AgenciaRepository agenciaRepository,
                          RolRepository rolRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.usuarioRepository = usuarioRepository;
        this.agenciaRepository = agenciaRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getDni(), request.getPassword())
            );

            Usuario usuario = usuarioRepository.findByDni(request.getDni())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            String token = jwtProvider.generateToken(usuario);

            AuthResponse response = new AuthResponse();
            response.setToken(token);

            if (usuario.getRol() != null) {
                response.setRol(usuario.getRol().getNombre());
            }

            if (usuario.getAgencia() != null) {
                response.setIdAgencia(usuario.getAgencia().getId());
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody RegistroRequest request) {
        try {
            if (usuarioRepository.findByDni(request.getDni()).isPresent()) {
                return ResponseEntity.badRequest().body("Error: El DNI ya está registrado en el sistema.");
            }

            Agencia agencia = null;
            if (request.getIdAgencia() != null) {
                agencia = agenciaRepository.findById(request.getIdAgencia())
                        .orElseThrow(() -> new RuntimeException("Error: La agencia indicada no existe."));
            }

            Rol rol = rolRepository.findByNombre(request.getRol())
                    .orElseThrow(() -> new RuntimeException("Error: El rol indicado (" + request.getRol() + ") no existe."));

            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setDni(request.getDni());
            nuevoUsuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            nuevoUsuario.setRol(rol);
            nuevoUsuario.setAgencia(agencia);

            usuarioRepository.save(nuevoUsuario);

            Map<String, Object> resp = new HashMap<>();
            resp.put("mensaje", "Usuario registrado exitosamente");
            resp.put("dni", nuevoUsuario.getDni());
            resp.put("rol", rol.getNombre());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Ruta de Oficina / Admin: Genera un token temporal para el empleado que olvidó su clave
    @PostMapping("/generar-token-password")
    public ResponseEntity<?> generarTokenPassword(@RequestBody Map<String, String> body) {
        try {
            String dniEmpleado = body.get("dniEmpleado");
            if (dniEmpleado == null || dniEmpleado.isBlank()) {
                return ResponseEntity.badRequest().body("Debe proporcionar el DNI del empleado.");
            }

            Usuario empleado = usuarioRepository.findByDni(dniEmpleado)
                    .orElseThrow(() -> new RuntimeException("Empleado con DNI " + dniEmpleado + " no encontrado."));

            // Genera un código numérico seguro de 6 dígitos
            String token = String.format("%06d", new Random().nextInt(999999));

            empleado.setTokenRecuperacion(token);
            empleado.setExpiracionToken(LocalDateTime.now().plusHours(2)); // Válido por 2 horas
            usuarioRepository.save(empleado);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("dni", dniEmpleado);
            respuesta.put("token", token);
            respuesta.put("expiracion", empleado.getExpiracionToken().toString());
            respuesta.put("mensaje", "Token de recuperación generado con éxito por la oficina.");

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Ruta pública para canjear el token de oficina por la nueva contraseña
    @PostMapping("/cambiar-password")
    public ResponseEntity<?> cambiarPassword(@RequestBody CambioPasswordRequest request) {
        try {
            Usuario empleado = usuarioRepository.findByDni(request.getDni())
                    .orElseThrow(() -> new RuntimeException("Usuario con DNI " + request.getDni() + " no encontrado."));

            if (empleado.getTokenRecuperacion() == null || !empleado.getTokenRecuperacion().equalsIgnoreCase(request.getToken().trim())) {
                return ResponseEntity.badRequest().body("Token inválido o incorrecto.");
            }

            if (empleado.getExpiracionToken() == null || empleado.getExpiracionToken().isBefore(LocalDateTime.now())) {
                return ResponseEntity.badRequest().body("El token ha expirado. Solicite uno nuevo a la administración.");
            }

            empleado.setPasswordHash(passwordEncoder.encode(request.getNuevaPassword()));
            empleado.setTokenRecuperacion(null);
            empleado.setExpiracionToken(null);
            usuarioRepository.save(empleado);

            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Contraseña actualizada exitosamente. Ya puede iniciar sesión con su nueva clave.");
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}