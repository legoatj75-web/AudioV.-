package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.Playlist
import com.example.model.Song
import com.example.ui.components.AudiovBottomNavigation
import com.example.ui.components.FullPlayerSheet
import com.example.ui.components.MiniPlayer
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.AudiovBackground
import com.example.viewmodel.AudioPlayerViewModel
import com.example.viewmodel.AudiovNavTab

@Composable
fun AudiovMainScreen(
  viewModel: AudioPlayerViewModel = viewModel()
) {
  val uiState by viewModel.uiState.collectAsState()
  val playbackState by viewModel.playbackState.collectAsState()
  val context = LocalContext.current

  LaunchedEffect(uiState.toastMessage) {
    uiState.toastMessage?.let { msg ->
      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
      viewModel.clearToast()
    }
  }

  val isCurrentFavorite = playbackState.currentSong?.let {
    uiState.favoriteSongIds.contains(it.id)
  } ?: false

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(AudiovBackground)
  ) {
    Scaffold(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding(),
      containerColor = AudiovBackground,
      bottomBar = {
        Column(modifier = Modifier.fillMaxWidth()) {
          // Mini Player persistently sitting above navigation bar when a track is active
          MiniPlayer(
            playbackState = playbackState,
            isFavorite = isCurrentFavorite,
            onPlayPauseClick = { viewModel.togglePlayPause() },
            onSkipPreviousClick = { viewModel.playPrevious() },
            onSkipNextClick = { viewModel.playNext() },
            onFavoriteClick = {
              playbackState.currentSong?.let { viewModel.toggleFavorite(it.id) }
            },
            onClick = { viewModel.showFullPlayer(true) }
          )

          // Bottom Navigation Bar with 4 distinct tabs (Home, Search, Library, Settings)
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
        when (uiState.activeTab) {
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
              onFavoriteClick = { songId -> viewModel.toggleFavorite(songId) },
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
              onFavoriteClick = { songId -> viewModel.toggleFavorite(songId) }
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
              onFavoriteClick = { songId -> viewModel.toggleFavorite(songId) },
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

    // Full Player Modal Sheet
    if (uiState.isFullPlayerVisible && playbackState.currentSong != null) {
      FullPlayerSheet(
        playbackState = playbackState,
        isFavorite = isCurrentFavorite,
        isLyricsVisible = uiState.isLyricsViewVisible,
        isQueueVisible = uiState.isQueueSheetVisible,
        onDismiss = {
          viewModel.showFullPlayer(false)
          viewModel.toggleLyricsView(false)
          viewModel.toggleQueueSheet(false)
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
