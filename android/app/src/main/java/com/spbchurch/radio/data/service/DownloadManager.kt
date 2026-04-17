package com.spbchurch.radio.data.service

import android.content.Context
import com.spbchurch.radio.data.model.DownloadState
import com.spbchurch.radio.data.model.DownloadedTrackMetadata
import com.spbchurch.radio.data.model.Track
import com.spbchurch.radio.data.model.sha256
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

class DownloadManager(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(300, TimeUnit.SECONDS)
        .build()

    private val downloadsDir: File by lazy {
        File(context.filesDir, "OfflineTracks").apply {
            if (!exists()) mkdirs()
        }
    }

    private val metadataFile: File by lazy {
        File(context.filesDir, "downloads.json")
    }

    private val json = Json { ignoreUnknownKeys = true }

    private val _downloadProgress = MutableStateFlow<Map<String, Float>>(emptyMap())
    val downloadProgress: StateFlow<Map<String, Float>> = _downloadProgress.asStateFlow()

    private val _downloadedTracks = MutableStateFlow<Map<String, DownloadedTrackMetadata>>(emptyMap())
    val downloadedTracks: StateFlow<Map<String, DownloadedTrackMetadata>> = _downloadedTracks.asStateFlow()

    private val activeDownloads = mutableMapOf<String, Job>()

    init {
        loadMetadata()
    }

    private fun loadMetadata() {
        try {
            if (metadataFile.exists()) {
                val content = metadataFile.readText()
                val tracks = json.decodeFromString<List<DownloadedTrackMetadata>>(content)
                _downloadedTracks.value = tracks.associateBy { it.url }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveMetadata() {
        try {
            val tracks = _downloadedTracks.value.values.toList()
            metadataFile.writeText(json.encodeToString(tracks))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isDownloaded(url: String): Boolean {
        return _downloadedTracks.value.containsKey(url)
    }

    fun getLocalPath(url: String): String? {
        return _downloadedTracks.value[url]?.localPath
    }

    fun getDownloadState(track: Track): DownloadState {
        return when {
            isDownloaded(track.url) -> DownloadState.Downloaded
            activeDownloads.containsKey(track.id) -> DownloadState.Downloading
            track.downloadState == DownloadState.Failed -> DownloadState.Failed
            else -> DownloadState.None
        }
    }

    fun download(track: Track, onComplete: (Boolean) -> Unit = {}) {
        if (isDownloaded(track.url) || activeDownloads.containsKey(track.id)) {
            return
        }

        val job = scope.launch {
            try {
                val hash = track.url.sha256()
                val file = File(downloadsDir, "$hash.mp3")

                val request = Request.Builder()
                    .url(track.url)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        onComplete(false)
                        return@use
                    }

                    val body = response.body ?: run {
                        onComplete(false)
                        return@use
                    }

                    val totalBytes = body.contentLength()
                    var downloadedBytes = 0L

                    FileOutputStream(file).use { output ->
                        body.byteStream().use { input ->
                            val buffer = ByteArray(8192)
                            var bytes: Int

                            while (input.read(buffer).also { bytes = it } != -1) {
                                output.write(buffer, 0, bytes)
                                downloadedBytes += bytes

                                if (totalBytes > 0) {
                                    val progress = downloadedBytes.toFloat() / totalBytes
                                    _downloadProgress.value = _downloadProgress.value + (track.id to progress)
                                }
                            }
                        }
                    }

                    val metadata = DownloadedTrackMetadata(
                        url = track.url,
                        title = track.title,
                        localPath = file.absolutePath
                    )

                    _downloadedTracks.value = _downloadedTracks.value + (track.url to metadata)
                    _downloadProgress.value = _downloadProgress.value - track.id
                    saveMetadata()
                    onComplete(true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _downloadProgress.value = _downloadProgress.value - track.id
                onComplete(false)
            }
        }

        activeDownloads[track.id] = job
    }

    fun cancelDownload(track: Track) {
        activeDownloads[track.id]?.cancel()
        activeDownloads.remove(track.id)
        _downloadProgress.value = _downloadProgress.value - track.id
    }

    fun deleteDownload(track: Track) {
        val metadata = _downloadedTracks.value[track.url] ?: return
        val file = File(metadata.localPath)
        if (file.exists()) {
            file.delete()
        }
        _downloadedTracks.value = _downloadedTracks.value - track.url
        saveMetadata()
    }

    fun getDownloadedTrackList(): List<Track> {
        return _downloadedTracks.value.values.map { metadata ->
            Track.fromUrl(metadata.url, metadata.title).copy(
                downloadState = DownloadState.Downloaded,
                localPath = metadata.localPath
            )
        }
    }

    fun backfillMetadata(tracks: List<Track>) {
        val updated = _downloadedTracks.value.toMutableMap()
        var changed = false

        for (track in tracks) {
            if (!updated.containsKey(track.url) && File(downloadsDir, "${track.url.sha256()}.mp3").exists()) {
                val localPath = File(downloadsDir, "${track.url.sha256()}.mp3").absolutePath
                updated[track.url] = DownloadedTrackMetadata(
                    url = track.url,
                    title = track.title,
                    localPath = localPath
                )
                changed = true
            }
        }

        if (changed) {
            _downloadedTracks.value = updated
            saveMetadata()
        }
    }

    fun release() {
        scope.cancel()
    }
}
