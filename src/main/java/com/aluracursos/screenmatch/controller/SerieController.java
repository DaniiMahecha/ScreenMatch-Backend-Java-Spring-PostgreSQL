package com.aluracursos.screenmatch.controller;

import com.aluracursos.screenmatch.dto.EpisodioDTO;
import com.aluracursos.screenmatch.dto.SerieDTO;
import com.aluracursos.screenmatch.model.CategoriaEnum;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController // Declaramos la clase como un controlador REST
@RequestMapping("/series") // Establece la ruta base para cualquier petición HTTP

public class SerieController {
    @Autowired // Inyectamos el servicio
    private SerieService service;

    @GetMapping("/top5")
    public List<SerieDTO> obtenerTop5Series(){
        return service.obtenerTop5Series();
    }

    @GetMapping("/lanzamientos")
    public List<SerieDTO> obtenerSeriesMasRecientes(){
        return service.obtenerSeriesMasRecientes();
    }

    @GetMapping() // Petición GET que llama al servicio
    public List<SerieDTO> obtenerTodasLasSeries(){
        return service.obtenerTodasLasSeries();
    }

    @GetMapping("/{id}")
    public SerieDTO obtenerSeriePorId(@PathVariable Long id){
        return service.SeriePorId(id);
    }

    @GetMapping("/{id}/temporadas/todas")
    public List<EpisodioDTO> obtenerTodosLosEpisodios(@PathVariable Long id){
        return service.obtenerTodosLosEpisodios(id);
    }

    @GetMapping("/{id}/temporadas/{temporada}")
    public List<EpisodioDTO> obtenerEpisodiosPorTemporada(@PathVariable Long id, @PathVariable Integer temporada){
        return service.obtenerEpisodiosPorTemporada(id, temporada);
    }

    @GetMapping("/categoria/{genero}")
    public List<SerieDTO> obtenerSerieGenero(@PathVariable String genero){
        return service.obtenerSeriePorGenero(genero); // <-- Esto
    }

}

