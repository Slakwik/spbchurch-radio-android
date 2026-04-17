package com.spbchurch.radio

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.spbchurch.radio.data.repository.DownloadRepository
import com.spbchurch.radio.data.repository.FavoritesRepository
import com.spbchurch.radio.data.repository.TrackRepository
import com.spbchurch.radio.data.service.RadioStreamService
import com.spbchurch.radio.data.service.DownloadManager
import com.spbchurch.radio.data.service.FavoritesManager

class RadioApplication : Application() {

    lateinit var radioStreamService: RadioStreamService
        private set
    lateinit var trackRepository: TrackRepository
        private set
    lateinit var downloadManager: DownloadManager
        private set
    lateinit var favoritesManager: FavoritesManager
        private set
    lateinit var favoritesRepository: FavoritesRepository
        private set
    lateinit var downloadRepository: DownloadRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        createNotificationChannel()

        downloadManager = DownloadManager(this)
        favoritesManager = FavoritesManager(this)
        radioStreamService = RadioStreamService(this)
        trackRepository = TrackRepository()
        favoritesRepository = FavoritesRepository(favoritesManager)
        downloadRepository = DownloadRepository(downloadManager)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_description)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "radio_playback"

        @Volatile
        private var instance: RadioApplication? = null

        fun getInstance(): RadioApplication =
            instance ?: throw IllegalStateException("Application not initialized")
    }
}
