package com.appWebsocket.security;

import com.appWebsocket.entities.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    // Esta clave debe ser larga para que el algoritmo de encriptación sea seguro.
    private final String SECRET_KEY = "LaClaveSecretaSuperSeguraParaElSistemaDeLogistica2026!";

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // Actualizado para recibir el Usuario y llamarse generateToken
    public String generateToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getDni())
                // ASEGÚRATE de añadir el .getNombre() al final de esta línea:
                .claim("rol", usuario.getRol().getNombre())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(getSigningKey())
                .compact();
    }

    public String obtenerDniDeToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // Si el token expiró o fue modificado, lo rechaza
        }
    }
}