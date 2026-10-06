package com.appWebsocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilita un "megáfono" (broker) para transmitir a los clientes suscritos a /topic
        config.enableSimpleBroker("/topic");
        // Prefijo para los mensajes que envíe el cliente al servidor
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 1. Soporte para conexión WebSocket pura y directa (ws:// o wss://)
        registry.addEndpoint("/ws-logistica")
                .setAllowedOriginPatterns("*");

        // 2. Soporte con fallback de emulación SockJS (XHR streaming / Polling)
        registry.addEndpoint("/ws-logistica")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}