package com.example.ciudadactiva.entity;

import com.example.ciudadactiva.entity.enums.TipoEvidencia;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evidencias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporte_id", nullable = false)
    private Reporte reporte;

    @Column(name = "ruta_fotografia", nullable = false, length = 255)
    private String rutaFotografia;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evidencia", nullable = false, length = 20)
    private TipoEvidencia tipoEvidencia;

    @Column(name = "fecha_carga", nullable = false, updatable = false)
    private LocalDateTime fechaCarga;

    @PrePersist
    protected void onCreate() {
        this.fechaCarga = LocalDateTime.now();
    }
}