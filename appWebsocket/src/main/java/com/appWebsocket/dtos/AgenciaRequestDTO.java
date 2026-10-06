package com.appWebsocket.dtos;

import java.math.BigDecimal;

public class AgenciaRequestDTO {
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private String ciudad;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private BigDecimal capacidadM3;
    private String estado;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public BigDecimal getLatitud() { return latitud; }
    public void setLatitud(BigDecimal latitud) { this.latitud = latitud; }
    public BigDecimal getLongitud() { return longitud; }
    public void setLongitud(BigDecimal longitud) { this.longitud = longitud; }
    public BigDecimal getCapacidadM3() { return capacidadM3; }
    public void setCapacidadM3(BigDecimal capacidadM3) { this.capacidadM3 = capacidadM3; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
