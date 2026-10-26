package com.example.ciudadactiva.repository;

import com.example.ciudadactiva.entity.CategoriaIncidencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaIncidenciaRepository extends JpaRepository<CategoriaIncidencia, Long> {
    Boolean existsByNombre(String nombre);
}