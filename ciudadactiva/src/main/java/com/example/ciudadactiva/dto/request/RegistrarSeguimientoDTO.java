package com.example.ciudadactiva.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegistrarSeguimientoDTO {
    @NotBlank
    private String mensaje;
    private Boolean esNotaInterna = false;
}