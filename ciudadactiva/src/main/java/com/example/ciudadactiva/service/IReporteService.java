package com.example.ciudadactiva.service;

import com.example.ciudadactiva.dto.request.*;
import com.example.ciudadactiva.dto.response.ReporteResponseDTO;
import com.example.ciudadactiva.dto.response.SeguimientoResponseDTO;
import com.example.ciudadactiva.entity.enums.EstadoReporte;
import com.example.ciudadactiva.entity.enums.Prioridad;

import java.util.List;

public interface IReporteService {
    ReporteResponseDTO crearReporte(CrearReporteDTO dto, String emailCiudadano);
    List<ReporteResponseDTO> listarReportes(Long categoriaId, EstadoReporte estado, Prioridad prioridad);
    List<ReporteResponseDTO> listarMisReportes(String emailCiudadano);
    ReporteResponseDTO obtenerDetalle(Long id, String emailUsuario);
    List<SeguimientoResponseDTO> obtenerSeguimientoReporte(Long id, String emailUsuario);
    ReporteResponseDTO validarReporte(Long id, ValidarReporteDTO dto, String emailGestor);
    void asignarOperador(Long id, AsignarOperadorDTO dto, String emailGestor);
    void registrarSeguimiento(Long id, RegistrarSeguimientoDTO dto, String emailUsuario);
    void cerrarReporte(Long id, CerrarReporteDTO dto, String emailOperador);
    List<ReporteResponseDTO> obtenerMisReportesAsignados(String emailOperador);
    void agregarComentarioCiudadano(Long id, String comentario, Integer calificacion, String emailCiudadano);
}