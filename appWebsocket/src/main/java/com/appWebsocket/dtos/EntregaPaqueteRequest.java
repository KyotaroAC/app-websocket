package com.appWebsocket.dtos;

public class EntregaPaqueteRequest {
    private String codigoTracking;
    private String claveEntrega;
    private String dniReceptor;
    private String nombreReceptor;

    public String getCodigoTracking() { return codigoTracking; }
    public void setCodigoTracking(String codigoTracking) { this.codigoTracking = codigoTracking; }

    public String getClaveEntrega() { return claveEntrega; }
    public void setClaveEntrega(String claveEntrega) { this.claveEntrega = claveEntrega; }

    public String getDniReceptor() { return dniReceptor; }
    public void setDniReceptor(String dniReceptor) { this.dniReceptor = dniReceptor; }

    public String getNombreReceptor() { return nombreReceptor; }
    public void setNombreReceptor(String nombreReceptor) { this.nombreReceptor = nombreReceptor; }
}
