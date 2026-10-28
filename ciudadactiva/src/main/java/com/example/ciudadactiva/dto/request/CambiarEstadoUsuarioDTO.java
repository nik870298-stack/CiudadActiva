package com.example.ciudadactiva.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CambiarEstadoUsuarioDTO {
    @NotNull(message = "El estado 'activo' es obligatorio")
    private Boolean activo;
}