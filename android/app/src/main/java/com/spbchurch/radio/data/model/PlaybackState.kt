package com.spbchurch.radio.data.model

data class PlaybackState(
    val isPlaying: Boolean = false,
    val isRadioMode: Boolean = true,
    val currentTrack: Track? = null,
    val currentTitle: String = "",
    val progress: Float = 0f,
    val duration: Long = 0L,
    val position: Long = 0L,
    val isBuffering: Boolean = false,
    val playbackOrder: PlaybackOrder = PlaybackOrder.SHUFFLE,
    val artwork: ByteArray? = null,
    val error: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PlaybackState) return false
        return isPlaying == other.isPlaying &&
                isRadioMode == other.isRadioMode &&
                currentTrack == other.currentTrack &&
                currentTitle == other.currentTitle &&
                progress == other.progress &&
                duration == other.duration &&
                position == other.position &&
                isBuffering == other.isBuffering &&
                playbackOrder == other.playbackOrder &&
                artwork.contentEqualsOrSame(other.artwork) &&
                error == other.error
    }

    override fun hashCode(): Int {
        var result = isPlaying.hashCode()
        result = 31 * result + isRadioMode.hashCode()
        result = 31 * result + (currentTrack?.hashCode() ?: 0)
        result = 31 * result + currentTitle.hashCode()
        result = 31 * result + progress.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + position.hashCode()
        result = 31 * result + isBuffering.hashCode()
        result = 31 * result + playbackOrder.hashCode()
        result = 31 * result + (artwork?.size ?: 0)
        result = 31 * result + (error?.hashCode() ?: 0)
        return result
    }
}

private fun ByteArray?.contentEqualsOrSame(other: ByteArray?): Boolean {
    if (this === other) return true
    if (this == null || other == null) return false
    return this.contentEquals(other)
}

enum class PlaybackOrder {
    SHUFFLE,
    REPEAT,
    PLAY_ONCE
}

data class RadioMetadata(
    val title: String = "",
    val isLive: Boolean = false
)
