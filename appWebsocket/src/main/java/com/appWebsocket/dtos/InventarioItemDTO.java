package com.appWebsocket.dtos;

import java.math.BigDecimal;

public class InventarioItemDTO {
    private Integer id;
    private String codigoTracking;
    private String remitenteDni;
    private String destinatarioDni;
    private BigDecimal pesoReal;
    private BigDecimal costoTotal;
    private String tipoComprobante;
    private String serieComprobante;
    private Integer numeroComprobante;
    private String claveEntrega;
    private String ubicacionAlmacen;
    private String estadoActual;
    private Integer idAgenciaOrigen;
    private String nombreAgenciaOrigen;
    private Integer idAgenciaDestino;
    private String nombreAgenciaDestino;
    private String categoriaAlmacen; // 'PENDIENTE_SALIDA' o 'EN_CUSTODIA_ENTREGA'

    public InventarioItemDTO() {}

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
    public BigDecimal getCostoTotal() { return costoTotal; }
    public void setCostoTotal(BigDecimal costoTotal) { this.costoTotal = costoTotal; }
    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante = tipoComprobante; }
    public String getSerieComprobante() { return serieComprobante; }
    public void setSerieComprobante(String serieComprobante) { this.serieComprobante = serieComprobante; }
    public Integer getNumeroComprobante() { return numeroComprobante; }
    public void setNumeroComprobante(Integer numeroComprobante) { this.numeroComprobante = numeroComprobante; }
    public String getClaveEntrega() { return claveEntrega; }
    public void setClaveEntrega(String claveEntrega) { this.claveEntrega = claveEntrega; }
    public String getUbicacionAlmacen() { return ubicacionAlmacen; }
    public void setUbicacionAlmacen(String ubicacionAlmacen) { this.ubicacionAlmacen = ubicacionAlmacen; }
    public String getEstadoActual() { return estadoActual; }
    public void setEstadoActual(String estadoActual) { this.estadoActual = estadoActual; }
    public Integer getIdAgenciaOrigen() { return idAgenciaOrigen; }
    public void setIdAgenciaOrigen(Integer idAgenciaOrigen) { this.idAgenciaOrigen = idAgenciaOrigen; }
    public String getNombreAgenciaOrigen() { return nombreAgenciaOrigen; }
    public void setNombreAgenciaOrigen(String nombreAgenciaOrigen) { this.nombreAgenciaOrigen = nombreAgenciaOrigen; }
    public Integer getIdAgenciaDestino() { return idAgenciaDestino; }
    public void setIdAgenciaDestino(Integer idAgenciaDestino) { this.idAgenciaDestino = idAgenciaDestino; }
    public String getNombreAgenciaDestino() { return nombreAgenciaDestino; }
    public void setNombreAgenciaDestino(String nombreAgenciaDestino) { this.nombreAgenciaDestino = nombreAgenciaDestino; }
    public String getCategoriaAlmacen() { return categoriaAlmacen; }
    public void setCategoriaAlmacen(String categoriaAlmacen) { this.categoriaAlmacen = categoriaAlmacen; }
}
