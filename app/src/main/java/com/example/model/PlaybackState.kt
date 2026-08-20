package com.example.model

enum class RepeatMode {
  OFF, ALL, ONE
}

enum class StreamingQuality(val label: String, val bitrate: String) {
  NORMAL("Normal", "128 kbps"),
  HIGH("High (HQ)", "256 kbps"),
  LOSSLESS("Lossless Studio", "1411 kbps FLAC")
}

data class PlaybackState(
  val currentSong: Song? = null,
  val isPlaying: Boolean = false,
  val isBuffering: Boolean = false,
  val currentPositionMs: Long = 0L,
  val totalDurationMs: Long = 0L,
  val queue: List<Song> = emptyList(),
  val currentQueueIndex: Int = 0,
  val isShuffle: Boolean = false,
  val repeatMode: RepeatMode = RepeatMode.OFF,
  val playbackSpeed: Float = 1.0f,
  val volume: Float = 1.0f,
  val crossfadeSeconds: Int = 3,
  val sleepTimerMinutesRemaining: Int? = null
) {
  val progressFraction: Float
    get() = if (totalDurationMs > 0) {
      (currentPositionMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)
    } else 0f

  val formattedCurrentPosition: String
    get() {
      val totalSec = currentPositionMs / 1000
      val min = totalSec / 60
      val sec = totalSec % 60
      return String.format("%d:%02d", min, sec)
    }

  val formattedTotalDuration: String
    get() {
      val totalSec = if (totalDurationMs > 0) totalDurationMs / 1000 else (currentSong?.durationSeconds?.toLong() ?: 0L)
      val min = totalSec / 60
      val sec = totalSec % 60
      return String.format("%d:%02d", min, sec)
    }
}
