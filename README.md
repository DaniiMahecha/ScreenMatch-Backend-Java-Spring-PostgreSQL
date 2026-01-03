# 🎬 ScreenMatch

**ScreenMatch** es una aplicación backend desarrollada en **Java** con **Spring Boot** que consume la API de OMDb para gestionar información de películas y series. El proyecto implementa persistencia de datos utilizando **JPA/Hibernate** con una base de datos **PostgreSQL** y sigue una arquitectura limpia basada en el patrón **Package by Layer**.

---

## 📋 Tabla de Contenidos

- [Características](#-características)
- [Tecnologías y Dependencias](#-tecnologías-y-dependencias)
- [Arquitectura del Proyecto](#-arquitectura-del-proyecto)
- [Estructura de Paquetes](#-estructura-de-paquetes)
- [Configuración](#-configuración)
- [Endpoints de la API](#-endpoints-de-la-api)
- [Instalación y Ejecución](#-instalación-y-ejecución)

---

## ✨ Características

- 🔌 **Consumo de API externa**: Integración con OMDb API para obtener información actualizada de series y películas
- 💾 **Persistencia de datos**: Almacenamiento en PostgreSQL mediante JPA/Hibernate
- 🔐 **Seguridad**: Uso de DTOs para evitar exponer entidades JPA directamente
- 🤖 **Integración con IA**: Soporte para traducción de sinopsis con ChatGPT y Gemini
- 🌐 **API RESTful**: Endpoints organizados para consultas específicas
- 🔄 **Relaciones bidireccionales**: Manejo de relaciones OneToMany y ManyToOne entre Series y Episodios
- 📊 **Consultas avanzadas**: Implementación de Derived Queries, Native Queries y JPQL
- 🛡️ **CORS configurado**: Habilitado para comunicación con frontend

---

## 🛠️ Tecnologías y Dependencias

### Framework Principal
- **Spring Boot 3.2.0** - Framework base para el desarrollo
- **Spring Web** - Para crear API RESTful
- **Spring Data JPA** - Capa de persistencia

### Base de Datos
- **PostgreSQL** - Base de datos relacional
- **Hibernate** - ORM para mapeo objeto-relacional

### Procesamiento de Datos
- **Jackson Databind 2.16.0** - Deserialización de JSON

### Integraciones de IA
- **Google Gemini 1.0.0** - API de Google para traducción
- **OpenAI GPT-3 Java 0.14.0** - API de OpenAI para traducción

### Herramientas de Desarrollo
- **Spring Boot DevTools** - Herramientas de desarrollo
- **Maven** - Gestor de dependencias

---

## 🏗️ Arquitectura del Proyecto

El proyecto sigue el patrón **Package by Layer**, donde cada capa tiene una responsabilidad clara y bien definida:

```
com.aluracursos.screenmatch
├── config/          # Configuraciones (CORS, etc.)
├── controller/      # Controladores REST
├── dto/             # Data Transfer Objects
├── model/           # Entidades JPA y Records
├── repository/      # Repositorios JPA
├── service/         # Lógica de negocio
└── principal/       # Clase principal (consola)
```

---

## 📦 Estructura de Paquetes

### 📁 **Model** - Capa de Modelo

Contiene **6 archivos** organizados en 3 categorías:

#### **Records (DTOs de API)** 🔖
Implementan el patrón **Data Transfer Object** para mapear respuestas de la API OMDb:

- **`DatosSerie`**: Mapea información general de series (título, temporadas, evaluación, género, actores, sinopsis)
- **`DatosTemporadas`**: Mapea información de temporadas con sus episodios
- **`DatosEpisodio`**: Mapea información individual de episodios (título, número, evaluación, fecha)

✅ **Ventaja**: Evitan exponer directamente las entidades JPA, proporcionando una capa de seguridad adicional.

#### **Entidades JPA** 🗄️
Clases que representan tablas en la base de datos:

- **`Serie`**: Entidad principal que almacena información de series
  - Relación **OneToMany** con Episodio
  - Método `setEpisodioList()` que actualiza la Foreign Key del lado propietario manteniendo coherencia en memoria
  
- **`Episodio`**: Entidad que almacena información de episodios
  - Relación **ManyToOne** con Serie
  - Maneja conversión de datos y validación de fechas

#### **Enum** 🎭
- **`CategoriaEnum`**: Gestiona categorías de series de forma robusta
  - Define categorías: ACCION, ROMANCE, COMEDIA, DRAMA, CRIMEN, AVENTURA
  - Métodos de conversión:
    - `fromString()`: Convierte String de OMDb API → Enum
    - `fromInput()`: Convierte input de usuario → Enum
    - `fromFront()`: Convierte petición del frontend → Enum

---

### 📁 **DTO** - Data Transfer Objects

Contiene **2 DTOs** para respuestas al cliente:

- **`SerieDTO`**: Expone datos de serie al frontend sin revelar la entidad JPA completa
- **`EpisodioDTO`**: Expone datos de episodios de forma controlada

✅ **Ventaja**: Mayor seguridad al no exponer toda la estructura interna de las entidades.

---

### 📁 **Service** - Capa de Servicio

Contiene la **lógica de negocio** del proyecto:

#### Servicios principales:

- **`SerieService`**: 
  - Métodos para obtener series (Top 5, más recientes, por género, por ID)
  - Método reutilizable `convertirDatos()` que transforma entidades JPA → DTOs
  - Gestión de episodios por serie y temporada

- **`ConsumoAPI`**: 
  - Consume la API de OMDb usando `HttpClient`
  - Manejo de peticiones HTTP

- **`ConvierteDatos`**: 
  - Implementa interfaz `IConvierteDatos`
  - Deserialización genérica de JSON con Jackson
  - Flexibilidad para reutilizar en diferentes tipos de datos

- **`ConsultaGemini`**: 
  - Integración con API de Google Gemini
  - Traducción de sinopsis al español

- **`ConsultaChatGPT`**: 
  - Integración con API de OpenAI
  - Traducción usando GPT-3.5

---

### 📁 **Repository** - Capa de Persistencia

**`SerieRepository`** extiende `JpaRepository<Serie, Long>` y proporciona métodos de consulta:

#### Tipos de consultas implementadas:

1. **JPA Derived Queries** (Spring genera automáticamente):
   ```java
   Optional<Serie> findByTituloContainsIgnoreCase(String tituloSerie);
   List<Serie> findTop5ByOrderByEvaluacionDesc();
   List<Serie> findByGenero(CategoriaEnum genero);
   ```

2. **JPQL** (Java Persistence Query Language):
   ```java
   @Query("SELECT s FROM Serie s WHERE s.totalTemporadas <= :totalTemporadas 
           AND s.evaluacion >= :evaluacion")
   List<Serie> consultarBDPorJPQL(Integer totalTemporadas, Double evaluacion);
   ```

3. **JPQL con JOIN** (consultas entre tablas relacionadas):
   ```java
   // Buscar episodios por título
   @Query("SELECT e FROM Serie s JOIN s.episodioList e 
           WHERE e.titulo ILIKE %:nombreEpisodio%")
   List<Episodio> consultaEntreTablas(String nombreEpisodio);
   
   // Top 5 episodios de una serie
   @Query("SELECT e FROM Serie s JOIN s.episodioList e WHERE s = :serie 
           ORDER BY e.evaluacion DESC LIMIT 5")
   List<Episodio> top5Episodios(Serie serie);
   
   // Series con lanzamientos más recientes
   @Query("SELECT s FROM Serie s JOIN s.episodioList e GROUP BY s 
           ORDER BY MAX(e.fechaDeLanzamiento) DESC LIMIT 5")
   List<Serie> lanzamientosMasRecientes();
   ```

---

### 📁 **Controller** - Capa de Controladores

**`SerieController`** maneja las peticiones HTTP del frontend:

```java
@RestController
@RequestMapping("/series")
```

#### Endpoints disponibles:
- `GET /series` - Obtiene todas las series
- `GET /series/top5` - Top 5 series mejor evaluadas
- `GET /series/lanzamientos` - Series más recientes
- `GET /series/{id}` - Serie específica por ID
- `GET /series/{id}/temporadas/todas` - Todos los episodios de una serie
- `GET /series/{id}/temporadas/{temporada}` - Episodios de una temporada específica
- `GET /series/categoria/{genero}` - Series filtradas por género

✅ Inyecta `SerieService` para delegación de lógica de negocio.

---

### 📁 **Config** - Configuración

**`CorsConfiguration`** implementa `WebMvcConfigurer`:

```java
@Configuration
public class CorsConfiguration implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://127.0.0.1:5500")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", 
                                "HEAD", "TRACE", "CONNECT");
    }
}
```

#### ¿Qué es CORS?
**CORS** (Cross-Origin Resource Sharing) actúa como una **muralla de seguridad** que controla qué sitios pueden acceder a tu API. Por defecto, los navegadores bloquean peticiones entre diferentes orígenes (dominios, puertos o protocolos) para prevenir ataques maliciosos.

✅ Esta configuración "abre una puerta autorizada" permitiendo que el frontend en `http://127.0.0.1:5500` se comunique con el backend sin ser bloqueado.

---

## ⚙️ Configuración

### Variables de Entorno

El proyecto usa **variables de entorno** para proteger información sensible:

```properties
# application.properties
spring.datasource.url=jdbc:postgresql://${DB_HOST}/${DB_NAME}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
```

### Configuración requerida:

Crea las siguientes variables de entorno en tu sistema:

```bash
# Base de datos PostgreSQL
DB_HOST=localhost:5432
DB_NAME=screenmatch
DB_USER=tu_usuario
DB_PASSWORD=tu_contraseña

# API de OMDb
OMDB_APIKEY=tu_api_key
```

### Configuración de JPA/Hibernate:

```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

---

## 🔌 Endpoints de la API

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/series` | Obtiene todas las series almacenadas |
| `GET` | `/series/top5` | Top 5 series mejor evaluadas |
| `GET` | `/series/lanzamientos` | 5 series con lanzamientos más recientes |
| `GET` | `/series/{id}` | Obtiene una serie específica |
| `GET` | `/series/{id}/temporadas/todas` | Todos los episodios de una serie |
| `GET` | `/series/{id}/temporadas/{temporada}` | Episodios de una temporada específica |
| `GET` | `/series/categoria/{genero}` | Series filtradas por género |

### Géneros disponibles:
- `accion` / `acción`
- `romance`
- `comedia`
- `drama`
- `crimen`
- `aventura`

---

## 🚀 Instalación y Ejecución

### Prerrequisitos

- **Java 17** o superior
- **Maven** 3.6+
- **PostgreSQL** 12+
- Clave API de **OMDb** ([obtener aquí](http://www.omdbapi.com/apikey.aspx))

### Pasos de instalación:

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/tu-usuario/screenmatch.git
   cd screenmatch
   ```

2. **Configurar base de datos PostgreSQL**
   ```sql
   CREATE DATABASE screenmatch;
   ```

3. **Configurar variables de entorno**
   
   En Linux/Mac:
   ```bash
   export DB_HOST=localhost:5432
   export DB_NAME=screenmatch
   export DB_USER=tu_usuario
   export DB_PASSWORD=tu_contraseña
   export OMDB_APIKEY=tu_api_key
   ```
   
   En Windows (CMD):
   ```cmd
   set DB_HOST=localhost:5432
   set DB_NAME=screenmatch
   set DB_USER=tu_usuario
   set DB_PASSWORD=tu_contraseña
   set OMDB_APIKEY=tu_api_key
   ```

4. **Instalar dependencias**
   ```bash
   mvn clean install
   ```

5. **Ejecutar la aplicación**
   ```bash
   mvn spring-boot:run
   ```

6. **Acceder a la API**
   
   La aplicación estará disponible en: `http://localhost:8088`

---

## 📝 Notas del Desarrollo

Este proyecto fue desarrollado como parte de mi aprendizaje en el desarrollo backend con Java y Spring Boot, enfocándome en:

- ✅ Arquitectura limpia y separación de responsabilidades
- ✅ Buenas prácticas con JPA/Hibernate
- ✅ Manejo de relaciones bidireccionales
- ✅ Seguridad mediante DTOs
- ✅ Consultas avanzadas con JPQL
- ✅ Integración con APIs externas
- ✅ Configuración de CORS para aplicaciones web

---


## 👤 Daniel Felipe Mahecha Peña

Desarrollado con 💙 durante mi formación en desarrollo backend con Java y Spring Boot.

---

## 🙏 Agradecimientos

- [OMDb API](http://www.omdbapi.com/) - Por proporcionar datos de películas y series
- [Alura Latam](https://www.aluracursos.com/) - Por la formación en desarrollo backend
- Spring Boot Community - Por la excelente documentación

---

⭐ Si este proyecto te fue útil, no olvides darle una estrella en GitHub
