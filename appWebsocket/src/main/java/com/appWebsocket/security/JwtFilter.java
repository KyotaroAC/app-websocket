package com.appWebsocket.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtFilter(JwtProvider jwtProvider, UserDetailsServiceImpl userDetailsService) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extrae el token de la cabecera "Authorization: Bearer <token>"
        String header = request.getHeader("Authorization");
        String token = null;
        String dni = null;

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            try {
                if (jwtProvider.validarToken(token)) {
                    dni = jwtProvider.obtenerDniDeToken(token);
                }
            } catch (Exception e) {
                System.out.println("Token inválido o expirado");
            }
        }

        // Si el DNI es válido y el usuario aún no está autenticado en el contexto actual
        if (dni != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(dni);

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Le confirmamos a Spring que el usuario tiene permiso para pasar
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}