package com.spbchurch.radio.data.repository

import com.spbchurch.radio.data.model.SortOrder
import com.spbchurch.radio.data.model.Track
import com.spbchurch.radio.data.service.TrackListService

class TrackRepository {

    private val service = TrackListService()
    private var cachedTracks: List<Track>? = null

    suspend fun getTracks(forceRefresh: Boolean = false): Result<List<Track>> {
        if (!forceRefresh && cachedTracks != null) {
            return Result.success(cachedTracks!!)
        }

        val result = service.fetchTracks()
        if (result.isSuccess) {
            cachedTracks = result.getOrNull()
        }
        return result
    }

    fun getCachedTracks(): List<Track>? = cachedTracks

    fun searchTracks(tracks: List<Track>, query: String): List<Track> {
        if (query.isBlank()) return tracks
        val lowerQuery = query.lowercase()
        return tracks.filter { it.title.lowercase().contains(lowerQuery) }
    }

    fun sortTracks(tracks: List<Track>, sortOrder: SortOrder): List<Track> {
        return when (sortOrder) {
            SortOrder.DEFAULT -> tracks
            SortOrder.A_TO_Z -> tracks.sortedBy { it.title.lowercase() }
            SortOrder.Z_TO_A -> tracks.sortedByDescending { it.title.lowercase() }
        }
    }
}
