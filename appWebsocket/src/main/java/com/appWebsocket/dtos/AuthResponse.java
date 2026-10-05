package com.appWebsocket.dtos;

public class AuthResponse {
    private String token;
    private String dni;
    private String rol;
    private Integer idAgencia; // Para saber en qué agencia trabaja el operario que inició sesión

    public AuthResponse() {
        this.token = token;
        this.dni = dni;
        this.rol = rol;
        this.idAgencia = idAgencia;
    }

    // Getters y Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public Integer getIdAgencia() { return idAgencia; }
    public void setIdAgencia(Integer idAgencia) { this.idAgencia = idAgencia; }
}