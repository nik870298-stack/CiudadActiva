package com.example.ciudadactiva.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponseDTO {
    private Integer status;
    private String error;
    private String mensaje;
    private LocalDateTime timestamp;
}