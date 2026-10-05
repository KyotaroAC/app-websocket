package com.appWebsocket.dtos;

import com.appWebsocket.entities.Envio;
import com.appWebsocket.entities.HistorialEnvio;

import java.util.List;

public class TrackingDetalleDTO {
    private Envio envio;
    private List<HistorialEnvio> historial;

    public TrackingDetalleDTO() {}

    public TrackingDetalleDTO(Envio envio, List<HistorialEnvio> historial) {
        this.envio = envio;
        this.historial = historial;
    }

    public Envio getEnvio() { return envio; }
    public void setEnvio(Envio envio) { this.envio = envio; }

    public List<HistorialEnvio> getHistorial() { return historial; }
    public void setHistorial(List<HistorialEnvio> historial) { this.historial = historial; }
}
