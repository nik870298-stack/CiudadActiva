package com.example.ciudadactiva.service;

import com.example.ciudadactiva.dto.request.CrearCategoriaDTO;
import com.example.ciudadactiva.dto.response.CategoriaResponseDTO;

import java.util.List;

public interface ICategoriaService {
    CategoriaResponseDTO crearCategoria(CrearCategoriaDTO dto);
    List<CategoriaResponseDTO> listarCategorias();
}