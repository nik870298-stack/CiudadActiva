package com.example.ciudadactiva.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categorias_incidencia")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaIncidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "tiempo_maximo_horas_sla", nullable = false)
    private Integer tiempoMaximoHorasSLA;
}
