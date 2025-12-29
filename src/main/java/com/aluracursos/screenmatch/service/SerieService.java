package com.aluracursos.screenmatch.service;

import com.aluracursos.screenmatch.dto.EpisodioDTO;
import com.aluracursos.screenmatch.dto.SerieDTO;
import com.aluracursos.screenmatch.model.CategoriaEnum;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.model.Serie;
import com.aluracursos.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service // Indica que esta clase pertenece a la capa de servicio
public class SerieService {
    @Autowired // Inyección de dependencias: permite usar el repositorio
    private SerieRepository repository;

    public List<SerieDTO> obtenerTop5Series(){
        return convertirDatos(repository.findTop5ByOrderByEvaluacionDesc());
    }

    public List<SerieDTO> obtenerSeriesMasRecientes(){
        return convertirDatos(repository.lanzamientosMasRecientes());
    }

    public List<SerieDTO> obtenerTodasLasSeries() {
        return convertirDatos(repository.findAll());
    }

    public List<SerieDTO> convertirDatos(List<Serie> serieList){
        return serieList.stream()
                .map(s -> new SerieDTO(s.getId(),
                        s.getTitulo(),
                        s.getTotalTemporadas(),
                        s.getEvaluacion(),
                        s.getPoster(),
                        s.getGenero(),
                        s.getActores(),
                        s.getSinopsis()
                ))
                .collect(Collectors.toList()); // Recupera todas las series desde la base de datos y transforma las series a su DTO usando streams y map
    }


    public SerieDTO SeriePorId(Long id){
        Optional<Serie> serie = repository.findById(id);
        if(serie.isPresent()){
            Serie s = serie.get();
            return new SerieDTO(s.getId(),
                    s.getTitulo(),
                    s.getTotalTemporadas(),
                    s.getEvaluacion(),
                    s.getPoster(),
                    s.getGenero(),
                    s.getActores(),
                    s.getSinopsis());
        }
        return null;
    }

    public List<EpisodioDTO> obtenerTodosLosEpisodios(Long id) {
        Optional<Serie> serie = repository.findById(id);
        if(serie.isPresent()){
            Serie s = serie.get();
            return s.getEpisodioList().stream()
                    .map(e -> new EpisodioDTO(e.getTemporada(),
                            e.getTitulo(),
                            e.getNumeroEpisodio()

                    ))
                    .collect(Collectors.toList());
        }
        return null;
    }

    public List<EpisodioDTO> obtenerEpisodiosPorTemporada(Long id, Integer temporada) {
            List<Episodio> episodios = repository.todosLosEpisodiosPorTemporada(id, temporada);
            return episodios.stream()
                    .map(e -> new EpisodioDTO(e.getTemporada(),
                            e.getTitulo(),
                            e.getNumeroEpisodio()

                    ))
                    .collect(Collectors.toList());

    }

    public List<SerieDTO> obtenerSeriePorGenero(String genero) {
        List<Serie> serie = repository.findByGenero(CategoriaEnum.fromFront(genero));
        return convertirDatos(serie); // <-- Esto
    }

}
