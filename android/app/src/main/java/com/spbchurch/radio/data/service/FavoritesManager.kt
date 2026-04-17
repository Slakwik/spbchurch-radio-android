package com.spbchurch.radio.data.service

import android.content.Context
import com.spbchurch.radio.data.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class FavoritesManager(private val context: Context) {

    private val favoritesFile: File by lazy {
        File(context.filesDir, "favorites.json")
    }

    private val json = Json { ignoreUnknownKeys = true }

    private val _favorites = MutableStateFlow<List<Track>>(emptyList())
    val favorites: StateFlow<List<Track>> = _favorites.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        try {
            if (favoritesFile.exists()) {
                val content = favoritesFile.readText()
                val tracks = json.decodeFromString<List<Track>>(content)
                _favorites.value = tracks
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveFavorites() {
        try {
            favoritesFile.writeText(json.encodeToString(_favorites.value))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isFavorite(track: Track): Boolean {
        return _favorites.value.any { it.url == track.url }
    }

    fun addFavorite(track: Track) {
        if (!isFavorite(track)) {
            _favorites.value = _favorites.value + track.copy(isFavorite = true)
            saveFavorites()
        }
    }

    fun removeFavorite(track: Track) {
        _favorites.value = _favorites.value.filter { it.url != track.url }
        saveFavorites()
    }

    fun toggleFavorite(track: Track) {
        if (isFavorite(track)) {
            removeFavorite(track)
        } else {
            addFavorite(track)
        }
    }

    fun reorderFavorites(from: Int, to: Int) {
        val list = _favorites.value.toMutableList()
        val item = list.removeAt(from)
        list.add(to, item)
        _favorites.value = list
        saveFavorites()
    }

    fun updateTrack(track: Track) {
        _favorites.value = _favorites.value.map {
            if (it.url == track.url) track.copy(isFavorite = true) else it
        }
        saveFavorites()
    }
}
