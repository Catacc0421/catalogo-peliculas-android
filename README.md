# Catálogo de Películas, Android + Jetpack Compose

Aplicación Android que consume la API REST de [TMDB](https://developer.themoviedb.org/) para explorar un catálogo de películas.

## Funcionalidades
- Lista de películas populares en cuadrícula con **scroll infinito** (paginación).
- **Búsqueda por título** y **filtro por género** (con los endpoints de la API).
- **Pantalla de detalle** con sinopsis, duración, géneros y reparto.
- **Indicador de conexión** (en línea / sin conexión).
- Manejo de errores de red con botón **Reintentar**.

## Endpoints utilizados (todos GET, respuesta JSON)
| Endpoint | Uso |
|---|---|
| `/movie/popular` | Lista principal paginada |
| `/search/movie` | Búsqueda por título |
| `/discover/movie` | Filtro por género |
| `/genre/movie/list` | Lista de géneros |
| `/movie/{id}` | Detalle (con `append_to_response=credits`) |

## Arquitectura
UI (Jetpack Compose) → ViewModel → Repository → Retrofit (TMDB), con inyección de dependencias usando Hilt.

## Cómo ejecutar el proyecto
1. Clonar el repositorio y abrirlo con Android Studio.
2. Crear una cuenta gratuita en [themoviedb.org](https://www.themoviedb.org/) y obtener el **API Read Access Token** (Configuración → API).
3. Copiar `local.properties.example` como `local.properties` (o agregar la línea a un `local.properties` existente) y reemplazar el valor de `TMDB_TOKEN`.
4. Sincronizar Gradle y ejecutar la app en un emulador o dispositivo.

> La clave de la API no se incluye en el repositorio por seguridad.

## Tecnologías
Kotlin · Jetpack Compose · Retrofit · OkHttp · Hilt · Coil · Navigation Compose · Coroutines / Flow