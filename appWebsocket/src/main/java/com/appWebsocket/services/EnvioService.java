package com.appWebsocket.services;

import com.appWebsocket.dtos.EnvioRequest;
import com.appWebsocket.dtos.TrackingDetalleDTO;
import com.appWebsocket.entities.Agencia;
import com.appWebsocket.entities.Envio;
import com.appWebsocket.entities.HistorialEnvio;
import com.appWebsocket.entities.Usuario;
import com.appWebsocket.repositories.AgenciaRepository;
import com.appWebsocket.repositories.EnvioRepository;
import com.appWebsocket.repositories.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class EnvioService {

    private final EnvioRepository envioRepository;
    private final AgenciaRepository agenciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialEnvioService historialEnvioService;

    private final BigDecimal TARIFA_POR_KILO = new BigDecimal("15.00");

    public EnvioService(EnvioRepository envioRepository,
                        AgenciaRepository agenciaRepository,
                        UsuarioRepository usuarioRepository,
                        HistorialEnvioService historialEnvioService) {
        this.envioRepository = envioRepository;
        this.agenciaRepository = agenciaRepository;
        this.usuarioRepository = usuarioRepository;
        this.historialEnvioService = historialEnvioService;
    }

    @Transactional
    public Envio registrarEnvio(EnvioRequest request) {
        String dniEmpleado = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario empleado = usuarioRepository.findByDni(dniEmpleado)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        Agencia origen = empleado.getAgencia();
        if (origen == null) {
            if (request.getIdAgenciaOrigen() != null) {
                origen = agenciaRepository.findById(request.getIdAgenciaOrigen())
                        .orElseThrow(() -> new RuntimeException("Agencia de origen no existe"));
            } else {
                throw new RuntimeException("Es necesario especificar una agencia de origen para este usuario.");
            }
        }

        Agencia destino = agenciaRepository.findById(request.getIdAgenciaDestino())
                .orElseThrow(() -> new RuntimeException("Agencia de destino no existe"));

        BigDecimal volumen = request.getLargo().multiply(request.getAncho()).multiply(request.getAlto());
        BigDecimal pesoVolumetrico = volumen.divide(new BigDecimal("5000"), 2, RoundingMode.HALF_UP);
        BigDecimal pesoFacturable = request.getPesoReal().max(pesoVolumetrico);
        BigDecimal costoTotal = pesoFacturable.multiply(TARIFA_POR_KILO);

        String estadoPago = "PENDIENTE";
        if ("PAGO_ORIGEN".equalsIgnoreCase(request.getModalidadPago())) {
            estadoPago = "PAGADO";
        } else if ("CUENTA_CORRIENTE".equalsIgnoreCase(request.getModalidadPago())) {
            estadoPago = "FACTURADO";
        }

        Envio envio = new Envio();
        envio.setCodigoTracking("TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        envio.setRemitenteDni(request.getRemitenteDni());
        envio.setDestinatarioDni(request.getDestinatarioDni());
        envio.setPesoReal(request.getPesoReal());
        envio.setPesoVolumetrico(pesoVolumetrico);
        envio.setCostoTotal(costoTotal);
        envio.setAgenciaOrigen(origen);
        envio.setAgenciaDestino(destino);
        envio.setModalidadPago(request.getModalidadPago());
        envio.setMetodoPago(request.getMetodoPago());
        envio.setEstadoPago(estadoPago);
        envio.setEstadoActual("REGISTRADO");

        Envio guardado = envioRepository.save(envio);

        // Registro de auditoria inmediata en KARDEX / Historial
        historialEnvioService.registrarEvento(
                guardado,
                empleado,
                "REGISTRADO",
                "Recepción y pesaje en " + origen.getNombre()
        );

        return guardado;
    }

    public List<Envio> obtenerPendientesPorAgencia(Integer idAgenciaFiltro) {
        String dniEmpleado = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario empleado = usuarioRepository.findByDni(dniEmpleado)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Agencia origen = empleado.getAgencia();
        if (origen == null) {
            origen = agenciaRepository.findById(idAgenciaFiltro)
                    .orElseThrow(() -> new RuntimeException("Debe indicar una agencia para buscar pendientes."));
        }

        return envioRepository.findByAgenciaOrigenAndEstadoActual(origen, "REGISTRADO");
    }

    public TrackingDetalleDTO obtenerDetalleTracking(String codigoTracking) {
        Envio envio = envioRepository.findByCodigoTracking(codigoTracking)
                .orElseThrow(() -> new RuntimeException("No se encontró ningún paquete con el código: " + codigoTracking));
        List<HistorialEnvio> historial = historialEnvioService.obtenerHistorialPorEnvio(envio.getId());
        return new TrackingDetalleDTO(envio, historial);
    }

    public List<Envio> obtenerTodos() {
        return envioRepository.findAllByOrderByIdDesc();
    }

    public List<Envio> buscarPorDni(String dni) {
        return envioRepository.findByRemitenteDniOrDestinatarioDni(dni, dni);
    }

    @Transactional
    public Envio actualizarEstado(String codigoTracking, String nuevoEstado, String observacion) {
        String dniEmpleado = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario empleado = usuarioRepository.findByDni(dniEmpleado)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        Envio envio = envioRepository.findByCodigoTracking(codigoTracking)
                .orElseThrow(() -> new RuntimeException("Envío no encontrado: " + codigoTracking));

        envio.setEstadoActual(nuevoEstado);
        if ("ENTREGADO".equalsIgnoreCase(nuevoEstado) && "PENDIENTE".equalsIgnoreCase(envio.getEstadoPago())) {
            envio.setEstadoPago("PAGADO");
        }

        Envio actualizado = envioRepository.save(envio);

        historialEnvioService.registrarEvento(
                actualizado,
                empleado,
                nuevoEstado,
                observacion != null && !observacion.isBlank() ? observacion : "Actualización de estado a " + nuevoEstado
        );

        return actualizado;
    }
}