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

    @Column(name = "tipo_comprobante", length = 20)
    private String tipoComprobante = "BOLETA";

    @Column(name = "serie_comprobante", length = 10)
    private String serieComprobante = "B001";

    @Column(name = "numero_comprobante")
    private Integer numeroComprobante;

    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal igv = BigDecimal.ZERO;

    @Column(name = "clave_entrega", length = 4, nullable = false)
    private String claveEntrega = "1234";

    @Column(name = "numero_operacion_pago", length = 50)
    private String numeroOperacionPago;

    @Column(name = "ubicacion_almacen", length = 50)
    private String ubicacionAlmacen = "BAHIA_RECEPCION";

    @Column(name = "dni_receptor", length = 11)
    private String dniReceptor;

    @Column(name = "nombre_receptor", length = 150)
    private String nombreReceptor;

    @Column(name = "fecha_entrega")
    private java.time.LocalDateTime fechaEntrega;

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
    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante = tipoComprobante; }
    public String getSerieComprobante() { return serieComprobante; }
    public void setSerieComprobante(String serieComprobante) { this.serieComprobante = serieComprobante; }
    public Integer getNumeroComprobante() { return numeroComprobante; }
    public void setNumeroComprobante(Integer numeroComprobante) { this.numeroComprobante = numeroComprobante; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getIgv() { return igv; }
    public void setIgv(BigDecimal igv) { this.igv = igv; }
    public String getClaveEntrega() { return claveEntrega; }
    public void setClaveEntrega(String claveEntrega) { this.claveEntrega = claveEntrega; }
    public String getNumeroOperacionPago() { return numeroOperacionPago; }
    public void setNumeroOperacionPago(String numeroOperacionPago) { this.numeroOperacionPago = numeroOperacionPago; }
    public String getUbicacionAlmacen() { return ubicacionAlmacen; }
    public void setUbicacionAlmacen(String ubicacionAlmacen) { this.ubicacionAlmacen = ubicacionAlmacen; }
    public String getDniReceptor() { return dniReceptor; }
    public void setDniReceptor(String dniReceptor) { this.dniReceptor = dniReceptor; }
    public String getNombreReceptor() { return nombreReceptor; }
    public void setNombreReceptor(String nombreReceptor) { this.nombreReceptor = nombreReceptor; }
    public java.time.LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(java.time.LocalDateTime fechaEntrega) { this.fechaEntrega = fechaEntrega; }
    public String getModalidadPago() { return modalidadPago; }
    public void setModalidadPago(String modalidadPago) { this.modalidadPago = modalidadPago; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }
    public String getEstadoActual() { return estadoActual; }
    public void setEstadoActual(String estadoActual) { this.estadoActual = estadoActual; }
}