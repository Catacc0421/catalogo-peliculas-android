package com.example.peliculas.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peliculas.data.network.ConnectivityObserver
import com.example.peliculas.domain.model.Genre
import com.example.peliculas.domain.model.Movie
import com.example.peliculas.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val movies: List<Movie> = emptyList(),
    val genres: List<Genre> = emptyList(),
    val query: String = "",
    val selectedGenreId: Int? = null,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val page: Int = 0,
    val endReached: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MovieRepository,
    connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    val isOnline: StateFlow<Boolean> = connectivityObserver.isOnline
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    private var loadJob: Job? = null
    private var searchJob: Job? = null

    init {
        loadGenres()
        refresh()
    }

    private fun loadGenres() {
        viewModelScope.launch {
            repository.getGenres().onSuccess { list ->
                _state.update { it.copy(genres = list) }
            }
        }
    }

    // Reinicia la lista desde la página 1 (al buscar o cambiar el filtro)
    private fun refresh() {
        loadJob?.cancel()
        _state.update {
            it.copy(
                movies = emptyList(),
                page = 0,
                endReached = false,
                error = null,
                isLoading = false,
                isLoadingMore = false
            )
        }
        loadNextPage()
    }

    // Se llama al llegar al final del scroll (scroll infinito)
    fun loadNextPage() {
        val current = _state.value
        if (current.isLoading || current.isLoadingMore || current.endReached) return
        if (current.error != null) return // espera a que el usuario pulse "Reintentar"

        val nextPage = current.page + 1
        _state.update {
            it.copy(isLoading = nextPage == 1, isLoadingMore = nextPage > 1)
        }

        loadJob = viewModelScope.launch {
            repository.getMovies(nextPage, current.query.trim(), current.selectedGenreId)
                .onSuccess { result ->
                    _state.update { s ->
                        s.copy(
                            movies = (s.movies + result.movies).distinctBy { m -> m.id },
                            page = result.page,
                            endReached = result.page >= result.totalPages,
                            isLoading = false,
                            isLoadingMore = false
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { s ->
                        s.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            error = e.message ?: "Error desconocido"
                        )
                    }
                }
        }
    }

    fun onQueryChange(text: String) {
        _state.update { it.copy(query = text, selectedGenreId = null) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // espera a que el usuario deje de escribir
            refresh()
        }
    }

    fun onGenreSelected(genreId: Int) {
        searchJob?.cancel()
        _state.update {
            it.copy(
                selectedGenreId = if (it.selectedGenreId == genreId) null else genreId,
                query = ""
            )
        }
        refresh()
    }

    fun retry() {
        if (_state.value.genres.isEmpty()) loadGenres()
        _state.update { it.copy(error = null) }
        loadNextPage()
    }
}