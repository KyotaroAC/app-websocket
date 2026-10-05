package com.appWebsocket.repositories;

import com.appWebsocket.entities.ClienteCorporativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteCorporativoRepository extends JpaRepository<ClienteCorporativo, String> {
    Optional<ClienteCorporativo> findByRuc(String ruc);
}
