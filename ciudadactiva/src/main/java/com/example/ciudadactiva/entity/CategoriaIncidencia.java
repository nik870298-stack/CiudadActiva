package com.example.ciudadactiva.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categorias_incidencia")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CategoriaIncidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String descripcion;

    @Column(name = "tiempo_sla_horas", nullable = false)
    private Integer tiempoSlaHoras;
}