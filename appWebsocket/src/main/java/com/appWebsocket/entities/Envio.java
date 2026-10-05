package com.appWebsocket.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "envios")
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codigo_tracking", nullable = false, unique = true, length = 20)
    private String codigoTracking;

    @Column(name = "remitente_dni", nullable = false, length = 11)
    private String remitenteDni;

    @Column(name = "destinatario_dni", nullable = false, length = 11)
    private String destinatarioDni;

    @Column(name = "peso_real", nullable = false, precision = 8, scale = 2)
    private BigDecimal pesoReal;

    @Column(name = "peso_volumetrico", nullable = false, precision = 8, scale = 2)
    private BigDecimal pesoVolumetrico;

    @Column(name = "costo_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoTotal;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_agencia_origen", nullable = false)
    private Agencia agenciaOrigen;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_agencia_destino", nullable = false)
    private Agencia agenciaDestino;

    // Mapeo seguro de ENUMs desde la base de datos
    @Column(name = "modalidad_pago", nullable = false, columnDefinition = "ENUM('PAGO_ORIGEN', 'PAGO_DESTINO', 'CUENTA_CORRIENTE')")
    private String modalidadPago;

    @Column(name = "metodo_pago", nullable = false, columnDefinition = "ENUM('EFECTIVO', 'TARJETA', 'YAPE_PLIN', 'CREDITO_CORP')")
    private String metodoPago;

    @Column(name = "estado_pago", nullable = false, columnDefinition = "ENUM('PENDIENTE', 'PAGADO', 'FACTURADO')")
    private String estadoPago;

    @Column(name = "estado_actual", nullable = false, length = 50)
    private String estadoActual = "REGISTRADO";

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCodigoTracking() { return codigoTracking; }
    public void setCodigoTracking(String codigoTracking) { this.codigoTracking = codigoTracking; }
    public String getRemitenteDni() { return remitenteDni; }
    public void setRemitenteDni(String remitenteDni) { this.remitenteDni = remitenteDni; }
    public String getDestinatarioDni() { return destinatarioDni; }
    public void setDestinatarioDni(String destinatarioDni) { this.destinatarioDni = destinatarioDni; }
    public BigDecimal getPesoReal() { return pesoReal; }
    public void setPesoReal(BigDecimal pesoReal) { this.pesoReal = pesoReal; }
    public BigDecimal getPesoVolumetrico() { return pesoVolumetrico; }
    public void setPesoVolumetrico(BigDecimal pesoVolumetrico) { this.pesoVolumetrico = pesoVolumetrico; }
    public BigDecimal getCostoTotal() { return costoTotal; }
    public void setCostoTotal(BigDecimal costoTotal) { this.costoTotal = costoTotal; }
    public Agencia getAgenciaOrigen() { return agenciaOrigen; }
    public void setAgenciaOrigen(Agencia agenciaOrigen) { this.agenciaOrigen = agenciaOrigen; }
    public Agencia getAgenciaDestino() { return agenciaDestino; }
    public void setAgenciaDestino(Agencia agenciaDestino) { this.agenciaDestino = agenciaDestino; }
    public String getModalidadPago() { return modalidadPago; }
    public void setModalidadPago(String modalidadPago) { this.modalidadPago = modalidadPago; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }
    public String getEstadoActual() { return estadoActual; }
    public void setEstadoActual(String estadoActual) { this.estadoActual = estadoActual; }
}