package com.example.ciudadactiva.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteResumenSlaDTO {
    private Long id;
    private String descripcion;
    private String categoria;
    private String estado;
    private String prioridad;
    private LocalDateTime fechaLimiteSla;
    private Long horasDeRetraso;
}