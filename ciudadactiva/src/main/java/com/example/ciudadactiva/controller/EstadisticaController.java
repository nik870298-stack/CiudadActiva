// EstadisticaController.java
package com.example.ciudadactiva.controller;

import com.example.ciudadactiva.dto.response.EstadisticasDTO;
import com.example.ciudadactiva.service.IEstadisticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/estadisticas")
@RequiredArgsConstructor
public class EstadisticaController {

    private final IEstadisticaService estadisticaService;

    @GetMapping("/reportes")
    public ResponseEntity<EstadisticasDTO> obtenerEstadisticas() {
        return ResponseEntity.ok(estadisticaService.obtenerEstadisticasGenerales());
    }
}