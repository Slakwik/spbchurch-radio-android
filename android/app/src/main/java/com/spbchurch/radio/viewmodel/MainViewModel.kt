package com.spbchurch.radio.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.spbchurch.radio.RadioApplication
import com.spbchurch.radio.data.model.*
import com.spbchurch.radio.data.repository.TrackRepository
import com.spbchurch.radio.data.service.DownloadManager
import com.spbchurch.radio.data.service.FavoritesManager
import com.spbchurch.radio.data.service.FilePlayerService
import com.spbchurch.radio.data.service.RadioStreamService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as RadioApplication

    private val radioStreamService = app.radioStreamService
    private val filePlayerService = FilePlayerService(application)
    private val trackRepository = app.trackRepository
    private val downloadManager = app.downloadManager
    private val favoritesManager = app.favoritesManager

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    private val _filteredTracks = MutableStateFlow<List<Track>>(emptyList())
    val filteredTracks: StateFlow<List<Track>> = _filteredTracks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.DEFAULT)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    val favorites: StateFlow<List<Track>> = favoritesManager.favorites

    val downloadProgress: StateFlow<Map<String, Float>> = downloadManager.downloadProgress

    private var currentQueue: List<Track> = emptyList()

    init {
        initialize()
        observeRadioStream()
        observeFilePlayer()
    }

    private fun initialize() {
        radioStreamService.initialize()
        filePlayerService.initialize()

        viewModelScope.launch {
            loadTracks()
        }
    }

    private fun observeRadioStream() {
        viewModelScope.launch {
            combine(
                radioStreamService.isPlaying,
                radioStreamService.isBuffering,
                radioStreamService.radioMetadata
            ) { isPlaying, isBuffering, metadata ->
                _playbackState.value.copy(
                    isPlaying = isPlaying,
                    isBuffering = isBuffering,
                    currentTitle = metadata.title,
                    isRadioMode = true
                )
            }.collect { state ->
                _playbackState.value = state
            }
        }
    }

    private fun observeFilePlayer() {
        viewModelScope.launch {
            filePlayerService.currentTrack.collect { track ->
                updatePlaybackState(track)
            }
        }

        viewModelScope.launch {
            filePlayerService.isPlaying.collect { isPlaying ->
                updatePlaybackState(_playbackState.value.currentTrack)
            }
        }

        viewModelScope.launch {
            filePlayerService.isBuffering.collect { isBuffering ->
                _playbackState.value = _playbackState.value.copy(isBuffering = isBuffering)
            }
        }

        viewModelScope.launch {
            filePlayerService.position.collect { position ->
                _playbackState.value = _playbackState.value.copy(
                    position = position,
                    progress = if (_playbackState.value.duration > 0) 
                        position.toFloat() / _playbackState.value.duration else 0f
                )
            }
        }

        viewModelScope.launch {
            filePlayerService.duration.collect { duration ->
                _playbackState.value = _playbackState.value.copy(
                    duration = duration,
                    progress = if (duration > 0) 
                        _playbackState.value.position.toFloat() / duration else 0f
                )
            }
        }

        viewModelScope.launch {
            filePlayerService.playbackOrder.collect { order ->
                _playbackState.value = _playbackState.value.copy(playbackOrder = order)
            }
        }

        filePlayerService.setOnTrackEnded {
            if (_playbackState.value.playbackOrder != PlaybackOrder.PLAY_ONCE) {
                nextTrack()
            }
        }

        filePlayerService.setOnNextTrack { track ->
            currentQueue = currentQueue.dropWhile { it.url != track.url }
        }
    }

    private fun updatePlaybackState(track: Track?) {
        _playbackState.value = _playbackState.value.copy(
            currentTrack = track,
            currentTitle = track?.title ?: "",
            isRadioMode = false
        )
    }

    fun loadTracks() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            trackRepository.getTracks().fold(
                onSuccess = { loadedTracks ->
                    _tracks.value = loadedTracks
                    downloadManager.backfillMetadata(loadedTracks)
                    updateFilteredTracks()
                },
                onFailure = { e ->
                    _error.value = e.message ?: "Ошибка загрузки"
                }
            )

            _isLoading.value = false
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        updateFilteredTracks()
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
        updateFilteredTracks()
    }

    private fun updateFilteredTracks() {
        val query = _searchQuery.value
        val sortOrder = _sortOrder.value
        val allTracks = _tracks.value

        val searched = trackRepository.searchTracks(allTracks, query)
        _filteredTracks.value = trackRepository.sortTracks(searched, sortOrder)
    }

    fun toggleRadioPlayback() {
        if (_playbackState.value.isRadioMode && _playbackState.value.isPlaying) {
            radioStreamService.stop()
        } else {
            radioStreamService.play()
        }
    }

    fun playRadio() {
        radioStreamService.play()
    }

    fun stopRadio() {
        radioStreamService.stop()
    }

    fun playTrack(track: Track, queue: List<Track>? = null) {
        val playbackQueue = queue ?: _filteredTracks.value
        currentQueue = playbackQueue
        filePlayerService.play(track, playbackQueue)
    }

    fun togglePlayPause() {
        if (_playbackState.value.isRadioMode) {
            toggleRadioPlayback()
        } else {
            if (_playbackState.value.isPlaying) {
                filePlayerService.pause()
            } else {
                filePlayerService.resume()
            }
        }
    }

    fun nextTrack() {
        filePlayerService.nextTrack()
    }

    fun previousTrack() {
        filePlayerService.previousTrack()
    }

    fun seekTo(position: Long) {
        filePlayerService.seekTo(position)
    }

    fun setPlaybackOrder(order: PlaybackOrder) {
        filePlayerService.setPlaybackOrder(order)
    }

    fun toggleFavorite(track: Track) {
        favoritesManager.toggleFavorite(track)
    }

    fun isFavorite(track: Track): Boolean {
        return favoritesManager.isFavorite(track)
    }

    fun getDownloadState(track: Track): DownloadState {
        return downloadManager.getDownloadState(track)
    }

    fun getDownloadProgress(track: Track): Float {
        return downloadProgress.value[track.id] ?: 0f
    }

    fun downloadTrack(track: Track) {
        downloadManager.download(track)
    }

    fun cancelDownload(track: Track) {
        downloadManager.cancelDownload(track)
    }

    fun deleteDownload(track: Track) {
        downloadManager.deleteDownload(track)
    }

    fun getDownloadedTracks(): List<Track> {
        return downloadManager.getDownloadedTrackList()
    }

    fun findInLibrary(title: String): Track? {
        return _tracks.value.find {
            it.title.contains(title, ignoreCase = true) ||
                    title.contains(it.title, ignoreCase = true)
        }
    }

    override fun onCleared() {
        super.onCleared()
        radioStreamService.release()
        filePlayerService.release()
    }
}
