package com.appWebsocket.repositories;

import com.appWebsocket.entities.Agencia;
import com.appWebsocket.entities.Envio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Integer> {
    
    List<Envio> findByAgenciaOrigenAndEstadoActual(Agencia agenciaOrigen, String estadoActual);

    Optional<Envio> findByCodigoTracking(String codigoTracking);

    List<Envio> findByRemitenteDniOrDestinatarioDni(String remitenteDni, String destinatarioDni);

    List<Envio> findAllByOrderByIdDesc();
}