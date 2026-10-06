package com.appWebsocket.repositories;

import com.appWebsocket.entities.Agencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgenciaRepository extends JpaRepository<Agencia, Integer> {
    List<Agencia> findByActivoTrue();
    List<Agencia> findByEstado(String estado);
}