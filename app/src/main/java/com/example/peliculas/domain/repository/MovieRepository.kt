package com.example.peliculas.domain.repository

import com.example.peliculas.domain.model.Genre
import com.example.peliculas.domain.model.MovieDetail
import com.example.peliculas.domain.model.MoviePage

interface MovieRepository {
    suspend fun getMovies(page: Int, query: String, genreId: Int?): Result<MoviePage>
    suspend fun getGenres(): Result<List<Genre>>
    suspend fun getMovieDetail(id: Int): Result<MovieDetail>
}