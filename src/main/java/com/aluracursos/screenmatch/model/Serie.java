package com.aluracursos.screenmatch.model;


import com.aluracursos.screenmatch.service.ConsultaChatGPT;
import com.aluracursos.screenmatch.service.ConsultaGemini;
import jakarta.persistence.*;

import java.util.List;
import java.util.OptionalDouble;

@Entity // Hace a la clase Serie una entidad (tabla en PostgreSQL)
@Table(name = "series") // La entidad a diferencia de la clase se llamará "series" en la base de datos

public class Serie {
    @Id // Es el identificador unico de la entidad dentro de la base de datos
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indíca de qué manera será creado el ID
                                                        // En este caso sera autoincremental
                                                        // Quiere decir que a la primera entidad creada
                                                        // Se le asiganará el ID 1, a la segunda el 2
                                                        // Y así sucesivamente de manera incremental.
    private Long id;


    @Column(unique = true) // Restringe el valor a ser único en la base de datos (no permite duplicados).
    private  String titulo;

    private Integer totalTemporadas;
    private Double evaluacion;
    private String poster;

    @Enumerated(EnumType.STRING) // @Enumerated(EnumType.STRING) indica que el valor del enum se almacenará como
                                 // texto en la base de datos. Esto evita problemas si cambia el orden de los
                                 // elementos del enum (lo cual sí afectaría al usar EnumType.ORDINAL).
                                 // Recomendada para mantener integridad y legibilidad en la persistencia.
    private  CategoriaEnum genero;


    private String actores;
    private String sinopsis;
    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, fetch = FetchType.EAGER) // Indica que la relación será de 1:N y
                                                              // mapeara la relación por el campo serie de la entidad Episodio
                                                              // Agrgandole el comportamiento de cascada rn donde realizará
                                                              // a conveniencia la operación CRUD correspondiente.
                                                              // Dejando en claro que se podran hacer llamados para consultar datos
                                                              // que incluso ya esten precargados en la base de datos

    private List<Episodio> episodioList; // Relación bidireccional con la clase Episodio
                                         // 👉 “Una serie está compuesta por varios episodios”.

    public Serie(){} // Constructor vacio para evitar InstantiatioException en Hibernate

    public Serie(DatosSerie datosSerie) {
        this.titulo = datosSerie.titulo();
        this.totalTemporadas = datosSerie.totalTemporadas();

        //En el Optional se guardará o no la evaluación, esto pendende si fué posible
        //parsear el String de evaluación a Double-
        //Si no fue posible "orElse(0)", el optional será 0.

        //Quiere decir que el Optional tendrá dos posibilidades
        //  - Realizar el Cast correctamente y devolver el Double
        //  - Devolver 0

        this.evaluacion = OptionalDouble.of(Double.valueOf(datosSerie.evaluacion())).orElse(0);
        this.poster = datosSerie.poster();

        // Hacemos uso del método estático del Enum "CategoriaEnum"
        // sin embargo en la API las categorias vienen de la siguiente manera:
        // "Action, Crime, Drama"
        // Para poder devolver solo una categoría usamos .split()
        // .split() sepára al String con un punto de quiebre dado como párametro
        //
        // .split(",")
        //
        // Como queremos acceder a la primera categoría dentro del Stream lo indicamos con [0]
        // {"Action", "Crime", "Drama"}
        //     0         1        2
        //
        // .split(",")[0]
        //
        // Por ultimo, usamos .trim(), para evitar un String vacio.
        //
        // .split(",")[0].trim()
        //

        this.genero = CategoriaEnum.fromString(datosSerie.genero().split(",")[0].trim());
        this.actores = datosSerie.actores();
        this.sinopsis = datosSerie.sinopsis();

    }

    public List<Episodio> getEpisodioList() {return episodioList;}

    // Asignamos esta Serie como propietaria de cada Episodio (actualiza la FK),
    // y luego reemplazamos la lista interna para mantener coherencia en memoria.
    public void setEpisodioList(List<Episodio> episodioList) {
        episodioList.forEach(episodio -> episodio.setSerie(this)); // actualiza la FK (lado propietario)
        this.episodioList = episodioList;                                   // sincroniza el lado inverso en memoria
    }

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getTotalTemporadas() {return totalTemporadas;}

    public void setTotalTemporadas(Integer totalTemporadas) {
        this.totalTemporadas = totalTemporadas;
    }

    public Double getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(Double evaluacion) {
        this.evaluacion = evaluacion;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public CategoriaEnum getGenero() {
        return genero;
    }

    public void setGenero(CategoriaEnum genero) {
        this.genero = genero;
    }

    public String getActores() {
        return actores;
    }

    public void setActores(String actores) {
        this.actores = actores;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    @Override
    public String toString() {
        return
                "genero=" + genero +
                ", titulo='" + titulo + '\'' +
                ", totalTemporadas=" + totalTemporadas +
                ", evaluacion=" + evaluacion +
                ", poster='" + poster + '\'' +
                ", actores='" + actores + '\'' +
                ", sinopsis='" + sinopsis + '\'' +
                ", episodios" + episodioList;
    }
}
