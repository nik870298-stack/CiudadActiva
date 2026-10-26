package com.example.ciudadactiva.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "seguimientos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "es_nota_interna", nullable = false)
    @Builder.Default
    private Boolean esNotaInterna = false;

    @Column(name = "estado_anterior")
    private String estadoAnterior;

    @Column(name = "estado_nuevo")
    private String estadoNuevo;

    @Column(name = "fecha_accion", nullable = false)
    private LocalDateTime fechaAccion;

    private Integer calificacion;

    @Column(name = "comentario_ciudadano", columnDefinition = "TEXT")
    private String comentarioCiudadano;
}