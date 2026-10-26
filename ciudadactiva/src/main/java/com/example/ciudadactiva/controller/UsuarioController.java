package com.example.ciudadactiva.controller;

import com.example.ciudadactiva.dto.request.RegistroUsuarioAdminDTO;
import com.example.ciudadactiva.dto.response.UsuarioResponseDTO;
import com.example.ciudadactiva.service.IUsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final IUsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> registrarPorAdmin(@Valid @RequestBody RegistroUsuarioAdminDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrarUsuarioPorAdmin(request));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarTodosLosUsuarios());
    }
}