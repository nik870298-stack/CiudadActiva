package com.example.ciudadactiva.service;

import com.example.ciudadactiva.dto.request.LoginRequestDTO;
import com.example.ciudadactiva.dto.request.RegistroUsuarioDTO;
import com.example.ciudadactiva.dto.response.AuthResponseDTO;
import com.example.ciudadactiva.dto.response.UsuarioResponseDTO;

public interface IUsuarioService {
    AuthResponseDTO login(LoginRequestDTO request);
    UsuarioResponseDTO registrar(RegistroUsuarioDTO request);
    UsuarioResponseDTO obtenerPerfil(String email);
}