package com.appWebsocket.dtos;

import java.math.BigDecimal;

public class RegistroRequest {
    private String dni;
    private String nombres;
    private String apellidos;
    private String email;
    private String telefono;
    private BigDecimal sueldoBase;
    private String password;
    private String rol; // ROLE_REPARTIDOR, ROLE_OPERARIO, ROLE_ADMIN
    private Integer idAgencia;

    // Getters y Setters
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

    public BigDecimal getSueldoBase() { return sueldoBase != null ? sueldoBase : new BigDecimal("1500.00"); }
    public void setSueldoBase(BigDecimal sueldoBase) { this.sueldoBase = sueldoBase; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Integer getIdAgencia() { return idAgencia; }
    public void setIdAgencia(Integer idAgencia) { this.idAgencia = idAgencia; }
}
