package com.appWebsocket.dtos;

import java.util.List;

public class ManifiestoRequest {
    private Integer idVehiculo;
    private Integer idConductor;
    private Integer idAgenciaOrigen;
    private Integer idAgenciaDestino;
    private List<Integer> idEnvios;
    private Boolean autorizarCargaBaja = false;

    public Integer getIdVehiculo() { return idVehiculo; }
    public void setIdVehiculo(Integer idVehiculo) { this.idVehiculo = idVehiculo; }

    public Integer getIdConductor() { return idConductor; }
    public void setIdConductor(Integer idConductor) { this.idConductor = idConductor; }

    public Integer getIdAgenciaOrigen() { return idAgenciaOrigen; }
    public void setIdAgenciaOrigen(Integer idAgenciaOrigen) { this.idAgenciaOrigen = idAgenciaOrigen; }

    public Integer getIdAgenciaDestino() { return idAgenciaDestino; }
    public void setIdAgenciaDestino(Integer idAgenciaDestino) { this.idAgenciaDestino = idAgenciaDestino; }

    public List<Integer> getIdEnvios() { return idEnvios; }
    public void setIdEnvios(List<Integer> idEnvios) { this.idEnvios = idEnvios; }

    public Boolean getAutorizarCargaBaja() { return autorizarCargaBaja != null ? autorizarCargaBaja : false; }
    public void setAutorizarCargaBaja(Boolean autorizarCargaBaja) { this.autorizarCargaBaja = autorizarCargaBaja; }
}