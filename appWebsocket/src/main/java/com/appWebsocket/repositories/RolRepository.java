package com.appWebsocket.repositories;

import com.appWebsocket.entities.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Integer> {
    // Busca el rol por su nombre (ej. "ROLE_REPARTIDOR")
    // Nota: Si el atributo en tu clase Rol se llama distinto a "nombre", actualiza esta línea.
    Optional<Rol> findByNombre(String nombre);
}