package com.spbchurch.radio.data.model

import kotlinx.serialization.Serializable
import java.security.MessageDigest

@Serializable
data class Track(
    val id: String,
    val title: String,
    val url: String,
    val isFavorite: Boolean = false,
    val downloadState: DownloadState = DownloadState.None,
    val localPath: String? = null
) {
    companion object {
        fun fromUrl(url: String, title: String): Track {
            return Track(
                id = url.sha256().take(16),
                title = title,
                url = url
            )
        }
    }

    fun sha256(): String = url.sha256()
}

@Serializable
enum class DownloadState {
    None,
    Downloading,
    Downloaded,
    Failed
}

@Serializable
data class DownloadedTrackMetadata(
    val url: String,
    val title: String,
    val localPath: String
)

fun String.sha256(): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}
