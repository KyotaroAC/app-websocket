package com.appWebsocket.repositories;

import com.appWebsocket.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByDni(String dni);
    Optional<Usuario> findByTokenRecuperacion(String tokenRecuperacion);
    List<Usuario> findByRol_Nombre(String nombreRol);
}