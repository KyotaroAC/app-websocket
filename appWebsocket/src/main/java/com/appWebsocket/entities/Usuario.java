package com.appWebsocket.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 8)
    private String dni;

    @Column(length = 100)
    private String nombres;

    @Column(length = 100)
    private String apellidos;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Column(length = 200)
    private String direccion;

    @Column(name = "sueldo_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal sueldoBase = new BigDecimal("1500.00");

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso = LocalDate.now();

    @Column(name = "estado_empleado", length = 20, nullable = false)
    private String estadoEmpleado = "ACTIVO";

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @ManyToOne
    @JoinColumn(name = "id_agencia")
    private Agencia agencia; // Puede ser null para los admins

    @Column(name = "token_recuperacion", length = 6, unique = true)
    private String tokenRecuperacion;

    @Column(name = "expiracion_token")
    private LocalDateTime expiracionToken;

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public BigDecimal getSueldoBase() { return sueldoBase; }
    public void setSueldoBase(BigDecimal sueldoBase) { this.sueldoBase = sueldoBase; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public String getEstadoEmpleado() { return estadoEmpleado; }
    public void setEstadoEmpleado(String estadoEmpleado) { this.estadoEmpleado = estadoEmpleado; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public Agencia getAgencia() { return agencia; }
    public void setAgencia(Agencia agencia) { this.agencia = agencia; }

    public String getTokenRecuperacion() { return tokenRecuperacion; }
    public void setTokenRecuperacion(String tokenRecuperacion) { this.tokenRecuperacion = tokenRecuperacion; }

    public LocalDateTime getExpiracionToken() { return expiracionToken; }
    public void setExpiracionToken(LocalDateTime expiracionToken) { this.expiracionToken = expiracionToken; }
}