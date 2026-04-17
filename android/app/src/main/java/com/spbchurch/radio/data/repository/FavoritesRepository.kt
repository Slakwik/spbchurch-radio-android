package com.spbchurch.radio.data.repository

import com.spbchurch.radio.data.model.Track
import com.spbchurch.radio.data.service.FavoritesManager
import kotlinx.coroutines.flow.StateFlow

class FavoritesRepository(private val manager: FavoritesManager) {

    val favorites: StateFlow<List<Track>> = manager.favorites

    fun isFavorite(track: Track): Boolean = manager.isFavorite(track)

    fun addFavorite(track: Track) = manager.addFavorite(track)

    fun removeFavorite(track: Track) = manager.removeFavorite(track)

    fun toggleFavorite(track: Track) = manager.toggleFavorite(track)

    fun reorder(from: Int, to: Int) = manager.reorderFavorites(from, to)
}
