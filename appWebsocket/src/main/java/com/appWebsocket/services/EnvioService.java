package com.appWebsocket.services;

import com.appWebsocket.dtos.*;
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
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
public class EnvioService {

    private final EnvioRepository envioRepository;
    private final AgenciaRepository agenciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialEnvioService historialEnvioService;
    private final SimpMessagingTemplate messagingTemplate;

    private final BigDecimal TARIFA_POR_KILO = new BigDecimal("15.00");

    public EnvioService(EnvioRepository envioRepository,
                        AgenciaRepository agenciaRepository,
                        UsuarioRepository usuarioRepository,
                        HistorialEnvioService historialEnvioService,
                        SimpMessagingTemplate messagingTemplate) {
        this.envioRepository = envioRepository;
        this.agenciaRepository = agenciaRepository;
        this.usuarioRepository = usuarioRepository;
        this.historialEnvioService = historialEnvioService;
        this.messagingTemplate = messagingTemplate;
    }

    private Usuario getUsuarioActual() {
        String dniEmpleado = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByDni(dniEmpleado)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
    }

    @Transactional
    public Envio registrarEnvio(EnvioRequest request) {
        Usuario empleado = getUsuarioActual();

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

        // Desglose Tributario (Base imponible e IGV 18%)
        BigDecimal subtotal = costoTotal.divide(new BigDecimal("1.18"), 2, RoundingMode.HALF_UP);
        BigDecimal igv = costoTotal.subtract(subtotal);

        // Comprobante Electrónico (Boleta o Factura)
        String tipoComp = (request.getTipoComprobante() != null && !request.getTipoComprobante().isBlank())
                ? request.getTipoComprobante().toUpperCase()
                : (request.getRemitenteDni().length() == 11 ? "FACTURA" : "BOLETA");

        String serieComp = "FACTURA".equalsIgnoreCase(tipoComp) ? "F001" : "B001";
        int numeroComp = (int) (envioRepository.count() + 1001);

        // Clave de Seguridad de 4 Dígitos para Entrega (PIN)
        String claveEntrega = request.getClaveEntrega();
        if (claveEntrega == null || !claveEntrega.matches("\\d{4}")) {
            claveEntrega = String.format("%04d", new Random().nextInt(10000));
        }

        String estadoPago = "PENDIENTE";
        if ("PAGO_ORIGEN".equalsIgnoreCase(request.getModalidadPago())) {
            estadoPago = "PAGADO";
        } else if ("CUENTA_CORRIENTE".equalsIgnoreCase(request.getModalidadPago())) {
            estadoPago = "FACTURADO";
        }

        Envio envio = new Envio();
        envio.setCodigoTracking("TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        envio.setTipoComprobante(tipoComp);
        envio.setSerieComprobante(serieComp);
        envio.setNumeroComprobante(numeroComp);
        envio.setRemitenteDni(request.getRemitenteDni());
        envio.setDestinatarioDni(request.getDestinatarioDni());
        envio.setPesoReal(request.getPesoReal());
        envio.setPesoVolumetrico(pesoVolumetrico);
        envio.setSubtotal(subtotal);
        envio.setIgv(igv);
        envio.setCostoTotal(costoTotal);
        envio.setClaveEntrega(claveEntrega);
        envio.setNumeroOperacionPago(request.getNumeroOperacionPago());
        envio.setUbicacionAlmacen(request.getUbicacionAlmacen() != null ? request.getUbicacionAlmacen() : "BAHIA_RECEPCION");
        envio.setAgenciaOrigen(origen);
        envio.setAgenciaDestino(destino);
        envio.setModalidadPago(request.getModalidadPago());
        envio.setMetodoPago(request.getMetodoPago());
        envio.setEstadoPago(estadoPago);
        envio.setEstadoActual("REGISTRADO");

        Envio guardado = envioRepository.save(envio);

        // Auditoría KARDEX
        historialEnvioService.registrarEvento(
                guardado,
                empleado,
                "REGISTRADO",
                "Emisión de " + tipoComp + " " + serieComp + "-" + numeroComp + " en " + origen.getNombre()
        );

        // Notificación WebSocket en tiempo real a todos los clientes (Almacén, Manifiestos, Kárdex)
        try {
            Map<String, Object> ws = new HashMap<>();
            ws.put("tipo", "NUEVO_ENVIO");
            ws.put("tracking", guardado.getCodigoTracking());
            ws.put("idAgenciaOrigen", origen.getId());
            ws.put("idAgenciaDestino", destino.getId());
            messagingTemplate.convertAndSend("/topic/envios", (Object) ws);
        } catch (Exception ignored) {}

        return guardado;
    }

    // =========================================================================
    // ENTREGA FÍSICA CON BLOQUEO PESIMISTA Y VALIDACIÓN DE PIN DE 4 DÍGITOS
    // =========================================================================
    @Transactional
    public Envio entregarEnvioConPin(EntregaPaqueteRequest req) {
        Usuario empleado = getUsuarioActual();

        // Bloqueo pesimista: garantiza que ninguna otra transacción intente entregar o reasignar este paquete simultáneamente
        Envio envio = envioRepository.findByCodigoTrackingWithLock(req.getCodigoTracking())
                .orElseThrow(() -> new RuntimeException("No se encontró el envío con código: " + req.getCodigoTracking()));

        if ("ENTREGADO".equalsIgnoreCase(envio.getEstadoActual())) {
            throw new RuntimeException("Este paquete ya fue entregado con anterioridad.");
        }

        // Validación estricta del PIN de 4 dígitos
        if (!envio.getClaveEntrega().equals(req.getClaveEntrega())) {
            throw new RuntimeException("CLAVE DE RETIRO INCORRECTA: El código de 4 dígitos ingresado no coincide con el registrado por el remitente.");
        }

        if (req.getDniReceptor() == null || req.getDniReceptor().isBlank()) {
            throw new RuntimeException("Debe ingresar el DNI de la persona que retira la encomienda.");
        }

        envio.setDniReceptor(req.getDniReceptor().trim());
        envio.setNombreReceptor(req.getNombreReceptor() != null ? req.getNombreReceptor().trim() : "Receptor DNI " + req.getDniReceptor());
        envio.setFechaEntrega(LocalDateTime.now());
        envio.setEstadoActual("ENTREGADO");
        if ("PENDIENTE".equalsIgnoreCase(envio.getEstadoPago())) {
            envio.setEstadoPago("PAGADO");
        }

        Envio entregado = envioRepository.save(envio);

        historialEnvioService.registrarEvento(
                entregado,
                empleado,
                "ENTREGADO",
                "Entrega física exitosa con validación de PIN. Retirado por: " + envio.getNombreReceptor() + " (DNI: " + envio.getDniReceptor() + ")"
        );

        try {
            Map<String, Object> ws = new HashMap<>();
            ws.put("tipo", "ENVIO_ENTREGADO");
            ws.put("tracking", entregado.getCodigoTracking());
            messagingTemplate.convertAndSend("/topic/envios", (Object) ws);
        } catch (Exception ignored) {}

        return entregado;
    }

    // =========================================================================
    // RECTIFICACIÓN / CAMBIO DE CLAVE DE ENTREGA
    // =========================================================================
    @Transactional
    public Envio rectificarClaveEntrega(CambioClaveEntregaRequest req) {
        Usuario empleado = getUsuarioActual();

        Envio envio = envioRepository.findByCodigoTrackingWithLock(req.getCodigoTracking())
                .orElseThrow(() -> new RuntimeException("No se encontró el paquete con código: " + req.getCodigoTracking()));

        if ("ENTREGADO".equalsIgnoreCase(envio.getEstadoActual())) {
            throw new RuntimeException("No se puede modificar la clave de una encomienda que ya ha sido entregada.");
        }

        if (req.getNuevaClaveEntrega() == null || !req.getNuevaClaveEntrega().matches("\\d{4}")) {
            throw new RuntimeException("La nueva clave debe componerse exactamente de 4 dígitos numéricos.");
        }

        String claveAnterior = envio.getClaveEntrega();
        envio.setClaveEntrega(req.getNuevaClaveEntrega());
        Envio actualizado = envioRepository.save(envio);

        historialEnvioService.registrarEvento(
                actualizado,
                empleado,
                "CLAVE_RECTIFICADA",
                "Clave de entrega rectificada en ventanilla por solicitud de usuario. Motivo: " +
                        (req.getMotivo() != null && !req.getMotivo().isBlank() ? req.getMotivo() : "Corrección de remitente")
        );

        return actualizado;
    }

    // =========================================================================
    // INVENTARIO FÍSICO DE ALMACÉN EN AGENCIA
    // =========================================================================
    public List<InventarioItemDTO> obtenerInventarioAlmacen(Integer idAgencia) {
        List<Envio> envios = envioRepository.findInventarioByAgencia(idAgencia);
        List<InventarioItemDTO> items = new ArrayList<>();

        for (Envio e : envios) {
            InventarioItemDTO dto = new InventarioItemDTO();
            dto.setId(e.getId());
            dto.setCodigoTracking(e.getCodigoTracking());
            dto.setRemitenteDni(e.getRemitenteDni());
            dto.setDestinatarioDni(e.getDestinatarioDni());
            dto.setPesoReal(e.getPesoReal());
            dto.setCostoTotal(e.getCostoTotal());
            dto.setTipoComprobante(e.getTipoComprobante());
            dto.setSerieComprobante(e.getSerieComprobante());
            dto.setNumeroComprobante(e.getNumeroComprobante());
            dto.setClaveEntrega(e.getClaveEntrega());
            dto.setUbicacionAlmacen(e.getUbicacionAlmacen());
            dto.setEstadoActual(e.getEstadoActual());
            dto.setIdAgenciaOrigen(e.getAgenciaOrigen().getId());
            dto.setNombreAgenciaOrigen(e.getAgenciaOrigen().getNombre());
            dto.setIdAgenciaDestino(e.getAgenciaDestino().getId());
            dto.setNombreAgenciaDestino(e.getAgenciaDestino().getNombre());

            // Clasificación lógica de inventario
            if (e.getAgenciaOrigen().getId().equals(idAgencia) &&
                    ("REGISTRADO".equalsIgnoreCase(e.getEstadoActual()) || "EN_ALMACEN_ORIGEN".equalsIgnoreCase(e.getEstadoActual()))) {
                dto.setCategoriaAlmacen("PENDIENTE_SALIDA");
            } else {
                dto.setCategoriaAlmacen("EN_CUSTODIA_ENTREGA");
            }

            items.add(dto);
        }

        return items;
    }

    @Transactional
    public Envio actualizarUbicacionAlmacen(String tracking, String nuevaUbicacion) {
        Usuario empleado = getUsuarioActual();
        Envio envio = envioRepository.findByCodigoTracking(tracking)
                .orElseThrow(() -> new RuntimeException("Envío no encontrado: " + tracking));

        envio.setUbicacionAlmacen(nuevaUbicacion != null ? nuevaUbicacion : "BAHIA_RECEPCION");
        Envio actualizado = envioRepository.save(envio);

        historialEnvioService.registrarEvento(
                actualizado,
                empleado,
                "REUBICACION_ALMACEN",
                "Reubicado internamente a: " + nuevaUbicacion
        );

        try {
            Map<String, Object> ws = new HashMap<>();
            ws.put("tipo", "UBICACION_ACTUALIZADA");
            ws.put("tracking", tracking);
            ws.put("nuevaUbicacion", nuevaUbicacion);
            messagingTemplate.convertAndSend("/topic/envios", (Object) ws);
        } catch (Exception ignored) {}

        return actualizado;
    }

    public List<Envio> obtenerPendientesPorAgencia(Integer idAgenciaFiltro) {
        Usuario empleado = getUsuarioActual();

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
        Usuario empleado = getUsuarioActual();

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

        try {
            Map<String, Object> ws = new HashMap<>();
            ws.put("tipo", "ESTADO_ACTUALIZADO");
            ws.put("tracking", codigoTracking);
            ws.put("nuevoEstado", nuevoEstado);
            messagingTemplate.convertAndSend("/topic/envios", (Object) ws);
        } catch (Exception ignored) {}

        return actualizado;
    }
}