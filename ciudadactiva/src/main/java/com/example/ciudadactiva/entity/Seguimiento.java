package com.example.ciudadactiva.entity;

import com.example.ciudadactiva.entity.enums.EstadoReporte;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "seguimientos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seguimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporte_id", nullable = false)
    private Reporte reporte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String observacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_resultante", nullable = false, length = 20)
    private EstadoReporte estadoResultante;

    @Builder.Default
    @Column(name = "es_nota_interna", nullable = false)
    private Boolean esNotaInterna = false;

    @Column(name = "calificacion_ciudadano")
    private Integer calificacionCiudadano;

    @Column(name = "comentario_ciudadano", columnDefinition = "TEXT")
    private String comentarioCiudadano;

    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
    }
}
