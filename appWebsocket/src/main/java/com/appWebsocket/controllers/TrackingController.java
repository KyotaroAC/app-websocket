package com.appWebsocket.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class TrackingController {

    // Cuando el repartidor envíe sus coordenadas a la ruta "/app/actualizar-ubicacion"...
    @MessageMapping("/actualizar-ubicacion")
    // ...el servidor retransmitirá ese JSON a todos los que estén mirando el "/topic/mapa"
    @SendTo("/topic/mapa")
    public String actualizarUbicacion(String ubicacionJson) {
        // El servidor actúa como un puente ultra-rápido de retransmisión.
        return ubicacionJson;
    }
}