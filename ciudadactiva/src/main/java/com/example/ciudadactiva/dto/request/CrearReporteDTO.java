package com.example.ciudadactiva.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CrearReporteDTO {
    @NotBlank
    private String descripcion;
    @NotNull
    private Double latitud;
    @NotNull
    private Double longitud;
    private String direccion;
    @NotNull
    private Long categoriaId;
    @NotBlank
    private String urlImagenEvidencia; // Adjunto obligatorio por negocio
}