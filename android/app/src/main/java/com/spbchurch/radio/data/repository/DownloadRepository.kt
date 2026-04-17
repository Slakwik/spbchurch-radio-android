package com.spbchurch.radio.data.repository

import com.spbchurch.radio.data.model.DownloadState
import com.spbchurch.radio.data.model.Track
import com.spbchurch.radio.data.service.DownloadManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DownloadRepository(private val manager: DownloadManager) {

    val downloadProgress: StateFlow<Map<String, Float>> = manager.downloadProgress

    private val _downloadedTracks = MutableStateFlow<List<Track>>(emptyList())
    val downloadedTracks: StateFlow<List<Track>> = _downloadedTracks.asStateFlow()

    fun refreshDownloadedTracks() {
        _downloadedTracks.value = manager.getDownloadedTrackList()
    }

    fun isDownloaded(track: Track): Boolean = manager.isDownloaded(track.url)

    fun getLocalPath(track: Track): String? = manager.getLocalPath(track.url)

    fun getDownloadState(track: Track): DownloadState = manager.getDownloadState(track)

    fun download(track: Track, onComplete: (Boolean) -> Unit = {}) {
        manager.download(track) { success ->
            if (success) refreshDownloadedTracks()
            onComplete(success)
        }
    }

    fun cancelDownload(track: Track) = manager.cancelDownload(track)

    fun deleteDownload(track: Track) {
        manager.deleteDownload(track)
        refreshDownloadedTracks()
    }

    fun getDownloadedTrackList(): List<Track> = manager.getDownloadedTrackList()

    fun backfillMetadata(tracks: List<Track>) = manager.backfillMetadata(tracks)
}
