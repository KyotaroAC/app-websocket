package com.appWebsocket.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_envios")
public class HistorialEnvio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_envio", nullable = false)
    private Envio envio;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 50)
    private String estado;

    @Column(name = "ubicacion_descripcion", nullable = false, length = 150)
    private String ubicacionDescripcion;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    @PrePersist
    public void prePersist() {
        if (this.fechaHora == null) {
            this.fechaHora = LocalDateTime.now();
        }
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Envio getEnvio() { return envio; }
    public void setEnvio(Envio envio) { this.envio = envio; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getUbicacionDescripcion() { return ubicacionDescripcion; }
    public void setUbicacionDescripcion(String ubicacionDescripcion) { this.ubicacionDescripcion = ubicacionDescripcion; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
}