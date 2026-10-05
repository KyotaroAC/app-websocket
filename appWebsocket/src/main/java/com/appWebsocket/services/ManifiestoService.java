package com.appWebsocket.services;

import com.appWebsocket.dtos.ManifiestoRequest;
import com.appWebsocket.entities.Manifiesto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManifiestoService {

    private final FlotaService flotaService;

    public ManifiestoService(FlotaService flotaService) {
        this.flotaService = flotaService;
    }

    public Manifiesto crearManifiesto(ManifiestoRequest request) {
        return flotaService.crearManifiesto(request);
    }

    public List<Manifiesto> listarManifiestos() {
        return flotaService.listarManifiestos();
    }
}