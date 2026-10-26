package com.example.ciudadactiva.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String tipo = "Bearer";
    private String email;
    private String rol;
}