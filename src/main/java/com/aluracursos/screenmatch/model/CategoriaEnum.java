package com.aluracursos.screenmatch.model;

public enum CategoriaEnum {
    ACCION("Action", "Acción", "acc%C3%B3n"),
    ROMANCE("Romance", "Romance", "romance"),
    COMEDIA("Comedy", "Comedia", "comedia"),
    DRAMA("Drama", "Drama", "drama"),
    CRIMEN("Crime", "Crimen", "crimen"),
    AVENTURA("Adventure", "Aventura", "aventura");

    private String categoriaOmdb;
    private String inputUsuario;
    private String peticionDelFront;

    CategoriaEnum(String categoriaOmdb, String inputUsuario, String peticionDelFront){
        this.categoriaOmdb = categoriaOmdb;
        this.inputUsuario = inputUsuario;
        this.peticionDelFront = peticionDelFront;
    }

    // Se realiza el Cast del String del API a alguna de las categorías del Enum
    public static CategoriaEnum fromString(String text){
        // Estamos recorriendo con "categoria" todos los valores (Constantes) del Enum
        for (CategoriaEnum categoria : CategoriaEnum.values()){
            // Sí categoria.categoriaOmdb es igual a text, retornara el valor (Constante)
            // que corresponde a esa categoría.
            if (categoria.categoriaOmdb.equalsIgnoreCase(text)){
                return categoria;
            }
        }
        //De lo contrarió lanzará una excepción:
        throw new IllegalArgumentException("Ninguna categoria encontrada: " + text);
    }

    // Se realiza el Cast del String del input del Usuario en nuestra clase principal de las categorías del Enum
    public static CategoriaEnum fromInput(String text){
        // Estamos recorriendo con "categoria" todos los valores (Constantes) del Enum
        for (CategoriaEnum categoria : CategoriaEnum.values()){
            // Sí categoria.categoriaOmdb es igual a text, retornara el valor (Constante)
            // que corresponde a esa categoría.
            if (categoria.inputUsuario.equalsIgnoreCase(text)){
                return categoria;
            }
        }
        //De lo contrarió lanzará una excepción:
        throw new IllegalArgumentException("Ninguna categoria encontrada: " + text);
    }

    // Se realiza el Cast del String de la petición GET del front end de alguna de las categorías del Enum
    public static CategoriaEnum fromFront(String peticionDelFront){
        // Estamos recorriendo con "categoria" todos los valores (Constantes) del Enum
        for (CategoriaEnum categoria : CategoriaEnum.values()){
            // Sí categoria.categoriaOmdb es igual a text, retornara el valor (Constante)
            // que corresponde a esa categoría.
            if (categoria.inputUsuario.equalsIgnoreCase(peticionDelFront)){
                return categoria;
            }
        }
        //De lo contrarió lanzará una excepción:
        throw new IllegalArgumentException("Ninguna categoria encontrada: " + peticionDelFront);
    }


}
