package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.*;
import com.aluracursos.screenmatch.repository.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String apiKey = System.getenv("OMDB_APIKEY");
    private final String API_KEY = "&apikey=" + apiKey;
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosSerie> datosSeries = new ArrayList<>();
    private List<Serie> series = new ArrayList<>();
    private Optional<Serie> serieEncontrada;
    private SerieRepository repository;

    public Principal(SerieRepository repository) {
        this.repository = repository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar series 
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                    4 - Buscar series por titulo
                    5 - Top 5 mejores Series
                    6 - Buscar series por genero
                    7 - Busque personalizada
                    8 - Buscar episodio por titulo
                    9 - Top 5 mejores episodios por serie
                                  
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    top5MejoresSeries();
                    break;
                case 6:
                    buscaSeriePorGenero();
                    break;
                case 7:
                    System.out.println("***** Filtrar por Temporadas y Evaluación *****");
                    buscarPersonalizado();
                    break;
                case 8:
                    buscaEpisodioPorTitulo();
                    break;
                case 9:
                    top5MejoresEpisodios();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }

    private DatosSerie getDatosSerie() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        System.out.println(json);
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        return datos;
    }
    private void buscarEpisodioPorSerie() {
        List<DatosTemporadas> temporadas = new ArrayList<>();
        mostrarSeriesBuscadas(); // Muestra al ususario las Series que se encuentren en la base de datos
        System.out.println("Ingrese la serie que le interesa y consultaremos sus episodios");
        var nombreSerie = teclado.nextLine(); // Almacena la Serie que el usuario desea consultar

        Optional<Serie> serie = series.stream()
                .filter(e -> e.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                .findFirst(); // Si en la base de datos esta la Serie que el usuario desea consultar
                              // El Optional almcenara la coincidencia, recoredemos que el Optional puede
                              // o no almacenar algo.

        if (serie.isPresent()){ // Si el Optional almacenó la coincidencia con la base de datos
                                // consultara al API por todos los episodios de cada temporada de la Serie
                                // realizará la deserialización y los guardara en una lista de Temporadas
            var serieEncontrada = serie.get(); //Guardo en una variable el Optional
            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoApi.obtenerDatos(URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DatosTemporadas datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
                temporadas.add(datosTemporada);
            }
            temporadas.forEach(System.out::println);
            List<Episodio> episodios = temporadas.stream()
                    .flatMap(t -> t.episodios().stream()
                            .map(e -> new Episodio(t.numero(), e)))
                    .collect(Collectors.toList()); // Aplana la lista de temporadas y las convierte en
                                                   // episodios

            episodios.forEach(System.out::println); // Muestra la lista aplanada
            serieEncontrada.setEpisodioList(episodios); // La lista de episodios de la serie encontrada
                                                        // será igual a la lista de pisodios que aplanamos
            repository.save(serieEncontrada); // Guardamos en la base de datos los episodios de la serie
        }
    }

    private void buscarSerieWeb() {
        DatosSerie datos = getDatosSerie();
//        datosSeries.add(datos);
        Serie serie = new Serie(datos);
        repository.save(serie);
        System.out.println(datos);
    }

    private void mostrarSeriesBuscadas() {
        series = repository.findAll();

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);

    }

    /*
    * - El método permite que el usuario ingrese el titulo de la serie que desea buscar
      - Luego usa la derived query para buscar en la base de datos si existe una serie con el mismo titulo
      - Por último valida si en el `Optional<Serie>` tiene o no la coincidencia, y muestra los datos al usuario
      * */
    private void buscarSeriePorTitulo() {
        System.out.println("Ingrese el titulo de la serie que desea buscar: ");
        var tituloSerie = teclado.nextLine();
        serieEncontrada = repository.findByTituloContainsIgnoreCase(tituloSerie);

        if (serieEncontrada.isPresent()){
            System.out.println("La serie con titulo '" + tituloSerie + "' si se encuentra en la base datos !!!");
            System.out.println(serieEncontrada.get());
        } else {
            System.out.println("La serie con titulo '" + tituloSerie + "' no fue encontrada");
        }
    }

    // Este método guarda en una variable las mejores 5 series y luego muestra en pantalla el top 5
    private void top5MejoresSeries() {
        List<Serie> listaDelTop5Series = repository.findTop5ByOrderByEvaluacionDesc();
        listaDelTop5Series.forEach(serie -> System.out.println("Titulo: " + serie.getTitulo() + " [" + serie.getEvaluacion() + "]"));
    }

    /*
    * - Este método recibe un input del usuario
      - Luego busca en el repositorio la Serie con ese genero (Ya casteado a Enum)
      - Ordena los resultados de mejor a peor evaluación
      - Muestra los resultados
* */
    private void buscaSeriePorGenero() {
        System.out.println("Ingrese el genero que le interesa y le mostraremos las series correspondientes a ese genero: ");
        var generoUsuario = teclado.nextLine();
        List<Serie> seriesConGenero = repository.findByGenero(CategoriaEnum.fromInput(generoUsuario));
        List<Serie> organizadas = seriesConGenero.stream()
                        .sorted(Comparator.comparing(Serie::getEvaluacion).reversed())
                .collect(Collectors.toList());

        System.out.println("Series del genero: " + generoUsuario);
        organizadas.forEach(System.out::println);
    }


    private void buscarPersonalizado() {
        System.out.println("¿Número de temporadas para filtrar?");
        var nTemporadas = teclado.nextInt();
        teclado.nextLine();
        System.out.println("¿Evaluación? (Use , entre números para declarar cifras decimales)");
        var nEvaluacion = teclado.nextDouble();
        teclado.nextLine();
        System.out.println("cargando...");
        List<Serie> cumplenPersonalizado = repository.consultarBDPorJPQL(nTemporadas, nEvaluacion);
        List<Serie> busqueda = cumplenPersonalizado.stream()
                .sorted(Comparator.comparing(Serie::getEvaluacion).reversed())
                .collect(Collectors.toList());

        busqueda.forEach(System.out::println);
    }

    private void buscaEpisodioPorTitulo() {
        System.out.println("Ingrese el titulo del episodio: ");
        var episodio = teclado.nextLine();

        List<Episodio> episodioEncontrado = repository.consultaEntreTablas(episodio);
        episodioEncontrado.forEach(e -> System.out.printf("Serie: %s | Nombre episodio: %s | Temporada: %d | Número episodio: %d%n"
                , e.getSerie().getTitulo(), e.getTitulo(), e.getTemporada(), e.getNumeroEpisodio() ));
    }

    private void top5MejoresEpisodios() {
        buscarSeriePorTitulo();
        if(serieEncontrada.isPresent()){
            List<Episodio> topEpisodios = repository.top5Episodios(serieEncontrada.get());
            topEpisodios.forEach(e -> System.out.printf("Serie: %s | Nombre episodio: %s | Temporada: %d | Número episodio: %d | Evaluacion: %.1f %n"
                    , e.getSerie().getTitulo(), e.getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getEvaluacion() ));

        }
    }


}

