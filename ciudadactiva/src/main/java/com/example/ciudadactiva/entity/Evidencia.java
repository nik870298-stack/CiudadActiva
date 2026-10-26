package com.example.ciudadactiva.entity;

import com.example.ciudadactiva.entity.enums.TipoEvidencia;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "evidencias")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Evidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url_imagen", nullable = false, length = 1000)
    private String urlImagen;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evidencia", nullable = false)
    private TipoEvidencia tipoEvidencia;

    @Column(name = "fecha_subida", nullable = false)
    private LocalDateTime fechaSubida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporte_id", nullable = false)
    private Reporte reporte;
}