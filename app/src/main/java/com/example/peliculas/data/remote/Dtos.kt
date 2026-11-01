package com.example.peliculas.data.remote

import com.google.gson.annotations.SerializedName

data class MoviePageDto(
    val page: Int,
    val results: List<MovieDto>,
    @SerializedName("total_pages") val totalPages: Int
)

data class MovieDto(
    val id: Int,
    val title: String,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("release_date") val releaseDate: String?
)

data class GenreDto(
    val id: Int,
    val name: String
)

data class GenreListDto(
    val genres: List<GenreDto>
)

data class CastDto(
    val id: Int,
    val name: String,
    val character: String?,
    @SerializedName("profile_path") val profilePath: String?
)

data class CreditsDto(
    val cast: List<CastDto>
)

data class MovieDetailDto(
    val id: Int,
    val title: String,
    val overview: String?,
    val runtime: Int?,
    val genres: List<GenreDto>,
    val credits: CreditsDto?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("release_date") val releaseDate: String?
)