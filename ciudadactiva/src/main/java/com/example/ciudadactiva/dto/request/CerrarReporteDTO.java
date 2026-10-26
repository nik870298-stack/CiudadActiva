package com.example.ciudadactiva.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CerrarReporteDTO {
    @NotBlank
    private String urlImagenSolucion;
    @NotBlank
    private String notaFinalAtencion;
}