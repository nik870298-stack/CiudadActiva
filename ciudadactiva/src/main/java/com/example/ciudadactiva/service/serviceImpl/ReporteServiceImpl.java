package com.example.ciudadactiva.service.serviceImpl;

import com.example.ciudadactiva.dto.request.*;
import com.example.ciudadactiva.dto.response.ReporteResponseDTO;
import com.example.ciudadactiva.dto.response.SeguimientoResponseDTO;
import com.example.ciudadactiva.entity.*;
import com.example.ciudadactiva.entity.enums.*;
import com.example.ciudadactiva.exception.BusinessRuleException;
import com.example.ciudadactiva.exception.ResourceNotFoundException;
import com.example.ciudadactiva.repository.*;
import com.example.ciudadactiva.service.IReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements IReporteService {

    private final ReporteRepository reporteRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaIncidenciaRepository categoriaRepository;
    private final AsignacionRepository asignacionRepository;
    private final SeguimientoRepository seguimientoRepository;
    private final EvidenciaRepository evidenciaRepository;

    @Override
    @Transactional
    public ReporteResponseDTO crearReporte(CrearReporteDTO dto, String emailCiudadano) {
        Usuario ciudadano = usuarioRepository.findByEmail(emailCiudadano)
                .orElseThrow(() -> new ResourceNotFoundException("Ciudadano no encontrado"));

        if (!ciudadano.getActivo()) {
            throw new BusinessRuleException("El usuario se encuentra suspendido y no puede crear reportes.");
        }

        CategoriaIncidencia categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        Reporte reporte = Reporte.builder()
                .descripcion(dto.getDescripcion())
                .latitud(dto.getLatitud())
                .longitud(dto.getLongitud())
                .direccion(dto.getDireccion())
                .estado(EstadoReporte.PENDIENTE)
                .prioridad(Prioridad.BAJA)
                .fechaCreacion(LocalDateTime.now())
                .ciudadano(ciudadano)
                .categoria(categoria)
                .build();

        // Evidencia obligatoria requerida por el flujo
        Evidencia evidencia = Evidencia.builder()
                .urlImagen(dto.getUrlImagenEvidencia())
                .tipoEvidencia(TipoEvidencia.CREACION)
                .fechaSubida(LocalDateTime.now())
                .reporte(reporte)
                .build();

        reporte.getEvidencias().add(evidencia);
        Reporte guardado = reporteRepository.save(reporte);

        return mapToDTO(guardado, ciudadano);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteResponseDTO> listarReportes(Long categoriaId, EstadoReporte estado, Prioridad prioridad) {
        return reporteRepository.buscarConFiltros(categoriaId, estado, prioridad).stream()
                .map(r -> mapToDTO(r, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteResponseDTO> listarMisReportes(String emailCiudadano) {
        Usuario ciudadano = usuarioRepository.findByEmail(emailCiudadano)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return reporteRepository.findByCiudadanoId(ciudadano.getId()).stream()
                .map(r -> mapToDTO(r, ciudadano))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteResponseDTO obtenerDetalle(Long id, String emailUsuario) {
        Reporte reporte = reporteRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado"));
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return mapToDTO(reporte, usuario);
    }

    @Override
    @Transactional
    public ReporteResponseDTO validarReporte(Long id, ValidarReporteDTO dto, String emailGestor) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado"));
        Usuario gestor = usuarioRepository.findByEmail(emailGestor)
                .orElseThrow(() -> new ResourceNotFoundException("Gestor no encontrado"));

        if (!reporte.getEstado().equals(EstadoReporte.PENDIENTE)) {
            throw new BusinessRuleException("Solo se pueden validar reportes en estado PENDIENTE");
        }

        String estadoAnterior = reporte.getEstado().name();

        if (dto.getAdmitido()) {
            reporte.setEstado(EstadoReporte.ADMITIDO);
            reporte.setPrioridad(dto.getPrioridadOficial() != null ? dto.getPrioridadOficial() : Prioridad.MEDIA);
            // Asignación automática de fecha límite SLA
            reporte.setFechaLimiteSla(LocalDateTime.now().plusHours(reporte.getCategoria().getTiempoSlaHoras()));
        } else {
            if (dto.getJustificacionRechazo() == null || dto.getJustificacionRechazo().isBlank()) {
                throw new BusinessRuleException("Todo reporte rechazado requiere una justificación explícita.");
            }
            reporte.setEstado(EstadoReporte.RECHAZADO);

            // Regla de penalización por Spam acumulado
            if (Boolean.TRUE.equals(dto.getEsSpam())) {
                Usuario ciudadano = reporte.getCiudadano();
                ciudadano.setContadorSpam(ciudadano.getContadorSpam() + 1);
                if (ciudadano.getContadorSpam() >= 3) {
                    ciudadano.setActivo(false);
                    ciudadano.setFechaFinSuspension(LocalDateTime.now().plusDays(30));
                }
                usuarioRepository.save(ciudadano);
            }
        }

        Seguimiento seg = Seguimiento.builder()
                .reporte(reporte)
                .usuario(gestor)
                .mensaje(dto.getAdmitido() ? "Reporte admitido." : "Reporte rechazado. Motivo: " + dto.getJustificacionRechazo())
                .esNotaInterna(false)
                .estadoAnterior(estadoAnterior)
                .estadoNuevo(reporte.getEstado().name())
                .fechaAccion(LocalDateTime.now())
                .build();

        seguimientoRepository.save(seg);
        Reporte actualizado = reporteRepository.save(reporte);

        return mapToDTO(actualizado, gestor);
    }

    @Override
    @Transactional
    public void asignarOperador(Long id, AsignarOperadorDTO dto, String emailGestor) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado"));

        if (!reporte.getEstado().equals(EstadoReporte.ADMITIDO)) {
            throw new BusinessRuleException("Solo se pueden asignar operadores a reportes ADMITIDOS.");
        }

        Usuario operador = usuarioRepository.findById(dto.getOperadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Operador no encontrado"));

        if (!operador.getRol().equals(Rol.ROLE_OPERADOR)) {
            throw new BusinessRuleException("El usuario asignado debe tener el rol OPERADOR.");
        }

        // Desactivar asignación previa si existiera
        asignacionRepository.findByReporteIdAndActivoTrue(id).ifPresent(a -> {
            a.setActivo(false);
            asignacionRepository.save(a);
        });

        Asignacion asignacion = Asignacion.builder()
                .reporte(reporte)
                .operador(operador)
                .fechaAsignacion(LocalDateTime.now())
                .activo(true)
                .build();

        reporte.setEstado(EstadoReporte.EN_ATENCION);
        asignacionRepository.save(asignacion);
        reporteRepository.save(reporte);
    }

    @Override
    @Transactional
    public void registrarSeguimiento(Long id, RegistrarSeguimientoDTO dto, String emailUsuario) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado"));
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Seguimiento seg = Seguimiento.builder()
                .reporte(reporte)
                .usuario(usuario)
                .mensaje(dto.getMensaje())
                .esNotaInterna(dto.getEsNotaInterna())
                .fechaAccion(LocalDateTime.now())
                .build();

        seguimientoRepository.save(seg);
    }

    @Override
    @Transactional
    public void cerrarReporte(Long id, CerrarReporteDTO dto, String emailOperador) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado"));
        Usuario operador = usuarioRepository.findByEmail(emailOperador)
                .orElseThrow(() -> new ResourceNotFoundException("Operador no encontrado"));

        if (!reporte.getEstado().equals(EstadoReporte.EN_ATENCION)) {
            throw new BusinessRuleException("Solo se pueden cerrar reportes que estén EN_ATENCION.");
        }

        // Regla de Negocio: Debe tener al menos una nota previa
        Boolean tieneNotasAccion = seguimientoRepository.existsByReporteIdAndEsNotaInternaFalse(id);
        if (!tieneNotasAccion) {
            throw new BusinessRuleException("No se puede cerrar un reporte sin haber registrado previamente al menos un avance de atención.");
        }

        Evidencia evidenciaSolucion = Evidencia.builder()
                .urlImagen(dto.getUrlImagenSolucion())
                .tipoEvidencia(TipoEvidencia.CIERRE)
                .fechaSubida(LocalDateTime.now())
                .reporte(reporte)
                .build();

        evidenciaRepository.save(evidenciaSolucion);

        String estadoPrevio = reporte.getEstado().name();
        reporte.setEstado(EstadoReporte.CERRADO);

        Seguimiento cierre = Seguimiento.builder()
                .reporte(reporte)
                .usuario(operador)
                .mensaje("Cierre de caso: " + dto.getNotaFinalAtencion())
                .esNotaInterna(false)
                .estadoAnterior(estadoPrevio)
                .estadoNuevo(EstadoReporte.CERRADO.name())
                .fechaAccion(LocalDateTime.now())
                .build();

        seguimientoRepository.save(cierre);
        reporteRepository.save(reporte);
    }

    @Override
    @Transactional
    public void agregarComentarioCiudadano(Long id, String comentario, Integer calificacion, String emailCiudadano) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado"));
        Usuario ciudadano = usuarioRepository.findByEmail(emailCiudadano)
                .orElseThrow(() -> new ResourceNotFoundException("Ciudadano no encontrado"));

        if (!reporte.getCiudadano().getId().equals(ciudadano.getId())) {
            throw new BusinessRuleException("Solo el creador del reporte puede calificar la atención.");
        }

        if (!reporte.getEstado().equals(EstadoReporte.CERRADO)) {
            throw new BusinessRuleException("Solo se pueden evaluar casos que se encuentren CERRADOS.");
        }

        Seguimiento retroalimentacion = Seguimiento.builder()
                .reporte(reporte)
                .usuario(ciudadano)
                .mensaje("Calificación del ciudadano")
                .comentarioCiudadano(comentario)
                .calificacion(calificacion)
                .esNotaInterna(false)
                .fechaAccion(LocalDateTime.now())
                .build();

        seguimientoRepository.save(retroalimentacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeguimientoResponseDTO> obtenerSeguimientoReporte(Long id, String emailUsuario) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con id: " + id));

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        List<Seguimiento> seguimientos;

        // Regla de Negocio: Las notas internas solo son visibles para GESTOR, OPERADOR o ADMINISTRADOR
        if (usuario.getRol().equals(Rol.ROLE_CIUDADANO)) {
            seguimientos = seguimientoRepository.findByReporteIdAndEsNotaInternaFalseOrderByFechaAccionAsc(id);
        } else {
            seguimientos = seguimientoRepository.findByReporteIdOrderByFechaAccionAsc(id);
        }

        return seguimientos.stream()
                .map(s -> SeguimientoResponseDTO.builder()
                        .id(s.getId())
                        .usuarioNombre(s.getUsuario().getNombre())
                        .mensaje(s.getMensaje())
                        .esNotaInterna(s.getEsNotaInterna())
                        .estadoAnterior(s.getEstadoAnterior())
                        .estadoNuevo(s.getEstadoNuevo())
                        .fechaAccion(s.getFechaAccion())
                        .build())
                .collect(Collectors.toList());
    }

    private ReporteResponseDTO mapToDTO(Reporte r, Usuario usuarioConsulta) {
        Long conteoSpamArea = reporteRepository.contarReportesCercanosActivos(r.getCategoria().getId(), r.getLatitud(), r.getLongitud());

        // Filtrado por RBAC: Notas internas invisibles para CIUDADANO
        List<Seguimiento> seguimientos = seguimientoRepository.findByReporteIdOrderByFechaAccionAsc(r.getId());
        if (usuarioConsulta != null && usuarioConsulta.getRol().equals(Rol.ROLE_CIUDADANO)) {
            seguimientos = seguimientos.stream().filter(s -> !s.getEsNotaInterna()).collect(Collectors.toList());
        }

        return ReporteResponseDTO.builder()
                .id(r.getId())
                .descripcion(r.getDescripcion())
                .latitud(r.getLatitud())
                .longitud(r.getLongitud())
                .direccion(r.getDireccion())
                .estado(r.getEstado().name())
                .prioridad(r.getPrioridad().name())
                .sugerenciaPrioridadAlta(conteoSpamArea >= 3)
                .fechaCreacion(r.getFechaCreacion())
                .fechaLimiteSla(r.getFechaLimiteSla())
                .nombreCiudadano(r.getCiudadano().getNombre())
                .nombreCategoria(r.getCategoria().getNombre())
                .imagenesEvidencia(r.getEvidencias().stream().map(Evidencia::getUrlImagen).collect(Collectors.toList()))
                .historialSeguimiento(seguimientos.stream().map(s -> SeguimientoResponseDTO.builder()
                        .id(s.getId())
                        .usuarioNombre(s.getUsuario().getNombre())
                        .mensaje(s.getMensaje())
                        .esNotaInterna(s.getEsNotaInterna())
                        .estadoAnterior(s.getEstadoAnterior())
                        .estadoNuevo(s.getEstadoNuevo())
                        .fechaAccion(s.getFechaAccion())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}