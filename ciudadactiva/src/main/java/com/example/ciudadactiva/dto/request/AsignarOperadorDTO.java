package com.example.ciudadactiva.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AsignarOperadorDTO {
    @NotNull
    private Long operadorId;
}