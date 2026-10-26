package com.example.ciudadactiva.service.serviceImpl;

import com.example.ciudadactiva.config.JwtProvider;
import com.example.ciudadactiva.dto.request.LoginRequestDTO;
import com.example.ciudadactiva.dto.request.RegistroUsuarioDTO;
import com.example.ciudadactiva.dto.response.AuthResponseDTO;
import com.example.ciudadactiva.dto.response.UsuarioResponseDTO;
import com.example.ciudadactiva.entity.Usuario;
import com.example.ciudadactiva.exception.BusinessRuleException;
import com.example.ciudadactiva.exception.ResourceNotFoundException;
import com.example.ciudadactiva.exception.UnauthorizedException;
import com.example.ciudadactiva.repository.UsuarioRepository;
import com.example.ciudadactiva.service.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    @Transactional
    public AuthResponseDTO login(LoginRequestDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

        // Verificación de suspensión por Spam (Regla de negocio 30 días)
        if (!usuario.getActivo()) {
            if (usuario.getFechaFinSuspension() != null && LocalDateTime.now().isAfter(usuario.getFechaFinSuspension())) {
                usuario.setActivo(true);
                usuario.setContadorSpam(0);
                usuario.setFechaFinSuspension(null);
                usuarioRepository.save(usuario);
            } else {
                throw new BusinessRuleException("Su cuenta se encuentra suspendida temporalmente por acumulación de reportes rechazados como spam.");
            }
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        String token = jwtProvider.generarToken(usuario.getEmail(), usuario.getRol().name());
        return new AuthResponseDTO(token, "Bearer", usuario.getEmail(), usuario.getRol().name());
    }

    @Override
    @Transactional
    public UsuarioResponseDTO registrar(RegistroUsuarioDTO request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("El correo ya se encuentra registrado");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(request.getRol())
                .activo(true)
                .contadorSpam(0)
                .build();

        usuarioRepository.save(usuario);
        return mapToDTO(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPerfil(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return mapToDTO(usuario);
    }

    private UsuarioResponseDTO mapToDTO(Usuario u) {
        return UsuarioResponseDTO.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .email(u.getEmail())
                .rol(u.getRol().name())
                .activo(u.getActivo())
                .build();
    }
}