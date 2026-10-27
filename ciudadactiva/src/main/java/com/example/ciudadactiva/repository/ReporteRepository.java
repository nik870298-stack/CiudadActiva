package com.example.ciudadactiva.repository;

import com.example.ciudadactiva.entity.Reporte;
import com.example.ciudadactiva.entity.enums.EstadoReporte;
import com.example.ciudadactiva.entity.enums.Prioridad;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    @Query("SELECT DISTINCT r FROM Reporte r " +
            "JOIN FETCH r.ciudadano " +
            "JOIN FETCH r.categoria " +
            "LEFT JOIN FETCH r.evidencias " +
            "JOIN r.asignaciones a " +
            "WHERE a.operador.id = :operadorId")
    List<Reporte> findByOperadorAsignadoId(@Param("operadorId") Long operadorId);

    List<Reporte> findByCiudadanoId(Long ciudadanoId);

    // DTO Fetches evitando problema N+1
    @Query("SELECT DISTINCT r FROM Reporte r " +
            "JOIN FETCH r.ciudadano " +
            "JOIN FETCH r.categoria " +
            "LEFT JOIN FETCH r.evidencias " +
            "WHERE (:categoriaId IS NULL OR r.categoria.id = :categoriaId) " +
            "AND (:estado IS NULL OR r.estado = :estado) " +
            "AND (:prioridad IS NULL OR r.prioridad = :prioridad)")
    List<Reporte> buscarConFiltros(@Param("categoriaId") Long categoriaId,
                                   @Param("estado") EstadoReporte estado,
                                   @Param("prioridad") Prioridad prioridad);

    @Query("SELECT DISTINCT r FROM Reporte r " +
            "JOIN FETCH r.ciudadano " +
            "JOIN FETCH r.categoria " +
            "LEFT JOIN FETCH r.evidencias " +
            "WHERE r.id = :id")
    Optional<Reporte> findByIdWithDetails(@Param("id") Long id);

    // RF11: Sugerencia por concurrencia (Radio de aproximación ~100 metros: 0.001 grados lat/long)
    @Query("SELECT COUNT(r) FROM Reporte r WHERE r.categoria.id = :categoriaId " +
            "AND r.estado IN (com.example.ciudadactiva.entity.enums.EstadoReporte.PENDIENTE, com.example.ciudadactiva.entity.enums.EstadoReporte.ADMITIDO, com.example.ciudadactiva.entity.enums.EstadoReporte.EN_ATENCION) " +
            "AND ABS(r.latitud - :latitud) < 0.001 AND ABS(r.longitud - :longitud) < 0.001")
    Long contarReportesCercanosActivos(@Param("categoriaId") Long categoriaId,
                                       @Param("latitud") Double latitud,
                                       @Param("longitud") Double longitud);
}