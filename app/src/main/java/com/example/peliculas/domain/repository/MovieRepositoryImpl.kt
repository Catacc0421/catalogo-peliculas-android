package com.example.peliculas.data.repository

import com.example.peliculas.data.remote.CastDto
import com.example.peliculas.data.remote.GenreDto
import com.example.peliculas.data.remote.MovieDetailDto
import com.example.peliculas.data.remote.MovieDto
import com.example.peliculas.data.remote.MoviePageDto
import com.example.peliculas.data.remote.TmdbApi
import com.example.peliculas.domain.model.CastMember
import com.example.peliculas.domain.model.Genre
import com.example.peliculas.domain.model.Movie
import com.example.peliculas.domain.model.MovieDetail
import com.example.peliculas.domain.model.MoviePage
import com.example.peliculas.domain.repository.MovieRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

private const val IMAGE_BASE = "https://image.tmdb.org/t/p/w500"
private const val BACKDROP_BASE = "https://image.tmdb.org/t/p/w780"

class MovieRepositoryImpl @Inject constructor(
    private val api: TmdbApi
) : MovieRepository {

    override suspend fun getMovies(page: Int, query: String, genreId: Int?): Result<MoviePage> =
        safeCall {
            val dto = when {
                query.isNotBlank() -> api.searchMovies(query = query, page = page)
                genreId != null -> api.discoverByGenre(genreId = genreId, page = page)
                else -> api.getPopular(page = page)
            }
            dto.toDomain()
        }

    override suspend fun getGenres(): Result<List<Genre>> =
        safeCall { api.getGenres().genres.map { it.toDomain() } }

    override suspend fun getMovieDetail(id: Int): Result<MovieDetail> =
        safeCall { api.getMovieDetail(id).toDomain() }

    // Convierte los errores técnicos en mensajes entendibles para el usuario
    private suspend fun <T> safeCall(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: IOException) {
        Result.failure(Exception("No hay conexión a internet. Revisa tu red e inténtalo de nuevo."))
    } catch (e: HttpException) {
        val msg = when (e.code()) {
            401 -> "Token de TMDB inválido. Revisa local.properties."
            404 -> "No se encontró lo que buscabas."
            429 -> "Demasiadas peticiones. Espera un momento."
            else -> "Error del servidor (${e.code()})."
        }
        Result.failure(Exception(msg))
    } catch (e: Exception) {
        Result.failure(Exception("Ocurrió un error inesperado."))
    }
}

// ---------- Mapeo de DTO (API) a modelos del dominio ----------

private fun MoviePageDto.toDomain() = MoviePage(
    movies = results.map { it.toDomain() },
    page = page,
    totalPages = totalPages
)

private fun MovieDto.toDomain() = Movie(
    id = id,
    title = title,
    overview = overview.orEmpty(),
    posterUrl = posterPath?.let { "$IMAGE_BASE$it" },
    rating = voteAverage ?: 0.0,
    year = releaseDate?.take(4).orEmpty()
)

private fun GenreDto.toDomain() = Genre(id = id, name = name)

private fun CastDto.toDomain() = CastMember(
    id = id,
    name = name,
    character = character.orEmpty(),
    photoUrl = profilePath?.let { "$IMAGE_BASE$it" }
)

private fun MovieDetailDto.toDomain() = MovieDetail(
    id = id,
    title = title,
    overview = overview.orEmpty(),
    runtimeMinutes = runtime,
    genres = genres.map { it.toDomain() },
    cast = credits?.cast.orEmpty().take(15).map { it.toDomain() },
    posterUrl = posterPath?.let { "$IMAGE_BASE$it" },
    backdropUrl = backdropPath?.let { "$BACKDROP_BASE$it" },
    rating = voteAverage ?: 0.0,
    releaseDate = releaseDate.orEmpty()
)