package com.aluracursos.screenmatch.dto;

import com.aluracursos.screenmatch.model.CategoriaEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record SerieDTO(  long id,
                         String titulo,
                         Integer totalTemporadas,
                         Double evaluacion,
                         String poster,
                         CategoriaEnum genero,
                         String actores,
                         String sinopsis   ) {}
