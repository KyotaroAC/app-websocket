package com.appWebsocket.dtos;

public class CambioClaveEntregaRequest {
    private String codigoTracking;
    private String nuevaClaveEntrega;
    private String motivo;

    public String getCodigoTracking() { return codigoTracking; }
    public void setCodigoTracking(String codigoTracking) { this.codigoTracking = codigoTracking; }

    public String getNuevaClaveEntrega() { return nuevaClaveEntrega; }
    public void setNuevaClaveEntrega(String nuevaClaveEntrega) { this.nuevaClaveEntrega = nuevaClaveEntrega; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
