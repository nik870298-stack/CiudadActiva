package com.example.ciudadactiva.repository;

import com.example.ciudadactiva.entity.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {
    Optional<Asignacion> findByReporteIdAndActivoTrue(Long reporteId);
}