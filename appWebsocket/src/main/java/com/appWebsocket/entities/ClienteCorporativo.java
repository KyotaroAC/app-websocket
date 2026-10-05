package com.appWebsocket.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "clientes_corporativos")
public class ClienteCorporativo {

    @Id
    @Column(length = 11, nullable = false)
    private String ruc;

    @Column(name = "razon_social", nullable = false, length = 150)
    private String razonSocial;

    @Column(name = "email_contacto", length = 100)
    private String emailContacto;

    @Column(name = "telefono_contacto", length = 20)
    private String telefonoContacto;

    @Column(name = "representante_legal", length = 100)
    private String representanteLegal;

    @Column(name = "direccion_fiscal", length = 200)
    private String direccionFiscal;

    @Column(name = "linea_credito_maxima", nullable = false, precision = 10, scale = 2)
    private BigDecimal lineaCreditoMaxima;

    @Column(name = "saldo_consumido", precision = 10, scale = 2)
    private BigDecimal saldoConsumido = BigDecimal.ZERO;

    @Column(name = "dia_facturacion", nullable = false)
    private Integer diaFacturacion;

    // Getters y Setters
    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }
    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
    public String getEmailContacto() { return emailContacto; }
    public void setEmailContacto(String emailContacto) { this.emailContacto = emailContacto; }
    public String getTelefonoContacto() { return telefonoContacto; }
    public void setTelefonoContacto(String telefonoContacto) { this.telefonoContacto = telefonoContacto; }
    public String getRepresentanteLegal() { return representanteLegal; }
    public void setRepresentanteLegal(String representanteLegal) { this.representanteLegal = representanteLegal; }
    public String getDireccionFiscal() { return direccionFiscal; }
    public void setDireccionFiscal(String direccionFiscal) { this.direccionFiscal = direccionFiscal; }
    public BigDecimal getLineaCreditoMaxima() { return lineaCreditoMaxima; }
    public void setLineaCreditoMaxima(BigDecimal lineaCreditoMaxima) { this.lineaCreditoMaxima = lineaCreditoMaxima; }
    public BigDecimal getSaldoConsumido() { return saldoConsumido; }
    public void setSaldoConsumido(BigDecimal saldoConsumido) { this.saldoConsumido = saldoConsumido; }
    public Integer getDiaFacturacion() { return diaFacturacion; }
    public void setDiaFacturacion(Integer diaFacturacion) { this.diaFacturacion = diaFacturacion; }
}