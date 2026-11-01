package com.example.peliculas.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val rating: Double,
    val year: String
)

data class Genre(
    val id: Int,
    val name: String
)

data class CastMember(
    val id: Int,
    val name: String,
    val character: String,
    val photoUrl: String?
)

data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String,
    val runtimeMinutes: Int?,
    val genres: List<Genre>,
    val cast: List<CastMember>,
    val posterUrl: String?,
    val backdropUrl: String?,
    val rating: Double,
    val releaseDate: String
)

data class MoviePage(
    val movies: List<Movie>,
    val page: Int,
    val totalPages: Int
)