package com.appWebsocket.repositories;

import com.appWebsocket.entities.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Integer> {
    
    List<Vehiculo> findByActivoTrue();

    List<Vehiculo> findByEstadoAndActivoTrue(String estado);

    Optional<Vehiculo> findByPlaca(String placa);

    boolean existsByPlaca(String placa);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Vehiculo v WHERE v.id = :id")
    Optional<Vehiculo> findByIdWithLock(@Param("id") Integer id);
}