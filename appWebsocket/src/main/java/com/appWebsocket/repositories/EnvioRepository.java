package com.appWebsocket.repositories;

import com.appWebsocket.entities.Agencia;
import com.appWebsocket.entities.Envio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Integer> {
    
    List<Envio> findByAgenciaOrigenAndEstadoActual(Agencia agenciaOrigen, String estadoActual);

    Optional<Envio> findByCodigoTracking(String codigoTracking);

    List<Envio> findByRemitenteDniOrDestinatarioDni(String remitenteDni, String destinatarioDni);

    List<Envio> findAllByOrderByIdDesc();

    // ==========================================
    // BLOQUEO PESIMISTA (PESSIMISTIC LOCKING)
    // ==========================================
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Envio e WHERE e.id = :id")
    Optional<Envio> findByIdWithLock(@Param("id") Integer id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Envio e WHERE e.id IN :ids")
    List<Envio> findAllByIdInWithLock(@Param("ids") List<Integer> ids);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Envio e WHERE e.codigoTracking = :tracking")
    Optional<Envio> findByCodigoTrackingWithLock(@Param("tracking") String tracking);

    // ==========================================
    // CONSULTAS DE INVENTARIO DE ALMACÉN
    // ==========================================
    @Query("SELECT e FROM Envio e WHERE " +
           "(e.agenciaOrigen.id = :agenciaId AND e.estadoActual IN ('REGISTRADO', 'EN_ALMACEN_ORIGEN')) " +
           "OR (e.agenciaDestino.id = :agenciaId AND e.estadoActual IN ('EN_DESTINO', 'DISPONIBLE_RECOJO', 'EN_ALMACEN_DESTINO', 'EN AGENCIA DESTINO')) " +
           "OR ((e.agenciaOrigen.id = :agenciaId OR e.agenciaDestino.id = :agenciaId) AND e.estadoActual IN ('EXTRAVIADO', 'DANADO', 'RETENIDO_MTC', 'DEVUELTO')) " +
           "ORDER BY e.id DESC")
    List<Envio> findInventarioByAgencia(@Param("agenciaId") Integer agenciaId);

    List<Envio> findByAgenciaOrigenIdAndEstadoActual(Integer agenciaId, String estadoActual);

    List<Envio> findByAgenciaDestinoIdAndEstadoActual(Integer agenciaId, String estadoActual);

    // ==========================================
    // CORRELATIVO TRIBUTARIO ATÓMICO (ANTI-DUPLICADOS)
    // ==========================================
    @Query("SELECT COALESCE(MAX(e.numeroComprobante), 1000) FROM Envio e WHERE e.serieComprobante = :serie")
    Integer findMaxNumeroComprobanteBySerie(@Param("serie") String serie);
}