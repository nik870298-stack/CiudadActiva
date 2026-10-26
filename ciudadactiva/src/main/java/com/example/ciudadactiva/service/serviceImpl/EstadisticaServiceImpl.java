package com.example.ciudadactiva.service.serviceImpl;

import com.example.ciudadactiva.dto.response.EstadisticasDTO;
import com.example.ciudadactiva.entity.Reporte;
import com.example.ciudadactiva.repository.ReporteRepository;
import com.example.ciudadactiva.service.IEstadisticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        Map<String, Long> porEstado = reportes.stream()
                .collect(Collectors.groupingBy(r -> r.getEstado().name(), Collectors.counting()));

        Map<String, Long> porCategoria = reportes.stream()
                .collect(Collectors.groupingBy(r -> r.getCategoria().getNombre(), Collectors.counting()));

        long fueraSla = reportes.stream()
                .filter(r -> r.getFechaLimiteSla() != null
                        && LocalDateTime.now().isAfter(r.getFechaLimiteSla())
                        && !r.getEstado().name().equals("CERRADO"))
                .count();

        return EstadisticasDTO.builder()
                .totalReportes((long) reportes.size())
                .reportesPorEstado(porEstado)
                .reportesPorCategoria(porCategoria)
                .reportesFueraDeSla(fueraSla)
                .build();
    }
}