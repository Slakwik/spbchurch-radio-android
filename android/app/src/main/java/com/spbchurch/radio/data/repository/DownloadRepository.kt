package com.spbchurch.radio.data.repository

import com.spbchurch.radio.data.model.DownloadState
import com.spbchurch.radio.data.model.Track
import com.spbchurch.radio.data.service.DownloadManager
import kotlinx.coroutines.flow.StateFlow

class DownloadRepository(private val manager: DownloadManager) {

    val downloadProgress: StateFlow<Map<String, Float>> = manager.downloadProgress
    val downloadedTracks: StateFlow<List<Track>> = kotlinx.coroutines.flow.flow {
        emit(manager.getDownloadedTrackList())
    }.let { flow ->
        kotlinx.coroutines.flow.combine(
            kotlinx.coroutines.flow.MutableStateFlow(Unit)
        ) { _, _ -> manager.getDownloadedTrackList() }
    }

    fun isDownloaded(track: Track): Boolean = manager.isDownloaded(track.url)

    fun getLocalPath(track: Track): String? = manager.getLocalPath(track.url)

    fun getDownloadState(track: Track): DownloadState = manager.getDownloadState(track)

    fun download(track: Track, onComplete: (Boolean) -> Unit = {}) = manager.download(track, onComplete)

    fun cancelDownload(track: Track) = manager.cancelDownload(track)

    fun deleteDownload(track: Track) = manager.deleteDownload(track)

    fun getDownloadedTrackList(): List<Track> = manager.getDownloadedTrackList()

    fun backfillMetadata(tracks: List<Track>) = manager.backfillMetadata(tracks)
}
