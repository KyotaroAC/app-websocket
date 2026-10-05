package com.appWebsocket.services;

import com.appWebsocket.entities.Envio;
import com.appWebsocket.entities.HistorialEnvio;
import com.appWebsocket.entities.Usuario;
import com.appWebsocket.repositories.HistorialEnvioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistorialEnvioService {

    private final HistorialEnvioRepository historialEnvioRepository;

    public HistorialEnvioService(HistorialEnvioRepository historialEnvioRepository) {
        this.historialEnvioRepository = historialEnvioRepository;
    }

    @Transactional
    public HistorialEnvio registrarEvento(Envio envio, Usuario usuario, String estado, String descripcion) {
        HistorialEnvio historial = new HistorialEnvio();
        historial.setEnvio(envio);
        historial.setUsuario(usuario);
        historial.setEstado(estado);
        historial.setUbicacionDescripcion(descripcion);
        historial.setFechaHora(LocalDateTime.now());
        return historialEnvioRepository.save(historial);
    }

    public List<HistorialEnvio> obtenerHistorialPorEnvio(Integer idEnvio) {
        return historialEnvioRepository.findByEnvioIdOrderByFechaHoraAsc(idEnvio);
    }
}
