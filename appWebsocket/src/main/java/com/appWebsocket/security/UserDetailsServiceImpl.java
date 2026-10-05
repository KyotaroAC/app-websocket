package com.appWebsocket.security;

import com.appWebsocket.entities.Usuario;
import com.appWebsocket.repositories.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String dni) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByDni(dni)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con DNI: " + dni));

        return new UserDetailsImpl(usuario);
    }
}