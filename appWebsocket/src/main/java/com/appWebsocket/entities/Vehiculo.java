package com.appWebsocket.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "vehiculos")
public class Vehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String placa;
    private String marca;
    private String modelo;
    private BigDecimal capacidadKg;
    private String estado;

    // Genera los Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public BigDecimal getCapacidadKg() { return capacidadKg; }
    public void setCapacidadKg(BigDecimal capacidadKg) { this.capacidadKg = capacidadKg; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}