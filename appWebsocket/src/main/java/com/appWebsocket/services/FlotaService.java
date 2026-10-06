package com.appWebsocket.services;

import com.appWebsocket.dtos.ManifiestoRequest;
import com.appWebsocket.entities.*;
import com.appWebsocket.repositories.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FlotaService {

    private final ManifiestoRepository manifiestoRepository;
    private final VehiculoRepository vehiculoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AgenciaRepository agenciaRepository;
    private final EnvioRepository envioRepository;
    private final HistorialEnvioService historialEnvioService;
    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    public FlotaService(ManifiestoRepository manifiestoRepository,
                        VehiculoRepository vehiculoRepository,
                        UsuarioRepository usuarioRepository,
                        AgenciaRepository agenciaRepository,
                        EnvioRepository envioRepository,
                        HistorialEnvioService historialEnvioService,
                        org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate) {
        this.manifiestoRepository = manifiestoRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.usuarioRepository = usuarioRepository;
        this.agenciaRepository = agenciaRepository;
        this.envioRepository = envioRepository;
        this.historialEnvioService = historialEnvioService;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public Manifiesto crearManifiesto(ManifiestoRequest request) {
        String dniDespachador = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario despachador = usuarioRepository.findByDni(dniDespachador)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        // 1. Bloqueo Pesimista del vehículo para garantizar cálculo de capacidad atómico sin colisión
        Vehiculo vehiculo = vehiculoRepository.findByIdWithLock(request.getIdVehiculo())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado o bloqueado por otra operación"));

        Usuario conductor = usuarioRepository.findById(request.getIdConductor())
                .orElseThrow(() -> new RuntimeException("Conductor no encontrado"));

        Agencia origen = despachador.getAgencia();
        if (origen == null && request.getIdAgenciaOrigen() != null) {
            origen = agenciaRepository.findById(request.getIdAgenciaOrigen())
                    .orElseThrow(() -> new RuntimeException("Agencia origen no existe"));
        } else if (origen == null) {
            throw new RuntimeException("Debe especificar una agencia de origen");
        }

        Agencia destino = agenciaRepository.findById(request.getIdAgenciaDestino())
                .orElseThrow(() -> new RuntimeException("Agencia destino no existe"));

        if (origen.getId().equals(destino.getId())) {
            throw new RuntimeException("La agencia de destino no puede ser igual a la agencia de origen");
        }

        if (request.getIdEnvios() == null || request.getIdEnvios().isEmpty()) {
            throw new RuntimeException("Debe seleccionar al menos un envío para armar el manifiesto");
        }

        // 2. Bloqueo Pesimista (SELECT ... FOR UPDATE) sobre los paquetes seleccionados
        // Previene condición de carrera (evita que dos despachadores suban el mismo paquete a dos camiones distintos)
        List<Envio> envios = envioRepository.findAllByIdInWithLock(request.getIdEnvios());

        for (Envio e : envios) {
            if (!"REGISTRADO".equalsIgnoreCase(e.getEstadoActual()) && !"EN_ESCALA".equalsIgnoreCase(e.getEstadoActual())) {
                throw new RuntimeException("El envío " + e.getCodigoTracking() + " no está disponible (Estado: " + e.getEstadoActual() + ")");
            }
        }

        // 2. Control de capacidad del camion (Evitar sobrecarga y camion vacio)
        BigDecimal pesoTotalCarga = envios.stream()
                .map(e -> e.getPesoReal().max(e.getPesoVolumetrico()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Validacion estricta de sobrecarga
        if (pesoTotalCarga.compareTo(vehiculo.getCapacidadKg()) > 0) {
            throw new RuntimeException("Capacidad excedida: El peso total de la carga (" + pesoTotalCarga 
                    + " kg) excede la capacidad máxima del vehículo (" + vehiculo.getCapacidadKg() + " kg)");
        }

        // Alerta de camion semivacio (< 15% de su capacidad)
        BigDecimal porcentajeOcupacion = vehiculo.getCapacidadKg().compareTo(BigDecimal.ZERO) > 0
                ? pesoTotalCarga.multiply(BigDecimal.valueOf(100)).divide(vehiculo.getCapacidadKg(), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        if (porcentajeOcupacion.compareTo(BigDecimal.valueOf(15.00)) < 0 && !request.getAutorizarCargaBaja()) {
            throw new RuntimeException("Alerta de baja ocupación: El vehículo solo llevará el " 
                    + porcentajeOcupacion + "% de su capacidad (" + pesoTotalCarga + " kg de " + vehiculo.getCapacidadKg() 
                    + " kg). Requiere confirmación para despachar con baja carga.");
        }

        // 3. Ensamblamos y guardamos el Manifiesto
        Manifiesto manifiesto = new Manifiesto();
        manifiesto.setCodigoManifiesto("MAN-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        manifiesto.setVehiculo(vehiculo);
        manifiesto.setConductor(conductor);
        manifiesto.setAgenciaOrigen(origen);
        manifiesto.setAgenciaDestino(destino);
        manifiesto.setEstado("EN RUTA");
        manifiesto.setFechaCreacion(LocalDateTime.now());
        manifiesto.setEnvios(envios);

        Manifiesto guardado = manifiestoRepository.save(manifiesto);

        // 4. Actualizamos el estado del vehículo
        vehiculo.setEstado("EN_RUTA");
        vehiculoRepository.save(vehiculo);

        // 5. Actualizamos cada paquete y registramos en el KARDEX / Historial
        for (Envio envio : envios) {
            envio.setEstadoActual("EN RUTA");
            envioRepository.save(envio);

            historialEnvioService.registrarEvento(
                    envio,
                    despachador,
                    "EN RUTA",
                    "Despachado en Manifiesto " + guardado.getCodigoManifiesto() 
                            + " hacia " + destino.getNombre() 
                            + " | Vehículo: " + vehiculo.getPlaca() + " (" + vehiculo.getMarca() + ")"
                            + " | Conductor: DNI " + conductor.getDni()
            );
        }

        try {
            java.util.Map<String, Object> ws = new java.util.HashMap<>();
            ws.put("tipo", "MANIFIESTO_CREADO");
            ws.put("idManifiesto", guardado.getId());
            ws.put("codigoManifiesto", guardado.getCodigoManifiesto());
            messagingTemplate.convertAndSend("/topic/envios", (Object) ws);
        } catch (Exception ignored) {}

        return guardado;
    }

    public List<Manifiesto> listarManifiestos() {
        return manifiestoRepository.findAllByOrderByIdDesc();
    }

    public List<Vehiculo> obtenerVehiculosDisponibles() {
        return vehiculoRepository.findByEstadoAndActivoTrue("DISPONIBLE");
    }

    public List<Vehiculo> obtenerTodosLosVehiculos() {
        return vehiculoRepository.findByActivoTrue();
    }

    public List<Usuario> obtenerConductoresDisponibles() {
        List<Usuario> conductores = usuarioRepository.findByRol_Nombre("ROLE_CONDUCTOR");
        if (conductores.isEmpty()) {
            conductores = usuarioRepository.findByRol_Nombre("ROLE_REPARTIDOR");
        } else {
            conductores.addAll(usuarioRepository.findByRol_Nombre("ROLE_REPARTIDOR"));
        }
        return conductores.stream().filter(u -> Boolean.TRUE.equals(u.getActivo())).toList();
    }

    public List<Agencia> obtenerAgencias() {
        return agenciaRepository.findByActivoTrue();
    }

    @Transactional
    public Manifiesto recepcionarManifiesto(Integer idManifiesto) {
        String dniReceptor = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario receptor = usuarioRepository.findByDni(dniReceptor)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        Manifiesto manifiesto = manifiestoRepository.findById(idManifiesto)
                .orElseThrow(() -> new RuntimeException("Manifiesto no encontrado"));

        manifiesto.setEstado("FINALIZADO");
        manifiestoRepository.save(manifiesto);

        // Liberamos el vehículo
        Vehiculo vehiculo = manifiesto.getVehiculo();
        if (vehiculo != null) {
            vehiculo.setEstado("DISPONIBLE");
            vehiculoRepository.save(vehiculo);
        }

        // Cada paquete pasa a DISPONIBLE EN DESTINO
        if (manifiesto.getEnvios() != null) {
            for (Envio envio : manifiesto.getEnvios()) {
                envio.setEstadoActual("EN AGENCIA DESTINO");
                envioRepository.save(envio);

                historialEnvioService.registrarEvento(
                        envio,
                        receptor,
                        "EN AGENCIA DESTINO",
                        "Recepcionado en " + manifiesto.getAgenciaDestino().getNombre() + " listo para entrega"
                );
            }
        }

        try {
            java.util.Map<String, Object> ws = new java.util.HashMap<>();
            ws.put("tipo", "MANIFIESTO_RECEPCIONADO");
            ws.put("idManifiesto", manifiesto.getId());
            messagingTemplate.convertAndSend("/topic/envios", (Object) ws);
        } catch (Exception ignored) {}

        return manifiesto;
    }
}