package com.spbchurch.radio.data.service

import com.spbchurch.radio.data.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class TrackListService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    companion object {
        private const val CATALOG_URL = "https://station.spbchurch.ru/mp3/mp3_files_list.html"
        private val MP3_PATTERN = Regex("""<a\s+href="([^"]+\.mp3)"[^>]*>([^<]+)</a>""")
    }

    suspend fun fetchTracks(): Result<List<Track>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(CATALOG_URL)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("HTTP ${response.code}"))
                }

                val body = response.body?.string() ?: return@withContext Result.failure(
                    Exception("Empty response")
                )

                val tracks = parseTracks(body)
                Result.success(tracks)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseTracks(html: String): List<Track> {
        val tracks = mutableListOf<Track>()
        val matches = MP3_PATTERN.findAll(html)

        for (match in matches) {
            val url = match.groupValues[1]
            val title = match.groupValues[2]
                .trim()
                .replace(".mp3", "")
                .replace("_", " ")
                .replace("-", " ")
                .trim()

            if (url.isNotBlank() && title.isNotBlank()) {
                tracks.add(Track.fromUrl(url, title))
            }
        }

        return tracks
    }
}
