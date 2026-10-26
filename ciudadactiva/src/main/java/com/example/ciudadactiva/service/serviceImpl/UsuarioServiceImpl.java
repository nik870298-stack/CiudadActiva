package com.example.ciudadactiva.service.serviceImpl;

import com.example.ciudadactiva.config.JwtProvider;
import com.example.ciudadactiva.dto.request.LoginRequestDTO;
import com.example.ciudadactiva.dto.request.RegistroPublicoCiudadanoDTO;
import com.example.ciudadactiva.dto.request.RegistroUsuarioAdminDTO;
import com.example.ciudadactiva.dto.response.AuthResponseDTO;
import com.example.ciudadactiva.dto.response.UsuarioResponseDTO;
import com.example.ciudadactiva.entity.Usuario;
import com.example.ciudadactiva.entity.enums.Rol;
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
import java.util.List;
import java.util.stream.Collectors;

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

    // Registro público exclusivo para CIUDADANOS
    @Override
    @Transactional
    public UsuarioResponseDTO registrarCiudadano(RegistroPublicoCiudadanoDTO request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessRuleException("Las contraseñas no coinciden");
        }

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("El correo ya se encuentra registrado");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(Rol.ROLE_CIUDADANO)
                .activo(true)
                .contadorSpam(0)
                .build();

        usuarioRepository.save(usuario);
        return mapToDTO(usuario);
    }

    // Registro de personal interno realizado únicamente por ADMINISTRADOR
    @Override
    @Transactional
    public UsuarioResponseDTO registrarUsuarioPorAdmin(RegistroUsuarioAdminDTO request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessRuleException("Las contraseñas no coinciden");
        }

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("El correo ya se encuentra registrado");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .telefono(request.getTelefono())
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

    // Consulta global de usuarios (Exclusivo ADMINISTRADOR)
    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodosLosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        try {
            usuarioRepository.delete(usuario);
            usuarioRepository.flush(); // Forzar sincronización con Supabase para capturar violaciones de FK si existen
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new BusinessRuleException(
                    "No se puede eliminar el usuario porque posee registros vinculados (reportes, asignaciones o seguimientos). En su lugar, se recomienda suspender la cuenta."
            );
        }
    }

    private UsuarioResponseDTO mapToDTO(Usuario u) {
        boolean esCiudadano = u.getRol().equals(Rol.ROLE_CIUDADANO);

        return UsuarioResponseDTO.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .email(u.getEmail())
                .telefono(u.getTelefono())
                .rol(u.getRol().name())
                .activo(u.getActivo())
                .contadorSpam(esCiudadano ? u.getContadorSpam() : null)
                .fechaFinSuspension(esCiudadano ? u.getFechaFinSuspension() : null)
                .build();
    }
}