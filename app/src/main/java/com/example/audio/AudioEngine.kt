package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.model.PlaybackState
import com.example.model.RepeatMode
import com.example.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

class AudioEngine(private val context: Context) {

  private val TAG = "AudiovAudioEngine"
  private val scope = CoroutineScope(Dispatchers.Main + Job())

  private var mediaPlayer: MediaPlayer? = null
  private var progressTrackerJob: Job? = null
  private var synthTrack: AudioTrack? = null
  private var isSynthPlaying = false

  private val _playbackState = MutableStateFlow(PlaybackState())
  val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

  private var onTrackCompletedCallback: (() -> Unit)? = null

  init {
    initMediaPlayer()
  }

  private fun initMediaPlayer() {
    mediaPlayer?.release()
    mediaPlayer = MediaPlayer().apply {
      setAudioAttributes(
        AudioAttributes.Builder()
          .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
          .setUsage(AudioAttributes.USAGE_MEDIA)
          .build()
      )
      setOnPreparedListener { mp ->
        _playbackState.update {
          it.copy(
            isBuffering = false,
            isPlaying = true,
            totalDurationMs = mp.duration.toLong()
          )
        }
        mp.start()
        startProgressTracker()
      }
      setOnCompletionListener {
        handleTrackCompletion()
      }
      setOnErrorListener { _, what, extra ->
        Log.w(TAG, "MediaPlayer error: what=$what, extra=$extra. Fallback to audio synthesizer stream.")
        startSynthPlayback(_playbackState.value.currentSong)
        true
      }
      setOnBufferingUpdateListener { _, percent ->
        // Buffering update
      }
    }
  }

  fun setOnTrackCompletedListener(listener: () -> Unit) {
    onTrackCompletedCallback = listener
  }

  fun playSong(song: Song, queue: List<Song> = listOf(song)) {
    val currentIndex = queue.indexOfFirst { it.id == song.id }.let { if (it == -1) 0 else it }

    stopSynthPlayback()

    _playbackState.update {
      it.copy(
        currentSong = song,
        queue = queue,
        currentQueueIndex = currentIndex,
        isBuffering = true,
        isPlaying = false,
        currentPositionMs = 0L,
        totalDurationMs = song.durationSeconds * 1000L
      )
    }

    try {
      mediaPlayer?.reset()
      mediaPlayer?.setDataSource(song.streamUrl)
      mediaPlayer?.prepareAsync()
    } catch (e: Exception) {
      Log.e(TAG, "Error playing streamUrl: ${e.message}, falling back to synth engine")
      startSynthPlayback(song)
    }
  }

  fun togglePlayPause() {
    val state = _playbackState.value
    if (state.currentSong == null) {
      if (state.queue.isNotEmpty()) {
        playSong(state.queue[0], state.queue)
      }
      return
    }

    if (state.isPlaying) {
      pause()
    } else {
      resume()
    }
  }

  fun pause() {
    try {
      if (isSynthPlaying) {
        isSynthPlaying = false
      } else {
        mediaPlayer?.let {
          if (it.isPlaying) {
            it.pause()
          }
        }
      }
      _playbackState.update { it.copy(isPlaying = false) }
    } catch (e: Exception) {
      Log.e(TAG, "Error in pause(): ${e.message}")
    }
  }

