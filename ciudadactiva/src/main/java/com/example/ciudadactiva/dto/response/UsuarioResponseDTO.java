package com.example.ciudadactiva.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioResponseDTO {
    private Long id;
    private String nombre;
    private String email;
    private String telefono; // Expuesto para la app
    private String rol;
    private Boolean activo;
    private Integer contadorSpam;
    private LocalDateTime fechaFinSuspension;
}