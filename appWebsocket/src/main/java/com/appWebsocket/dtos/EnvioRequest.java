package com.appWebsocket.dtos;

import java.math.BigDecimal;

public class EnvioRequest {
    private String remitenteDni;
    private String destinatarioDni;

    private BigDecimal pesoReal;
    // En lugar de pedir el peso volumétrico, pedimos las dimensiones en centímetros
    private BigDecimal largo;
    private BigDecimal ancho;
    private BigDecimal alto;

    private Integer idAgenciaOrigen;
    private Integer idAgenciaDestino;

    private String modalidadPago; // 'PAGO_ORIGEN', 'PAGO_DESTINO', 'CUENTA_CORRIENTE'
    private String metodoPago;    // 'EFECTIVO', 'TARJETA', 'YAPE_PLIN', 'CREDITO_CORP'
    private String rucCorporativo; // Opcional, solo si es cuenta corriente

    // Genera todos los Getters y Setters aquí
    public String getRemitenteDni() { return remitenteDni; }
    public void setRemitenteDni(String remitenteDni) { this.remitenteDni = remitenteDni; }
    public String getDestinatarioDni() { return destinatarioDni; }
    public void setDestinatarioDni(String destinatarioDni) { this.destinatarioDni = destinatarioDni; }
    public BigDecimal getPesoReal() { return pesoReal; }
    public void setPesoReal(BigDecimal pesoReal) { this.pesoReal = pesoReal; }
    public BigDecimal getLargo() { return largo; }
    public void setLargo(BigDecimal largo) { this.largo = largo; }
    public BigDecimal getAncho() { return ancho; }
    public void setAncho(BigDecimal ancho) { this.ancho = ancho; }
    public BigDecimal getAlto() { return alto; }
    public void setAlto(BigDecimal alto) { this.alto = alto; }
    public Integer getIdAgenciaOrigen() { return idAgenciaOrigen; }
    public void setIdAgenciaOrigen(Integer idAgenciaOrigen) { this.idAgenciaOrigen = idAgenciaOrigen; }
    public Integer getIdAgenciaDestino() { return idAgenciaDestino; }
    public void setIdAgenciaDestino(Integer idAgenciaDestino) { this.idAgenciaDestino = idAgenciaDestino; }
    public String getModalidadPago() { return modalidadPago; }
    public void setModalidadPago(String modalidadPago) { this.modalidadPago = modalidadPago; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public String getRucCorporativo() { return rucCorporativo; }
    public void setRucCorporativo(String rucCorporativo) { this.rucCorporativo = rucCorporativo; }
}