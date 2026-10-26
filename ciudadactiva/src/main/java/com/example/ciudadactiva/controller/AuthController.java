package com.example.ciudadactiva.controller;

import com.example.ciudadactiva.dto.request.LoginRequestDTO;
import com.example.ciudadactiva.dto.request.RegistroPublicoCiudadanoDTO;
import com.example.ciudadactiva.dto.response.AuthResponseDTO;
import com.example.ciudadactiva.dto.response.UsuarioResponseDTO;
import com.example.ciudadactiva.service.IUsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IUsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(usuarioService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDTO> registrarCiudadano(@Valid @RequestBody RegistroPublicoCiudadanoDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrarCiudadano(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> me(Authentication authentication) {
        return ResponseEntity.ok(usuarioService.obtenerPerfil(authentication.getName()));
    }
}