package com.aluracursos.screenmatch.repository;
import com.aluracursos.screenmatch.model.CategoriaEnum;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findByTituloContainsIgnoreCase(String tituloSerie); // Buscar serie por nombre
    List<Serie> findTop5ByOrderByEvaluacionDesc(); // Top 5 mejores series teniendo en cuenta su calificación
    List<Serie> findByGenero(CategoriaEnum genero); // Buscar Series por categoria
    Optional<Serie> findById(Long id); // Buscar Serie por id
    //JPA Derived Query
//    List<Serie> findByTotalTemporadasLessThanEqualAndEvaluacionGreaterThanEqual(Integer numeroTemporadas, Double evaluacion);

    //JPA Native Query
//    @Query( value = "SELECT * FROM series WHERE serie.total_temporadas <= 6 AND series.evaluacion >= 7.5" , nativeQuery = true )
//    List<Serie> consultatBDPorNativeQuery();

    // JPQL
        /*
    Elementos clave

    - `s` → alias de la entidad `Serie`.
    - `s.totalTemporadas` → atributo de la clase, no columna.
    - `:totalTemporadas` → parámetro nombrado para inyección dinámica.
    * */

    @Query(value = "SELECT s FROM Serie s WHERE s.totalTemporadas <= :totalTemporadas AND s.evaluacion >= :evaluacion")
    List<Serie> consultarBDPorJPQL(Integer totalTemporadas, Double evaluacion);

    //JPQL Relacionando datos entre tablas Buscar un episodio por nombre
    /*
    * Tabla resumen de elementos JPQL
    | ---------- | ---------------------------------------------------------------------------------- |
    | Elemento   |                              Descripción breve                                     |
    | ---------- | ---------------------------------------------------------------------------------- |
    | **SELECT** | Indica qué tipo de objeto o campos deben devolverse en el resultado.               |
    | **FROM**   | Establece la entidad principal desde la cual inicia la consulta. Crea un alias.    |
    | **JOIN**   | Une entidades relacionadas usando sus asociaciones (OneToMany, ManyToOne, etc.).   |
    | **WHERE**  | Aplica condiciones o filtros sobre los datos después del JOIN.                     |
    | **ILIKE**  | Comparación de texto insensible a mayúsculas/minúsculas; permite patrones con `%`. |
    | ---------- | ---------------------------------------------------------------------------------- |
    * */
    @Query(value = "SELECT e FROM Serie s JOIN s.episodioList e WHERE e.titulo ILIKE %:nombreEpisodio%")
    List<Episodio> consultaEntreTablas(String nombreEpisodio);


    /*
    * Tabla resumen para referencia rápida
    * | ------------------------------ | ------------------------------------------------------------------ |
    | Elemento                       |                      Descripción breve                             |
    | ------------------------------ | ------------------------------------------------------------------ |
    | **SELECT e**                   | Devuelve episodios, no series.                                     |
    | **FROM Serie s**               | Define a Serie como entidad principal.                             |
    | **JOIN s.episodioList e**      | Obtiene los episodios relacionados con la Serie.                   |
    | **WHERE s = :serie**           | Filtra solo la Serie pasada como parámetro.                        |
    | **ORDER BY e.evaluacion DESC** | Ordena episodios por evaluación de mayor a menor.                  |
    | **LIMIT 5**                    | Devuelve únicamente los primeros 5 episodios tras el ordenamiento. |
    | ------------------------------ | ------------------------------------------------------------------ |
    * */
    @Query(value = "SELECT e FROM Serie s JOIN s.episodioList e WHERE s = :serie ORDER BY e.evaluacion DESC LIMIT 5")
    List<Episodio> top5Episodios(Serie serie);

    /*
     * Tabla resumen para referencia rápida
     * | -------------------------------------- | ------------------------------------------------------------------------------ |
     * | Elemento                               | Descripción breve                                                              |
     * | -------------------------------------- | ------------------------------------------------------------------------------ |
     * | **SELECT s**                           | Devuelve objetos de tipo Serie.                                                |
     * | **FROM Series s**                      | Define a Series (o Serie) como la entidad principal de la consulta.            |
     * | **JOIN s.episodios e**                 | Relaciona cada Serie con su lista de Episodios para acceder a sus atributos.   |
     * | **GROUP BY s**                         | Agrupa los resultados por Serie; necesario al usar funciones agregadas.        |
     * | **MAX(e.fechaDeLanzamiento)**          | Obtiene la fecha más reciente entre los episodios asociados a cada Serie.       |
     * | **ORDER BY MAX(...) DESC**             | Ordena las Series por su episodio más reciente, de más nuevo a más antiguo.     |
     * | **LIMIT 5**                             | Devuelve únicamente las 5 Series con lanzamientos más recientes.                |
     * | -------------------------------------- | ------------------------------------------------------------------------------ |
     * */
    @Query(value = "SELECT s FROM Serie s " +
            "JOIN s.episodioList e " +
            "GROUP BY s " +
            "ORDER BY MAX(e.fechaDeLanzamiento) DESC LIMIT 5")
    List<Serie> lanzamientosMasRecientes();

    @Query(value = "SELECT e FROM Serie s JOIN s.episodioList e WHERE s.id = :id AND e.temporada = :temporada")
    List<Episodio> todosLosEpisodiosPorTemporada(Long id, Integer temporada); // <-- Explic esto





}
