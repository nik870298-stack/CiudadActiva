package com.example.ciudadactiva.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class EstadisticasDTO {
    private Long totalReportes;
    private Map<String, Long> reportesPorEstado;
    private Map<String, Long> reportesPorCategoria;
    private Long reportesFueraDeSla;
}