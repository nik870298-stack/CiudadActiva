package com.example.ciudadactiva.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SeguimientoResponseDTO {
    private Long id;
    private String usuarioNombre;
    private String mensaje;
    private Boolean esNotaInterna;
    private String estadoAnterior;
    private String estadoNuevo;
    private LocalDateTime fechaAccion;
}