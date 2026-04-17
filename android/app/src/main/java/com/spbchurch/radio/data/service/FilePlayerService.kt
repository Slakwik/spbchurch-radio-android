package com.spbchurch.radio.data.service

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.spbchurch.radio.data.model.PlaybackOrder
import com.spbchurch.radio.data.model.Track
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class FilePlayerService(private val context: Context) {

    private var player: ExoPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering: StateFlow<Boolean> = _isBuffering.asStateFlow()

    private val _position = MutableStateFlow(0L)
    val position: StateFlow<Long> = _position.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _playbackOrder = MutableStateFlow(PlaybackOrder.SHUFFLE)
    val playbackOrder: StateFlow<PlaybackOrder> = _playbackOrder.asStateFlow()

    private var playlist: List<Track> = emptyList()
    private var currentIndex: Int = -1
    private var positionUpdateJob: Job? = null

    private var onTrackEnded: (() -> Unit)? = null
    private var onNextTrack: ((Track) -> Unit)? = null

    fun initialize() {
        if (player == null) {
            player = ExoPlayer.Builder(context).build().apply {
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        _isBuffering.value = state == Player.STATE_BUFFERING
                        if (state == Player.STATE_ENDED) {
                            handleTrackEnded()
                        }
                    }

                    override fun onIsPlayingChanged(playing: Boolean) {
                        _isPlaying.value = playing
                        if (playing) {
                            startPositionUpdates()
                        } else {
                            stopPositionUpdates()
                        }
                    }
                })
            }
        }
    }

    fun play(track: Track, queue: List<Track> = listOf(track)) {
        initialize()
        playlist = queue
        currentIndex = queue.indexOf(track).takeIf { it >= 0 } ?: 0

        val mediaUrl = when {
            track.localPath != null && File(track.localPath).exists() -> track.localPath
            else -> track.url
        }

        val mediaItem = MediaItem.Builder()
            .setUri(mediaUrl)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(track.title)
                    .build()
            )
            .build()

        player?.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }

        _currentTrack.value = track
    }

    fun pause() {
        player?.pause()
    }

    fun resume() {
        player?.play()
    }

    fun stop() {
        player?.stop()
        _isPlaying.value = false
        _currentTrack.value = null
    }

    fun seekTo(position: Long) {
        player?.seekTo(position)
    }

    fun setPlaybackOrder(order: PlaybackOrder) {
        _playbackOrder.value = order
    }

    fun nextTrack(): Track? {
        if (playlist.isEmpty()) return null

        val nextIndex = when (_playbackOrder.value) {
            PlaybackOrder.SHUFFLE -> (0 until playlist.size).random()
            PlaybackOrder.REPEAT -> (currentIndex + 1) % playlist.size
            PlaybackOrder.PLAY_ONCE -> {
                if (currentIndex + 1 < playlist.size) {
                    currentIndex + 1
                } else {
                    stop()
                    return null
                }
            }
        }

        currentIndex = nextIndex
        val track = playlist[currentIndex]
        play(track, playlist)
        onNextTrack?.invoke(track)
        return track
    }

    fun previousTrack(): Track? {
        if (playlist.isEmpty()) return null

        currentIndex = if (currentIndex > 0) currentIndex - 1 else playlist.size - 1
        val track = playlist[currentIndex]
        play(track, playlist)
        return track
    }

    fun setOnTrackEnded(callback: () -> Unit) {
        onTrackEnded = callback
    }

    fun setOnNextTrack(callback: (Track) -> Unit) {
        onNextTrack = callback
    }

    private fun handleTrackEnded() {
        when (_playbackOrder.value) {
            PlaybackOrder.PLAY_ONCE -> {
                if (currentIndex + 1 >= playlist.size) {
                    stop()
                    onTrackEnded?.invoke()
                    return
                }
            }
            PlaybackOrder.SHUFFLE, PlaybackOrder.REPEAT -> {
                nextTrack()
            }
        }
    }

    private fun startPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = scope.launch {
            while (isActive) {
                player?.let {
                    _position.value = it.currentPosition
                    _duration.value = it.duration.coerceAtLeast(0)
                }
                delay(500)
            }
        }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

    fun release() {
        stopPositionUpdates()
        player?.release()
        player = null
        scope.cancel()
    }
}
