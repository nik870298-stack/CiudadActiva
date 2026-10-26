package com.example.ciudadactiva.dto.request;

import com.example.ciudadactiva.entity.enums.Prioridad;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ValidarReporteDTO {
    @NotNull
    private Boolean admitido;
    private String justificacionRechazo;
    private Boolean esSpam = false;
    private Prioridad prioridadOficial;
}