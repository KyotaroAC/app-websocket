package com.appWebsocket.controllers;

import com.appWebsocket.dtos.PlanillaAgenciaDTO;
import com.appWebsocket.dtos.RegistroRequest;
import com.appWebsocket.dtos.UsuarioResponseDTO;
import com.appWebsocket.entities.Agencia;
import com.appWebsocket.entities.Rol;
import com.appWebsocket.entities.Usuario;
import com.appWebsocket.repositories.AgenciaRepository;
import com.appWebsocket.repositories.RolRepository;
import com.appWebsocket.repositories.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final AgenciaRepository agenciaRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository usuarioRepository,
                             AgenciaRepository agenciaRepository,
                             RolRepository rolRepository,
                             PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.agenciaRepository = agenciaRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private UsuarioResponseDTO mapToDTO(Usuario u) {
        return new UsuarioResponseDTO(
                u.getId(),
                u.getDni(),
                u.getNombres(),
                u.getApellidos(),
                u.getEmail(),
                u.getTelefono(),
                u.getSueldoBase(),
                u.getFechaIngreso(),
                u.getFechaNacimiento(),
                u.getEdad(),
                u.getActivo(),
                u.getEstadoEmpleado(),
                u.getRol() != null ? u.getRol().getNombre() : "SIN_ROL",
                u.getAgencia() != null ? u.getAgencia().getNombre() : "Sede Central",
                u.getAgencia() != null ? u.getAgencia().getId() : null
        );
    }

    // Listar todos los usuarios activos con información completa (exclusivo ADMIN)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listarUsuarios() {
        List<UsuarioResponseDTO> usuarios = usuarioRepository.findByActivoTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(usuarios);
    }

    // MÓDULO EXCLUSIVO ADMIN: Reporte de Planilla por Agencias
    @GetMapping("/planilla")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PlanillaAgenciaDTO>> obtenerPlanillaPorAgencias() {
        List<Usuario> todosLosUsuarios = usuarioRepository.findAll();
        List<Agencia> todasLasAgencias = agenciaRepository.findAll();

        List<PlanillaAgenciaDTO> reporte = new ArrayList<>();

        // 1. Agrupar por cada agencia registrada
        for (Agencia ag : todasLasAgencias) {
            List<Usuario> empleadosAgencia = todosLosUsuarios.stream()
                    .filter(u -> u.getAgencia() != null && u.getAgencia().getId().equals(ag.getId()))
                    .collect(Collectors.toList());

            BigDecimal gastoTotal = empleadosAgencia.stream()
                    .map(u -> u.getSueldoBase() != null ? u.getSueldoBase() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            int totalEmp = empleadosAgencia.size();
            BigDecimal promedio = totalEmp > 0
                    ? gastoTotal.divide(BigDecimal.valueOf(totalEmp), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            reporte.add(new PlanillaAgenciaDTO(
                    ag.getId(),
                    ag.getNombre(),
                    ag.getCiudad() != null ? ag.getCiudad() : "Perú",
                    totalEmp,
                    gastoTotal,
                    promedio,
                    empleadosAgencia.stream().map(this::mapToDTO).collect(Collectors.toList())
            ));
        }

        // 2. Grupo especial para Sede Central / Administradores (sin agencia asignada)
        List<Usuario> empleadosCentral = todosLosUsuarios.stream()
                .filter(u -> u.getAgencia() == null)
                .collect(Collectors.toList());

        if (!empleadosCentral.isEmpty()) {
            BigDecimal gastoCentral = empleadosCentral.stream()
                    .map(u -> u.getSueldoBase() != null ? u.getSueldoBase() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            int totalCentral = empleadosCentral.size();
            BigDecimal promedioCentral = gastoCentral.divide(BigDecimal.valueOf(totalCentral), 2, RoundingMode.HALF_UP);

            reporte.add(0, new PlanillaAgenciaDTO(
                    0,
                    "Sede Central / Dirección General",
                    "Lima",
                    totalCentral,
                    gastoCentral,
                    promedioCentral,
                    empleadosCentral.stream().map(this::mapToDTO).collect(Collectors.toList())
            ));
        }

        return ResponseEntity.ok(reporte);
    }

    // Registrar nuevo empleado con datos de contacto, fecha de nacimiento y validación de mayoría de edad
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> crearUsuario(@RequestBody RegistroRequest request) {
        try {
            if (request.getDni() == null || !request.getDni().matches("\\d{8}")) {
                return ResponseEntity.badRequest().body("Error de validación: El DNI debe contener exactamente 8 dígitos numéricos.");
            }

            if (usuarioRepository.existsByDni(request.getDni())) {
                return ResponseEntity.badRequest().body("Error: El DNI " + request.getDni() + " ya se encuentra registrado.");
            }

            if (request.getFechaNacimiento() != null) {
                int edad = java.time.Period.between(request.getFechaNacimiento(), LocalDate.now()).getYears();
                if (edad < 18) {
                    return ResponseEntity.badRequest().body("Error laboral: El colaborador debe ser mayor de edad (Edad calculada: " + edad + " años).");
                }
            }

            Agencia agencia = null;
            if (request.getIdAgencia() != null) {
                agencia = agenciaRepository.findById(request.getIdAgencia())
                        .orElseThrow(() -> new RuntimeException("Agencia no encontrada"));
            }

            Rol rol = rolRepository.findByNombre(request.getRol())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + request.getRol()));

            Usuario usuario = new Usuario();
            usuario.setDni(request.getDni());
            usuario.setNombres(request.getNombres());
            usuario.setApellidos(request.getApellidos());
            usuario.setFechaNacimiento(request.getFechaNacimiento());
            usuario.setEmail(request.getEmail());
            usuario.setTelefono(request.getTelefono());
            usuario.setDireccion(request.getDireccion());
            usuario.setSueldoBase(request.getSueldoBase());
            usuario.setFechaIngreso(LocalDate.now());
            usuario.setEstadoEmpleado("ACTIVO");
            usuario.setActivo(true);
            usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            usuario.setRol(rol);
            usuario.setAgencia(agencia);

            usuarioRepository.save(usuario);
            return ResponseEntity.ok("Empleado creado exitosamente con datos de planilla");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Actualizar datos personales, rol, agencia o sueldo de un empleado (exclusivo ADMIN)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (body.containsKey("nombres")) usuario.setNombres((String) body.get("nombres"));
            if (body.containsKey("apellidos")) usuario.setApellidos((String) body.get("apellidos"));
            if (body.containsKey("email")) usuario.setEmail((String) body.get("email"));
            if (body.containsKey("telefono")) usuario.setTelefono((String) body.get("telefono"));
            if (body.containsKey("direccion")) usuario.setDireccion((String) body.get("direccion"));
            if (body.containsKey("estadoEmpleado")) usuario.setEstadoEmpleado((String) body.get("estadoEmpleado"));

            if (body.containsKey("sueldoBase")) {
                usuario.setSueldoBase(new BigDecimal(body.get("sueldoBase").toString()));
            }

            if (body.containsKey("rol")) {
                String rolNombre = (String) body.get("rol");
                Rol rol = rolRepository.findByNombre(rolNombre)
                        .orElseThrow(() -> new RuntimeException("Rol no válido"));
                usuario.setRol(rol);
            }

            if (body.containsKey("idAgencia")) {
                Object agId = body.get("idAgencia");
                if (agId != null && !agId.toString().isBlank()) {
                    Integer idAgencia = Integer.parseInt(agId.toString());
                    Agencia agencia = agenciaRepository.findById(idAgencia)
                            .orElseThrow(() -> new RuntimeException("Agencia no encontrada"));
                    usuario.setAgencia(agencia);
                } else {
                    usuario.setAgencia(null);
                }
            }

            usuarioRepository.save(usuario);
            return ResponseEntity.ok("Empleado actualizado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Eliminación Lógica (Soft Delete) de empleado (exclusivo ADMIN, no puede eliminarse a sí mismo)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Integer id) {
        try {
            String dniActual = SecurityContextHolder.getContext().getAuthentication().getName();
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (usuario.getDni().equals(dniActual)) {
                return ResponseEntity.badRequest().body("No puede dar de baja su propia cuenta de administrador.");
            }

            // Baja lógica: preserva el historial, kárdex y nóminas históricas
            usuario.setActivo(false);
            usuario.setEstadoEmpleado("CESADO");
            usuarioRepository.save(usuario);

            return ResponseEntity.ok("Colaborador dado de baja lógicamente (Estado: CESADO). Registros históricos preservados.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Cambio de contraseña para el usuario actualmente autenticado desde el dashboard
    @PutMapping("/cambiar-password-sesion")
    public ResponseEntity<?> cambiarPasswordSesion(@RequestBody Map<String, String> body) {
        try {
            String dni = SecurityContextHolder.getContext().getAuthentication().getName();
            Usuario usuario = usuarioRepository.findByDni(dni)
                    .orElseThrow(() -> new RuntimeException("Usuario en sesión no encontrado."));

            String passwordActual = body.get("passwordActual");
            String passwordNueva = body.get("passwordNueva");

            if (passwordActual == null || passwordActual.isBlank() || passwordNueva == null || passwordNueva.isBlank()) {
                return ResponseEntity.badRequest().body("Debe ingresar la contraseña actual y la nueva contraseña.");
            }

            if (passwordNueva.trim().length() < 6) {
                return ResponseEntity.badRequest().body("La nueva contraseña debe tener al menos 6 caracteres.");
            }

            if (!passwordEncoder.matches(passwordActual, usuario.getPasswordHash())) {
                return ResponseEntity.badRequest().body("La contraseña actual es incorrecta.");
            }

            usuario.setPasswordHash(passwordEncoder.encode(passwordNueva.trim()));
            usuarioRepository.save(usuario);

            return ResponseEntity.ok("Contraseña actualizada exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
