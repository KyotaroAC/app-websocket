package com.appWebsocket.repositories;

import com.appWebsocket.entities.HistorialEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialEnvioRepository extends JpaRepository<HistorialEnvio, Integer> {
    // Para devolverle al cliente todo el Kárdex ordenado desde el más antiguo al más reciente
    List<HistorialEnvio> findByEnvioIdOrderByFechaHoraAsc(Integer idEnvio);
}