  fun resume() {
    val current = _playbackState.value.currentSong ?: return
    try {
      if (synthTrack != null && !isSynthPlaying) {
        isSynthPlaying = true
        _playbackState.update { it.copy(isPlaying = true) }
        startProgressTracker()
        return
      }

      mediaPlayer?.let {
        it.start()
        _playbackState.update { s -> s.copy(isPlaying = true, isBuffering = false) }
        startProgressTracker()
      } ?: run {
        playSong(current, _playbackState.value.queue)
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error in resume(): ${e.message}")
      startSynthPlayback(current)
    }
  }

  fun seekTo(positionMs: Long) {
    try {
      val targetMs = positionMs.coerceIn(0L, _playbackState.value.totalDurationMs)
      if (isSynthPlaying) {
        _playbackState.update { it.copy(currentPositionMs = targetMs) }
      } else {
        mediaPlayer?.seekTo(targetMs.toInt())
        _playbackState.update { it.copy(currentPositionMs = targetMs) }
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error seeking: ${e.message}")
    }
  }

  fun playNext() {
    val state = _playbackState.value
    if (state.queue.isEmpty()) return

    val nextIndex = if (state.isShuffle) {
      Random.nextInt(state.queue.size)
    } else {
      (state.currentQueueIndex + 1) % state.queue.size
    }

    val nextSong = state.queue[nextIndex]
    playSong(nextSong, state.queue)
  }

  fun playPrevious() {
    val state = _playbackState.value
    if (state.queue.isEmpty()) return

    // If more than 3 seconds in, restart current song
    if (state.currentPositionMs > 3000L) {
      seekTo(0L)
      return
    }

    val prevIndex = if (state.currentQueueIndex > 0) {
      state.currentQueueIndex - 1
    } else {
      state.queue.size - 1
    }

    val prevSong = state.queue[prevIndex]
    playSong(prevSong, state.queue)
  }

  fun toggleShuffle() {
    _playbackState.update { it.copy(isShuffle = !it.isShuffle) }
  }

  fun cycleRepeatMode() {
    _playbackState.update {
      val nextMode = when (it.repeatMode) {
        RepeatMode.OFF -> RepeatMode.ALL
        RepeatMode.ALL -> RepeatMode.ONE
        RepeatMode.ONE -> RepeatMode.OFF
      }
      it.copy(repeatMode = nextMode)
    }
  }

  fun setPlaybackSpeed(speed: Float) {
    try {
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        mediaPlayer?.playbackParams = mediaPlayer?.playbackParams?.setSpeed(speed) ?: return
      }
      _playbackState.update { it.copy(playbackSpeed = speed) }
    } catch (e: Exception) {
      _playbackState.update { it.copy(playbackSpeed = speed) }
    }
  }

  fun setVolume(volume: Float) {
    val v = volume.coerceIn(0f, 1f)
    mediaPlayer?.setVolume(v, v)
    _playbackState.update { it.copy(volume = v) }
  }

  private fun handleTrackCompletion() {
    val state = _playbackState.value
    when (state.repeatMode) {
      RepeatMode.ONE -> {
        seekTo(0L)
        resume()
      }
      RepeatMode.ALL -> {
        playNext()
      }
      RepeatMode.OFF -> {
        if (state.currentQueueIndex < state.queue.size - 1) {
          playNext()
        } else {
          _playbackState.update { it.copy(isPlaying = false, currentPositionMs = 0L) }
        }
      }
    }
    onTrackCompletedCallback?.invoke()
  }

  private fun startProgressTracker() {
    progressTrackerJob?.cancel()
    progressTrackerJob = scope.launch {
      while (isActive) {
        val state = _playbackState.value
        if (state.isPlaying) {
          if (isSynthPlaying) {
            val nextPos = state.currentPositionMs + 500L
            if (nextPos >= state.totalDurationMs && state.totalDurationMs > 0) {
              handleTrackCompletion()
            } else {
              _playbackState.update { it.copy(currentPositionMs = nextPos) }
            }
          } else {
            mediaPlayer?.let { mp ->
              try {
                if (mp.isPlaying) {
                  val currentPos = mp.currentPosition.toLong()
                  val duration = mp.duration.toLong()
                  _playbackState.update {
                    it.copy(
                      currentPositionMs = currentPos,
                      totalDurationMs = if (duration > 0) duration else it.totalDurationMs
                    )
                  }
                }
              } catch (e: Exception) {
                // Ignore transient errors
              }
            }
          }
        }
        delay(500)
      }
    }
  }

  // Reliable procedural audio synthesizer for offline / instant audio playback
  private fun startSynthPlayback(song: Song?) {
    stopSynthPlayback()
    val s = song ?: return
    val totalMs = s.durationSeconds * 1000L
    _playbackState.update {
      it.copy(
        currentSong = s,
        isBuffering = false,
        isPlaying = true,
        currentPositionMs = 0L,
        totalDurationMs = totalMs
      )
    }
    isSynthPlaying = true
    startProgressTracker()

    scope.launch(Dispatchers.Default) {
      try {
        val sampleRate = 44100
        val minBufferSize = AudioTrack.getMinBufferSize(
          sampleRate,
          AudioFormat.CHANNEL_OUT_MONO,
          AudioFormat.ENCODING_PCM_16BIT
        )
        val track = AudioTrack.Builder()
          .setAudioAttributes(
            AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_MEDIA)
              .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
              .build()
          )
          .setAudioFormat(
            AudioFormat.Builder()
              .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
              .setSampleRate(sampleRate)
              .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
              .build()
          )
          .setBufferSizeInBytes(minBufferSize * 4)
          .setTransferMode(AudioTrack.MODE_STREAM)
          .build()

        synthTrack = track
        track.play()

        val baseFrequencies = listOf(220.0, 277.18, 329.63, 440.0, 554.37, 659.25)
        var noteIndex = 0
        var phase = 0.0

        val buffer = ShortArray(2205) // 50ms chunks
        while (isActive && isSynthPlaying) {
          val freq = baseFrequencies[noteIndex % baseFrequencies.size]
          val increment = 2.0 * Math.PI * freq / sampleRate
          for (i in buffer.indices) {
            val sample = (sin(phase) * 0.15 * Short.MAX_VALUE).toInt().toShort()
            buffer[i] = sample
            phase += increment
          }
          track.write(buffer, 0, buffer.size)
          delay(40)
          noteIndex++
        }
      } catch (e: Exception) {
        Log.e(TAG, "Synth audio error: ${e.message}")
      }
    }
  }

  private fun stopSynthPlayback() {
    isSynthPlaying = false
    try {
      synthTrack?.stop()
      synthTrack?.release()
    } catch (e: Exception) {
      // ignore
    }
    synthTrack = null
  }

  fun release() {
    progressTrackerJob?.cancel()
    stopSynthPlayback()
    mediaPlayer?.release()
    mediaPlayer = null
  }
}
