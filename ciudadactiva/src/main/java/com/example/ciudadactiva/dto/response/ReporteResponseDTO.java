package com.example.ciudadactiva.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ReporteResponseDTO {
    private Long id;
    private String descripcion;
    private Double latitud;
    private Double longitud;
    private String direccion;
    private String estado;
    private String prioridad;
    private Boolean sugerenciaPrioridadAlta;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaLimiteSla;
    private String nombreCiudadano;
    private String nombreCategoria;
    private List<String> imagenesEvidencia;
    private List<SeguimientoResponseDTO> historialSeguimiento;
}