package com.appWebsocket.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class VehiculoRequestDTO {
    private String placa;
    private String marca;
    private String modelo;
    private String tipoVehiculo;
    private BigDecimal capacidadKg;
    private Integer kilometraje;
    private LocalDate vencimientoSoat;
    private LocalDate vencimientoRevision;
    private String estado;

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String getTipoVehiculo() { return tipoVehiculo; }
    public void setTipoVehiculo(String tipoVehiculo) { this.tipoVehiculo = tipoVehiculo; }
    public BigDecimal getCapacidadKg() { return capacidadKg; }
    public void setCapacidadKg(BigDecimal capacidadKg) { this.capacidadKg = capacidadKg; }
    public Integer getKilometraje() { return kilometraje; }
    public void setKilometraje(Integer kilometraje) { this.kilometraje = kilometraje; }
    public LocalDate getVencimientoSoat() { return vencimientoSoat; }
    public void setVencimientoSoat(LocalDate vencimientoSoat) { this.vencimientoSoat = vencimientoSoat; }
    public LocalDate getVencimientoRevision() { return vencimientoRevision; }
    public void setVencimientoRevision(LocalDate vencimientoRevision) { this.vencimientoRevision = vencimientoRevision; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
