// ReporteController.java
package com.example.ciudadactiva.controller;

import com.example.ciudadactiva.dto.request.*;
import com.example.ciudadactiva.dto.response.ReporteResponseDTO;
import com.example.ciudadactiva.dto.response.SeguimientoResponseDTO;
import com.example.ciudadactiva.entity.enums.EstadoReporte;
import com.example.ciudadactiva.entity.enums.Prioridad;
import com.example.ciudadactiva.service.IReporteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final IReporteService reporteService;

    @PostMapping
    public ResponseEntity<ReporteResponseDTO> crear(@Valid @RequestBody CrearReporteDTO dto, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reporteService.crearReporte(dto, auth.getName()));
    }

    @GetMapping
    public ResponseEntity<List<ReporteResponseDTO>> listar(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) EstadoReporte estado,
            @RequestParam(required = false) Prioridad prioridad) {
        return ResponseEntity.ok(reporteService.listarReportes(categoriaId, estado, prioridad));
    }

    @GetMapping("/mis-reportes")
    public ResponseEntity<List<ReporteResponseDTO>> listarMisReportes(Authentication auth) {
        return ResponseEntity.ok(reporteService.listarMisReportes(auth.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReporteResponseDTO> obtenerDetalle(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(reporteService.obtenerDetalle(id, auth.getName()));
    }

    @GetMapping("/mis-asignaciones")
    public ResponseEntity<List<ReporteResponseDTO>> obtenerMisReportesAsignados(Authentication authentication) {
        List<ReporteResponseDTO> reportes = reporteService.obtenerMisReportesAsignados(authentication.getName());
        return ResponseEntity.ok(reportes);
    }

    @PatchMapping("/{id}/validar")
    public ResponseEntity<ReporteResponseDTO> validar(@PathVariable Long id, @Valid @RequestBody ValidarReporteDTO dto, Authentication auth) {
        return ResponseEntity.ok(reporteService.validarReporte(id, dto, auth.getName()));
    }

    @PostMapping("/{id}/asignaciones")
    public ResponseEntity<Void> asignar(@PathVariable Long id, @Valid @RequestBody AsignarOperadorDTO dto, Authentication auth) {
        reporteService.asignarOperador(id, dto, auth.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/seguimientos")
    public ResponseEntity<Void> registrarSeguimiento(@PathVariable Long id, @Valid @RequestBody RegistrarSeguimientoDTO dto, Authentication auth) {
        reporteService.registrarSeguimiento(id, dto, auth.getName());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<Void> cerrar(@PathVariable Long id, @Valid @RequestBody CerrarReporteDTO dto, Authentication auth) {
        reporteService.cerrarReporte(id, dto, auth.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/seguimientos")
    public ResponseEntity<List<SeguimientoResponseDTO>> obtenerSeguimiento(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(reporteService.obtenerSeguimientoReporte(id, auth.getName()));
    }

    @PostMapping("/{id}/comentarios")
    public ResponseEntity<Void> comentar(@PathVariable Long id, @RequestParam String comentario, @RequestParam Integer calificacion, Authentication auth) {
        reporteService.agregarComentarioCiudadano(id, comentario, calificacion, auth.getName());
        return ResponseEntity.noContent().build();
    }
}