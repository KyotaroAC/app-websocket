package com.appWebsocket.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "manifiestos")
public class Manifiesto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String codigoManifiesto;

    @ManyToOne
    @JoinColumn(name = "id_vehiculo")
    private Vehiculo vehiculo;

    @ManyToOne
    @JoinColumn(name = "id_conductor")
    private Usuario conductor;

    @ManyToOne
    @JoinColumn(name = "id_agencia_origen")
    private Agencia agenciaOrigen;

    @ManyToOne
    @JoinColumn(name = "id_agencia_destino")
    private Agencia agenciaDestino;

    private String estado;
    private LocalDateTime fechaCreacion;

    @ManyToMany
    @JoinTable(
            name = "manifiesto_detalles",
            joinColumns = @JoinColumn(name = "id_manifiesto"),
            inverseJoinColumns = @JoinColumn(name = "id_envio")
    )
    private List<Envio> envios;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }

    // Genera los Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCodigoManifiesto() { return codigoManifiesto; }
    public void setCodigoManifiesto(String codigoManifiesto) { this.codigoManifiesto = codigoManifiesto; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }
    public Usuario getConductor() { return conductor; }
    public void setConductor(Usuario conductor) { this.conductor = conductor; }
    public Agencia getAgenciaOrigen() { return agenciaOrigen; }
    public void setAgenciaOrigen(Agencia agenciaOrigen) { this.agenciaOrigen = agenciaOrigen; }
    public Agencia getAgenciaDestino() { return agenciaDestino; }
    public void setAgenciaDestino(Agencia agenciaDestino) { this.agenciaDestino = agenciaDestino; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public List<Envio> getEnvios() { return envios; }
    public void setEnvios(List<Envio> envios) { this.envios = envios; }
}