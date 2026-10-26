package com.example.ciudadactiva.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CrearCategoriaDTO {
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El tiempo SLA en horas es obligatorio")
    @Min(value = 1, message = "El SLA debe ser de al menos 1 hora")
    private Integer tiempoSlaHoras;
}