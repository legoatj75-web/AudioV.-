package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AudiovBottomNavigation
import com.example.ui.components.FullPlayerSheet
import com.example.ui.components.MiniPlayer
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.AudiovBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AudioPlayerViewModel
import com.example.viewmodel.AudiovNavTab

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        AudiovApp()
      }
    }
  }
}

@Composable
fun AudiovApp(
  viewModel: AudioPlayerViewModel = viewModel()
) {
  val uiState by viewModel.uiState.collectAsState()
  val playbackState by viewModel.playbackState.collectAsState()
  val context = LocalContext.current
  val snackbarHostState = remember { SnackbarHostState() }

  // Toast listener
  LaunchedEffect(uiState.toastMessage) {
    uiState.toastMessage?.let { msg ->
      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
      viewModel.clearToast()
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(AudiovBackground)
  ) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      containerColor = AudiovBackground,
      snackbarHost = { SnackbarHost(snackbarHostState) },
      bottomBar = {
        Column(modifier = Modifier.fillMaxWidth()) {
          // Persistent Mini Player above Bottom Navigation
          MiniPlayer(
            playbackState = playbackState,
            isFavorite = playbackState.currentSong?.let { uiState.favoriteSongIds.contains(it.id) } ?: false,
            onPlayPauseClick = { viewModel.togglePlayPause() },
            onSkipPreviousClick = { viewModel.playPrevious() },
            onSkipNextClick = { viewModel.playNext() },
            onFavoriteClick = {
              playbackState.currentSong?.let { viewModel.toggleFavorite(it.id) }
            },
            onClick = { viewModel.showFullPlayer(true) }
          )

          // Bottom Navigation Bar
          AudiovBottomNavigation(
            activeTab = uiState.activeTab,
            onTabSelected = { viewModel.selectTab(it) }
          )
        }
      }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        AnimatedContent(
          targetState = uiState.activeTab,
          transitionSpec = { fadeIn() togetherWith fadeOut() },
          label = "screen_transition"
        ) { tab ->
          when (tab) {
            AudiovNavTab.HOME -> {
              HomeScreen(
                allSongs = uiState.allSongs,
                playlists = uiState.playlists,
                selectedGenre = uiState.selectedGenre,
                playbackState = playbackState,
                favoriteSongIds = uiState.favoriteSongIds,
                onGenreSelect = { viewModel.selectGenre(it) },
                onSongClick = { song, queue -> viewModel.playSong(song, queue) },
                onPlaylistClick = { playlist -> viewModel.playPlaylist(playlist) },
                onFavoriteClick = { id -> viewModel.toggleFavorite(id) },
                onNavigateToSearch = { viewModel.selectTab(AudiovNavTab.SEARCH) }
              )
            }

            AudiovNavTab.SEARCH -> {
              SearchScreen(
                allSongs = uiState.allSongs,
                searchQuery = uiState.searchQuery,
                playbackState = playbackState,
                favoriteSongIds = uiState.favoriteSongIds,
                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                onSongClick = { song, queue -> viewModel.playSong(song, queue) },
                onFavoriteClick = { id -> viewModel.toggleFavorite(id) }
              )
            }

            AudiovNavTab.LIBRARY -> {
              LibraryScreen(
                allSongs = uiState.allSongs,
                playlists = uiState.playlists,
                favoriteSongIds = uiState.favoriteSongIds,
                playbackState = playbackState,
                onSongClick = { song, queue -> viewModel.playSong(song, queue) },
                onPlaylistClick = { playlist -> viewModel.playPlaylist(playlist) },
                onFavoriteClick = { id -> viewModel.toggleFavorite(id) },
                onCreatePlaylist = { title, desc -> viewModel.createPlaylist(title, desc) }
              )
            }

            AudiovNavTab.SETTINGS -> {
              SettingsScreen(
                streamingQuality = uiState.streamingQuality,
                crossfadeSeconds = uiState.crossfadeSeconds,
                bassBoostEnabled = uiState.bassBoostEnabled,
                equalizerPreset = uiState.equalizerPreset,
                sleepTimerMinutes = uiState.sleepTimerMinutes,
                onQualityChange = { viewModel.setStreamingQuality(it) },
                onCrossfadeChange = { viewModel.setCrossfadeSeconds(it) },
                onBassBoostToggle = { viewModel.toggleBassBoost() },
                onEqualizerPresetChange = { viewModel.setEqualizerPreset(it) },
                onSleepTimerChange = { viewModel.setSleepTimer(it) }
              )
            }
          }
        }
      }
    }

    // Expandable Full Player Sheet
    if (uiState.isFullPlayerVisible && playbackState.currentSong != null) {
      FullPlayerSheet(
        playbackState = playbackState,
        isFavorite = playbackState.currentSong?.let { uiState.favoriteSongIds.contains(it.id) } ?: false,
        isLyricsVisible = uiState.isLyricsViewVisible,
        isQueueVisible = uiState.isQueueSheetVisible,
        onDismiss = {
          viewModel.showFullPlayer(false)
          viewModel.toggleQueueSheet(false)
          viewModel.toggleLyricsView(false)
        },
        onPlayPauseClick = { viewModel.togglePlayPause() },
        onSkipNextClick = { viewModel.playNext() },
        onSkipPreviousClick = { viewModel.playPrevious() },
        onSeekTo = { posMs -> viewModel.seekTo(posMs) },
        onToggleShuffle = { viewModel.toggleShuffle() },
        onCycleRepeat = { viewModel.cycleRepeatMode() },
        onToggleFavorite = {
          playbackState.currentSong?.let { viewModel.toggleFavorite(it.id) }
        },
        onSpeedChange = { speed -> viewModel.setPlaybackSpeed(speed) },
        onVolumeChange = { vol -> viewModel.setVolume(vol) },
        onToggleLyrics = { viewModel.toggleLyricsView(!uiState.isLyricsViewVisible) },
        onToggleQueue = { viewModel.toggleQueueSheet(!uiState.isQueueSheetVisible) },
        onSelectSongFromQueue = { song -> viewModel.playSong(song, playbackState.queue) }
      )
    }
  }
}
