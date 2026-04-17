package com.spbchurch.radio.data.service

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.spbchurch.radio.data.model.RadioMetadata
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class RadioStreamService(private val context: Context) {

    private var player: ExoPlayer? = null
    private var metadataJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _radioMetadata = MutableStateFlow(RadioMetadata())
    val radioMetadata: StateFlow<RadioMetadata> = _radioMetadata.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering: StateFlow<Boolean> = _isBuffering.asStateFlow()

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    companion object {
        const val RADIO_URL = "https://station.spbchurch.ru/radio"
        const val METADATA_URL = "https://station.spbchurch.ru/"
        private const val METADATA_POLL_INTERVAL = 5000L
    }

    fun initialize() {
        if (player == null) {
            player = ExoPlayer.Builder(context)
                .build()
                .apply {
                    val mediaItem = MediaItem.Builder()
                        .setUri(RADIO_URL)
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setTitle("SPBChurch Radio")
                                .build()
                        )
                        .build()
                    setMediaItem(mediaItem)
                    prepare()
                }

            player?.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    _isBuffering.value = state == Player.STATE_BUFFERING
                }

                override fun onIsPlayingChanged(playing: Boolean) {
                    _isPlaying.value = playing
                }
            })
        }
    }

    fun play() {
        initialize()
        player?.play()
        startMetadataPolling()
    }

    fun pause() {
        player?.pause()
        stopMetadataPolling()
    }

    fun stop() {
        player?.stop()
        _isPlaying.value = false
        stopMetadataPolling()
    }

    fun release() {
        stopMetadataPolling()
        player?.release()
        player = null
        scope.cancel()
    }

    private fun startMetadataPolling() {
        metadataJob?.cancel()
        metadataJob = scope.launch {
            while (isActive) {
                fetchMetadata()
                delay(METADATA_POLL_INTERVAL)
            }
        }
    }

    private fun stopMetadataPolling() {
        metadataJob?.cancel()
        metadataJob = null
    }

    private fun fetchMetadata() {
        try {
            val request = Request.Builder()
                .url(METADATA_URL)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: ""
                    val title = parseMetadata(body)
                    _radioMetadata.value = RadioMetadata(
                        title = title,
                        isLive = _isPlaying.value
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseMetadata(html: String): String {
        val patterns = listOf(
            Regex("""Currently playing:\s*([^<"'\n]+)"""),
            Regex("""stream_title['"]?\s*[=:]\s*['"]([^'"]+)['"]"""),
            Regex("""Названи[её]\s*</[^>]*>\s*<[^>]*>([^<]+)</a>"""),
            Regex("""<title>[^<]*-\s*([^<]+)</title>"""),
            Regex("""playing\s*[=:]\s*['"]([^'"]+)['"]""")
        )

        for (pattern in patterns) {
            val match = pattern.find(html)
            if (match != null) {
                val title = match.groupValues[1]
                    .replace(Regex("""[|_\-]+"""), " ")
                    .replace(Regex("""\s+"""), " ")
                    .replace("SPBChurch Radio", "")
                    .replace("Церковь Преображение", "")
                    .trim()
                if (title.isNotBlank()) {
                    return title
                }
            }
        }
        return "Радио"
    }
}
