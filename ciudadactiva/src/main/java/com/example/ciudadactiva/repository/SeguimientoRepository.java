package com.example.ciudadactiva.repository;

import com.example.ciudadactiva.entity.Seguimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeguimientoRepository extends JpaRepository<Seguimiento, Long> {
    List<Seguimiento> findByReporteIdOrderByFechaAccionAsc(Long reporteId);
    List<Seguimiento> findByReporteIdAndEsNotaInternaFalseOrderByFechaAccionAsc(Long reporteId);
    Boolean existsByReporteIdAndEsNotaInternaFalse(Long reporteId);
}