package com.example.ciudadactiva.dto.request;

import com.example.ciudadactiva.entity.enums.Rol;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegistroUsuarioDTO {
    @NotBlank
    private String nombre;
    @NotBlank @Email
    private String email;
    @NotBlank @Size(min = 6)
    private String password;
    @NotNull
    private Rol rol;
}