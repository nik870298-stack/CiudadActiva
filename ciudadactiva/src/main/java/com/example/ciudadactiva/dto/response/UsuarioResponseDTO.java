package com.example.ciudadactiva.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioResponseDTO {
    private Long id;
    private String nombre;
    private String email;
    private String rol;
    private Boolean activo;
}