package com.appWebsocket.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UsuarioResponseDTO {
    private Integer id;
    private String dni;
    private String nombres;
    private String apellidos;
    private String nombreCompleto;
    private String email;
    private String telefono;
    private BigDecimal sueldoBase;
    private LocalDate fechaIngreso;
    private String estadoEmpleado;
    private String rol;
    private String agenciaNombre;
    private Integer idAgencia;

    public UsuarioResponseDTO() {}

    public UsuarioResponseDTO(Integer id, String dni, String nombres, String apellidos,
                              String email, String telefono, BigDecimal sueldoBase,
                              LocalDate fechaIngreso, String estadoEmpleado,
                              String rol, String agenciaNombre, Integer idAgencia) {
        this.id = id;
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.nombreCompleto = (nombres != null ? nombres : "") + (apellidos != null ? " " + apellidos : "");
        if (this.nombreCompleto.isBlank()) {
            this.nombreCompleto = "Empleado " + dni;
        }
        this.email = email;
        this.telefono = telefono;
        this.sueldoBase = sueldoBase;
        this.fechaIngreso = fechaIngreso;
        this.estadoEmpleado = estadoEmpleado != null ? estadoEmpleado : "ACTIVO";
        this.rol = rol;
        this.agenciaNombre = agenciaNombre;
        this.idAgencia = idAgencia;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public BigDecimal getSueldoBase() { return sueldoBase; }
    public void setSueldoBase(BigDecimal sueldoBase) { this.sueldoBase = sueldoBase; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public String getEstadoEmpleado() { return estadoEmpleado; }
    public void setEstadoEmpleado(String estadoEmpleado) { this.estadoEmpleado = estadoEmpleado; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getAgenciaNombre() { return agenciaNombre; }
    public void setAgenciaNombre(String agenciaNombre) { this.agenciaNombre = agenciaNombre; }

    public Integer getIdAgencia() { return idAgencia; }
    public void setIdAgencia(Integer idAgencia) { this.idAgencia = idAgencia; }
}
