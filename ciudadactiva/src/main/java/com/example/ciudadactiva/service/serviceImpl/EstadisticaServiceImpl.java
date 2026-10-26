package com.example.ciudadactiva.service.serviceImpl;

import com.example.ciudadactiva.dto.response.EstadisticasDTO;
import com.example.ciudadactiva.dto.response.ReporteResumenSlaDTO;
import com.example.ciudadactiva.entity.Reporte;
import com.example.ciudadactiva.entity.enums.EstadoReporte;
import com.example.ciudadactiva.repository.ReporteRepository;
import com.example.ciudadactiva.service.IEstadisticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstadisticaServiceImpl implements IEstadisticaService {

    private final ReporteRepository reporteRepository;

    @Override
    @Transactional(readOnly = true)
    public EstadisticasDTO obtenerEstadisticasGenerales() {
        List<Reporte> reportes = reporteRepository.findAll();
        LocalDateTime ahora = LocalDateTime.now();

        Map<String, Long> porEstado = reportes.stream()
                .collect(Collectors.groupingBy(r -> r.getEstado().name(), Collectors.counting()));

        Map<String, Long> porCategoria = reportes.stream()
                .collect(Collectors.groupingBy(r -> r.getCategoria().getNombre(), Collectors.counting()));

        // Filtrar reportes activos (ADMITIDO, EN_ATENCION, PENDIENTE) que superaron su fecha límite SLA
        List<ReporteResumenSlaDTO> reportesVencidos = reportes.stream()
                .filter(r -> r.getFechaLimiteSla() != null
                        && ahora.isAfter(r.getFechaLimiteSla())
                        && !r.getEstado().equals(EstadoReporte.CERRADO)
                        && !r.getEstado().equals(EstadoReporte.RECHAZADO))
                .map(r -> ReporteResumenSlaDTO.builder()
                        .id(r.getId())
                        .descripcion(r.getDescripcion())
                        .categoria(r.getCategoria().getNombre())
                        .estado(r.getEstado().name())
                        .prioridad(r.getPrioridad().name())
                        .fechaLimiteSla(r.getFechaLimiteSla())
                        .horasDeRetraso(Duration.between(r.getFechaLimiteSla(), ahora).toHours())
                        .build())
                .collect(Collectors.toList());

        return EstadisticasDTO.builder()
                .totalReportes((long) reportes.size())
                .reportesPorEstado(porEstado)
                .reportesPorCategoria(porCategoria)
                .reportesFueraDeSla((long) reportesVencidos.size())
                .detalleReportesFueraDeSla(reportesVencidos)
                .build();
    }
}