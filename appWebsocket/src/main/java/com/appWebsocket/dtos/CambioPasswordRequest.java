package com.appWebsocket.dtos;

public class CambioPasswordRequest {
    private String dni;
    private String token;
    private String nuevaPassword;

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getNuevaPassword() { return nuevaPassword; }
    public void setNuevaPassword(String nuevaPassword) { this.nuevaPassword = nuevaPassword; }
}