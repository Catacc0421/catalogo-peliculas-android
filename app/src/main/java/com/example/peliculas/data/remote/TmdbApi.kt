package com.example.peliculas.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET("movie/popular")
    suspend fun getPopular(
        @Query("page") page: Int,
        @Query("language") language: String = "es-ES"
    ): MoviePageDto

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("language") language: String = "es-ES"
    ): MoviePageDto

    @GET("discover/movie")
    suspend fun discoverByGenre(
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int,
        @Query("language") language: String = "es-ES"
    ): MoviePageDto

    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("language") language: String = "es-ES"
    ): GenreListDto

    @GET("movie/{id}")
    suspend fun getMovieDetail(
        @Path("id") id: Int,
        @Query("append_to_response") append: String = "credits",
        @Query("language") language: String = "es-ES"
    ): MovieDetailDto
}