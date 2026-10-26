package com.example.ciudadactiva.service.serviceImpl;

import com.example.ciudadactiva.dto.request.CrearCategoriaDTO;
import com.example.ciudadactiva.dto.response.CategoriaResponseDTO;
import com.example.ciudadactiva.entity.CategoriaIncidencia;
import com.example.ciudadactiva.exception.BusinessRuleException;
import com.example.ciudadactiva.repository.CategoriaIncidenciaRepository;
import com.example.ciudadactiva.service.ICategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements ICategoriaService {

    private final CategoriaIncidenciaRepository categoriaRepository;

    @Override
    @Transactional
    public CategoriaResponseDTO crearCategoria(CrearCategoriaDTO dto) {
        if (categoriaRepository.existsByNombre(dto.getNombre())) {
            throw new BusinessRuleException("Ya existe una categoría registrada con el nombre: " + dto.getNombre());
        }

        CategoriaIncidencia categoria = CategoriaIncidencia.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .tiempoSlaHoras(dto.getTiempoSlaHoras())
                .build();

        CategoriaIncidencia guardada = categoriaRepository.save(categoria);
        return mapToDTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarCategorias() {
        return categoriaRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private CategoriaResponseDTO mapToDTO(CategoriaIncidencia c) {
        return CategoriaResponseDTO.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .descripcion(c.getDescripcion())
                .tiempoSlaHoras(c.getTiempoSlaHoras())
                .build();
    }
}