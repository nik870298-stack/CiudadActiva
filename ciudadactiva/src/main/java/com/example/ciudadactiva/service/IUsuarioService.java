package com.example.ciudadactiva.service;

import com.example.ciudadactiva.dto.request.LoginRequestDTO;
import com.example.ciudadactiva.dto.request.RegistroPublicoCiudadanoDTO;
import com.example.ciudadactiva.dto.request.RegistroUsuarioAdminDTO;
import com.example.ciudadactiva.dto.response.AuthResponseDTO;
import com.example.ciudadactiva.dto.response.UsuarioResponseDTO;

import java.util.List;

public interface IUsuarioService {
    AuthResponseDTO login(LoginRequestDTO request);
    UsuarioResponseDTO registrarCiudadano(RegistroPublicoCiudadanoDTO request);
    UsuarioResponseDTO registrarUsuarioPorAdmin(RegistroUsuarioAdminDTO request);
    UsuarioResponseDTO obtenerPerfil(String email);
    UsuarioResponseDTO cambiarEstadoUsuario(Long usuarioId, Boolean activo);
    List<UsuarioResponseDTO> listarTodosLosUsuarios();
    void eliminarUsuario(Long id);
}