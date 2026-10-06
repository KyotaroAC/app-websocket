package com.appWebsocket.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "vehiculos")
public class Vehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String placa;

    @Column(nullable = false, length = 50)
    private String marca;

    @Column(nullable = false, length = 50)
    private String modelo;

    @Column(name = "tipo_vehiculo", length = 50, nullable = false)
    private String tipoVehiculo = "CAMION_FURGON";

    @Column(name = "capacidad_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal capacidadKg;

    @Column(nullable = false)
    private Integer kilometraje = 0;

    @Column(name = "vencimiento_soat")
    private java.time.LocalDate vencimientoSoat;

    @Column(name = "vencimiento_revision")
    private java.time.LocalDate vencimientoRevision;

    @Column(nullable = false, length = 30)
    private String estado = "DISPONIBLE";

    @Column(nullable = false)
    private Boolean activo = true;

    // Genera los Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
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
    public java.time.LocalDate getVencimientoSoat() { return vencimientoSoat; }
    public void setVencimientoSoat(java.time.LocalDate vencimientoSoat) { this.vencimientoSoat = vencimientoSoat; }
    public java.time.LocalDate getVencimientoRevision() { return vencimientoRevision; }
    public void setVencimientoRevision(java.time.LocalDate vencimientoRevision) { this.vencimientoRevision = vencimientoRevision; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}