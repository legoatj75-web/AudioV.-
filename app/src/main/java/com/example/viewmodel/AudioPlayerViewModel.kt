package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioEngine
import com.example.data.MusicRepository
import com.example.model.PlaybackState
import com.example.model.Playlist
import com.example.model.RepeatMode
import com.example.model.Song
import com.example.model.StreamingQuality
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AudiovNavTab {
  HOME, SEARCH, LIBRARY, SETTINGS
}

data class AudiovUiState(
  val allSongs: List<Song> = MusicRepository.sampleSongs,
  val playlists: List<Playlist> = MusicRepository.samplePlaylists,
  val selectedGenre: String = "All",
  val searchQuery: String = "",
  val activeTab: AudiovNavTab = AudiovNavTab.HOME,
  val favoriteSongIds: Set<String> = setOf("song_1", "song_3"),
  val isFullPlayerVisible: Boolean = false,
  val streamingQuality: StreamingQuality = StreamingQuality.HIGH,
  val crossfadeSeconds: Int = 3,
  val bassBoostEnabled: Boolean = true,
  val equalizerPreset: String = "Electronic Glow",
  val sleepTimerMinutes: Int? = null,
  val isQueueSheetVisible: Boolean = false,
  val isLyricsViewVisible: Boolean = false,
  val toastMessage: String? = null
)

class AudioPlayerViewModel(application: Application) : AndroidViewModel(application) {

  val audioEngine = AudioEngine(application.applicationContext)

  private val _uiState = MutableStateFlow(AudiovUiState())
  val uiState: StateFlow<AudiovUiState> = _uiState.asStateFlow()

  val playbackState: StateFlow<PlaybackState> = audioEngine.playbackState

  private var sleepTimerJob: Job? = null

  init {
    // Start with the first song ready in queue
    val initialSongs = MusicRepository.sampleSongs
    audioEngine.setOnTrackCompletedListener {
      // Optional callback on track finish
    }
  }

  fun selectTab(tab: AudiovNavTab) {
    _uiState.update { it.copy(activeTab = tab) }
  }

  fun selectGenre(genre: String) {
    _uiState.update { it.copy(selectedGenre = genre) }
  }

  fun updateSearchQuery(query: String) {
    _uiState.update { it.copy(searchQuery = query) }
  }

  fun playSong(song: Song, queue: List<Song>? = null) {
    val effectiveQueue = queue ?: _uiState.value.allSongs
    audioEngine.playSong(song, effectiveQueue)
  }

  fun playPlaylist(playlist: Playlist) {
    if (playlist.songs.isNotEmpty()) {
      audioEngine.playSong(playlist.songs.first(), playlist.songs)
    }
  }

  fun togglePlayPause() {
    audioEngine.togglePlayPause()
  }

  fun playNext() {
    audioEngine.playNext()
  }

  fun playPrevious() {
    audioEngine.playPrevious()
  }

  fun seekTo(positionMs: Long) {
    audioEngine.seekTo(positionMs)
  }

  fun toggleShuffle() {
    audioEngine.toggleShuffle()
  }

  fun cycleRepeatMode() {
    audioEngine.cycleRepeatMode()
  }

  fun setPlaybackSpeed(speed: Float) {
    audioEngine.setPlaybackSpeed(speed)
  }

  fun setVolume(volume: Float) {
    audioEngine.setVolume(volume)
  }

  fun toggleFavorite(songId: String) {
    _uiState.update { current ->
      val newFavorites = if (current.favoriteSongIds.contains(songId)) {
        current.favoriteSongIds - songId
      } else {
        current.favoriteSongIds + songId
      }
      current.copy(favoriteSongIds = newFavorites)
    }
  }

  fun showFullPlayer(show: Boolean) {
    _uiState.update { it.copy(isFullPlayerVisible = show) }
  }

  fun toggleQueueSheet(show: Boolean) {
    _uiState.update { it.copy(isQueueSheetVisible = show) }
  }

  fun toggleLyricsView(show: Boolean) {
    _uiState.update { it.copy(isLyricsViewVisible = show) }
  }

  fun setStreamingQuality(quality: StreamingQuality) {
    _uiState.update { it.copy(streamingQuality = quality) }
    showToast("Streaming quality set to ${quality.label}")
  }

  fun setCrossfadeSeconds(seconds: Int) {
    _uiState.update { it.copy(crossfadeSeconds = seconds) }
  }

  fun setEqualizerPreset(preset: String) {
    _uiState.update { it.copy(equalizerPreset = preset) }
    showToast("Equalizer preset: $preset")
  }

  fun toggleBassBoost() {
    _uiState.update { it.copy(bassBoostEnabled = !it.bassBoostEnabled) }
  }

  fun setSleepTimer(minutes: Int?) {
    sleepTimerJob?.cancel()
    _uiState.update { it.copy(sleepTimerMinutes = minutes) }

    if (minutes != null && minutes > 0) {
      showToast("Sleep timer set for $minutes minutes")
      sleepTimerJob = viewModelScope.launch {
        var remaining = minutes
        while (remaining > 0) {
          delay(60_000L)
          remaining--
          _uiState.update { it.copy(sleepTimerMinutes = remaining) }
        }
        audioEngine.pause()
        _uiState.update { it.copy(sleepTimerMinutes = null) }
        showToast("Sleep timer finished: playback paused")
      }
    } else {
      showToast("Sleep timer turned off")
    }
  }

  fun createPlaylist(title: String, description: String) {
    val newPlaylist = Playlist(
      id = "custom_pl_${System.currentTimeMillis()}",
      title = title,
      description = description,
      coverDrawableRes = com.example.R.drawable.album_neon_synthwave,
      gradientStartHex = 0xFF8B5CF6,
      gradientEndHex = 0xFF06B6D4,
      songs = _uiState.value.allSongs.take(3)
    )
    _uiState.update { it.copy(playlists = it.playlists + newPlaylist) }
    showToast("Created playlist \"$title\"")
  }

  fun showToast(msg: String) {
    _uiState.update { it.copy(toastMessage = msg) }
  }

  fun clearToast() {
    _uiState.update { it.copy(toastMessage = null) }
  }

  override fun onCleared() {
    super.onCleared()
    sleepTimerJob?.cancel()
    audioEngine.release()
  }
}
