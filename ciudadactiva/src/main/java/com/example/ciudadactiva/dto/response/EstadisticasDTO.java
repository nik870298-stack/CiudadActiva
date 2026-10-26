package com.example.ciudadactiva.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasDTO {
    private Long totalReportes;
    private Map<String, Long> reportesPorEstado;
    private Map<String, Long> reportesPorCategoria;
    private Long reportesFueraDeSla;

    // Lista resumida de reportes que superaron la fecha límite SLA
    private List<ReporteResumenSlaDTO> detalleReportesFueraDeSla;
}