package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode as AnimRepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PlaybackState
import com.example.model.RepeatMode
import com.example.model.Song
import com.example.ui.theme.AudiovBackground
import com.example.ui.theme.AudiovCard
import com.example.ui.theme.AudiovCardElevated
import com.example.ui.theme.AudiovDivider
import com.example.ui.theme.AudiovPrimary
import com.example.ui.theme.AudiovPrimaryGlow
import com.example.ui.theme.AudiovSecondary
import com.example.ui.theme.AudiovSurfaceVariant
import com.example.ui.theme.AudiovTertiary
import com.example.ui.theme.AudiovTextMuted
import com.example.ui.theme.AudiovTextPrimary
import com.example.ui.theme.AudiovTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullPlayerSheet(
  playbackState: PlaybackState,
  isFavorite: Boolean,
  isLyricsVisible: Boolean,
  isQueueVisible: Boolean,
  onDismiss: () -> Unit,
  onPlayPauseClick: () -> Unit,
  onSkipNextClick: () -> Unit,
  onSkipPreviousClick: () -> Unit,
  onSeekTo: (Long) -> Unit,
  onToggleShuffle: () -> Unit,
  onCycleRepeat: () -> Unit,
  onToggleFavorite: () -> Unit,
  onSpeedChange: (Float) -> Unit,
  onVolumeChange: (Float) -> Unit,
  onToggleLyrics: () -> Unit,
  onToggleQueue: () -> Unit,
  onSelectSongFromQueue: (Song) -> Unit,
  modifier: Modifier = Modifier
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val song = playbackState.currentSong ?: return

  var isUserSeeking by remember { mutableStateOf(false) }
  var seekPositionFraction by remember { mutableFloatStateOf(0f) }

  val infiniteTransition = rememberInfiniteTransition(label = "player_art_rotation")
  val rotationDegree by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(20000, easing = LinearEasing),
      repeatMode = AnimRepeatMode.Restart
    ),
    label = "art_rotation"
  )

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = AudiovBackground,
    dragHandle = null,
    modifier = modifier.testTag("full_player_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .padding(horizontal = 20.dp, vertical = 8.dp)
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top Navigation header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onDismiss,
          modifier = Modifier.testTag("dismiss_player_button")
        ) {
          Icon(
            imageVector = Icons.Default.ExpandMore,
            contentDescription = "Collapse Player",
            tint = AudiovTextPrimary,
            modifier = Modifier.size(32.dp)
          )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "PLAYING FROM PLAYLIST",
            color = AudiovTextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp
          )
          Text(
            text = song.genre + " Vibe",
            color = AudiovPrimaryGlow,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        IconButton(onClick = onToggleQueue) {
          Icon(
            imageVector = Icons.Default.QueueMusic,
            contentDescription = "Queue",
            tint = if (isQueueVisible) AudiovPrimary else AudiovTextSecondary,
            modifier = Modifier.size(24.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Main Center Content: Either Queue List, Lyrics, or Large Album Art
      if (isQueueVisible) {
        // Queue view
        QueueView(
          queue = playbackState.queue,
          currentSong = song,
          isPlaying = playbackState.isPlaying,
          onSelectSong = onSelectSongFromQueue
        )
      } else if (isLyricsVisible) {
        // Lyrics View
        LyricsView(
          lyrics = song.lyrics,
          songTitle = song.title,
          artist = song.artist
        )
      } else {
        // Large Vinyl Artwork with dynamic ambient glow
        Box(
          modifier = Modifier
            .size(280.dp)
            .shadow(28.dp, CircleShape, spotColor = AudiovPrimary.copy(alpha = 0.5f)),
          contentAlignment = Alignment.Center
        ) {
          // Glow aura
          Box(
            modifier = Modifier
              .size(280.dp)
              .clip(CircleShape)
              .background(
                Brush.radialGradient(
                  listOf(
                    AudiovPrimary.copy(alpha = 0.4f),
                    AudiovSecondary.copy(alpha = 0.15f),
                    Color.Transparent
                  )
                )
              )
          )

          // Vinyl Record Disc
          Box(
            modifier = Modifier
              .size(250.dp)
              .rotate(if (playbackState.isPlaying) rotationDegree else 0f)
              .clip(CircleShape)
              .background(
                Brush.sweepGradient(
                  listOf(
                    Color(0xFF1E1E2F),
                    Color(0xFF0F0F1A),
                    Color(0xFF2A2A42),
                    Color(0xFF0F0F1A),
                    Color(0xFF1E1E2F)
                  )
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            // Album art core center
            Box(
              modifier = Modifier
                .size(170.dp)
                .clip(CircleShape)
                .background(
                  Brush.linearGradient(
                    listOf(
                      Color(song.gradientStartHex),
                      Color(song.gradientEndHex)
                    )
                  )
                ),
              contentAlignment = Alignment.Center
            ) {
              if (song.coverDrawableRes != null) {
                Image(
                  painter = painterResource(id = song.coverDrawableRes),
                  contentDescription = song.title,
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.size(170.dp)
                )
              }
              // Vinyl center spindle hole
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(AudiovBackground)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sound Wave Visualizer Banner
        SoundwaveVisualizer(
          isPlaying = playbackState.isPlaying,
          modifier = Modifier
            .fillMaxWidth()
            .height(28.dp)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Track Title, Artist, and Favorite button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = song.title,
            color = AudiovTextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "${song.artist} • ${song.album}",
            color = AudiovTextSecondary,
            fontSize = 15.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        IconButton(
          onClick = onToggleFavorite,
          modifier = Modifier.size(48.dp)
        ) {
          Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (isFavorite) "Liked" else "Like",
            tint = if (isFavorite) AudiovTertiary else AudiovTextMuted,
            modifier = Modifier.size(28.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Progress Timeline Slider
      val currentProgress = if (isUserSeeking) seekPositionFraction else playbackState.progressFraction

      Slider(
        value = currentProgress,
        onValueChange = { frac ->
          isUserSeeking = true
          seekPositionFraction = frac
        },
        onValueChangeFinished = {
          val targetMs = (seekPositionFraction * playbackState.totalDurationMs).toLong()
          onSeekTo(targetMs)
          isUserSeeking = false
        },
        colors = SliderDefaults.colors(
          thumbColor = AudiovPrimaryGlow,
          activeTrackColor = AudiovPrimary,
          inactiveTrackColor = AudiovDivider
        ),
        modifier = Modifier.fillMaxWidth()
      )

      // Time stamps
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = if (isUserSeeking) {
            val sec = ((seekPositionFraction * playbackState.totalDurationMs) / 1000).toLong()
            String.format("%d:%02d", sec / 60, sec % 60)
          } else playbackState.formattedCurrentPosition,
          color = AudiovTextMuted,
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium
        )
        Text(
          text = playbackState.formattedTotalDuration,
          color = AudiovTextMuted,
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Playback Controls Row: Shuffle, Previous, Play/Pause, Next, Repeat
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Shuffle Button
        IconButton(
          onClick = onToggleShuffle,
          modifier = Modifier.size(44.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Shuffle,
            contentDescription = "Shuffle",
            tint = if (playbackState.isShuffle) AudiovSecondary else AudiovTextMuted,
            modifier = Modifier.size(24.dp)
          )
        }

        // Previous Track
        IconButton(
          onClick = onSkipPreviousClick,
          modifier = Modifier.size(48.dp)
        ) {
          Icon(
            imageVector = Icons.Default.SkipPrevious,
            contentDescription = "Previous",
            tint = AudiovTextPrimary,
            modifier = Modifier.size(36.dp)
          )
        }

        // Main Play / Pause Button with Neon Aura
        Box(
          modifier = Modifier
            .size(72.dp)
            .shadow(16.dp, CircleShape, spotColor = AudiovPrimary)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(
                listOf(AudiovPrimary, AudiovPrimaryGlow)
              )
            )
            .clickable { onPlayPauseClick() }
            .testTag("full_player_play_pause_button"),
          contentAlignment = Alignment.Center
        ) {
          if (playbackState.isBuffering) {
            CircularProgressIndicator(
              modifier = Modifier.size(32.dp),
              color = AudiovTextPrimary,
              strokeWidth = 3.dp
            )
          } else {
            Icon(
              imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
              contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
              tint = AudiovTextPrimary,
              modifier = Modifier.size(40.dp)
            )
          }
        }

        // Next Track
        IconButton(
          onClick = onSkipNextClick,
          modifier = Modifier.size(48.dp)
        ) {
          Icon(
            imageVector = Icons.Default.SkipNext,
            contentDescription = "Next",
            tint = AudiovTextPrimary,
            modifier = Modifier.size(36.dp)
          )
        }

        // Repeat Mode Button
        IconButton(
          onClick = onCycleRepeat,
          modifier = Modifier.size(44.dp)
        ) {
          val (icon, tint) = when (playbackState.repeatMode) {
            RepeatMode.OFF -> Pair(Icons.Default.Repeat, AudiovTextMuted)
            RepeatMode.ALL -> Pair(Icons.Default.Repeat, AudiovSecondary)
            RepeatMode.ONE -> Pair(Icons.Default.RepeatOne, AudiovSecondary)
          }
          Icon(
            imageVector = icon,
            contentDescription = "Repeat",
            tint = tint,
            modifier = Modifier.size(24.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Bottom Control Pills: Speed, Lyrics, and Volume
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Speed Toggle Chip
        Surface(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable {
              val nextSpeed = when (playbackState.playbackSpeed) {
                1.0f -> 1.25f
                1.25f -> 1.5f
                1.5f -> 0.8f
                else -> 1.0f
              }
              onSpeedChange(nextSpeed)
            },
          color = AudiovSurfaceVariant,
          shape = RoundedCornerShape(20.dp)
        ) {
          Text(
            text = "${playbackState.playbackSpeed}x",
            color = if (playbackState.playbackSpeed != 1.0f) AudiovPrimaryGlow else AudiovTextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
          )
        }

        // Lyrics View Toggle Chip
        Surface(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onToggleLyrics() },
          color = if (isLyricsVisible) AudiovPrimary.copy(alpha = 0.2f) else AudiovSurfaceVariant,
          shape = RoundedCornerShape(20.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Lyrics,
              contentDescription = "Lyrics",
              tint = if (isLyricsVisible) AudiovPrimaryGlow else AudiovTextSecondary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Lyrics",
              color = if (isLyricsVisible) AudiovPrimaryGlow else AudiovTextSecondary,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        // Volume Bar Row
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.width(130.dp)
        ) {
          Icon(
            imageVector = Icons.Default.VolumeUp,
            contentDescription = "Volume",
            tint = AudiovTextMuted,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Slider(
            value = playbackState.volume,
            onValueChange = onVolumeChange,
            colors = SliderDefaults.colors(
              thumbColor = AudiovTextPrimary,
              activeTrackColor = AudiovSecondary,
              inactiveTrackColor = AudiovDivider
            ),
            modifier = Modifier.weight(1f)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
fun SoundwaveVisualizer(
  isPlaying: Boolean,
  modifier: Modifier = Modifier
) {
  val transition = rememberInfiniteTransition(label = "soundwave")
  val bars = 24

  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.SpaceEvenly,
    verticalAlignment = Alignment.CenterVertically
  ) {
    for (i in 0 until bars) {
      val duration = 300 + (i * 47) % 600
      val heightFraction by transition.animateFloat(
        initialValue = 0.15f,
        targetValue = if (isPlaying) (0.3f + ((i * 19) % 70) / 100f) else 0.15f,
        animationSpec = infiniteRepeatable(
          animation = tween(duration, easing = FastOutSlowInEasing),
          repeatMode = AnimRepeatMode.Reverse
        ),
        label = "wave_bar_$i"
      )

      Box(
        modifier = Modifier
          .width(3.dp)
          .height((28 * heightFraction).dp)
          .clip(RoundedCornerShape(2.dp))
          .background(
            if (i % 2 == 0) AudiovPrimary else AudiovSecondary
          )
      )
    }
  }
}

@Composable
fun LyricsView(
  lyrics: String,
  songTitle: String,
  artist: String,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .height(280.dp)
      .clip(RoundedCornerShape(20.dp)),
    color = AudiovCardElevated,
    shape = RoundedCornerShape(20.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .verticalScroll(rememberScrollState())
    ) {
      Text(
        text = "Lyrics - $songTitle",
        color = AudiovPrimaryGlow,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = artist,
        color = AudiovTextSecondary,
        fontSize = 13.sp
      )
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = lyrics,
        color = AudiovTextPrimary,
        fontSize = 14.sp,
        lineHeight = 22.sp
      )
    }
  }
}

@Composable
fun QueueView(
  queue: List<Song>,
  currentSong: Song,
  isPlaying: Boolean,
  onSelectSong: (Song) -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .height(280.dp)
      .clip(RoundedCornerShape(20.dp)),
    color = AudiovCardElevated,
    shape = RoundedCornerShape(20.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .verticalScroll(rememberScrollState())
    ) {
      Text(
        text = "Now Playing Queue (${queue.size} songs)",
        color = AudiovPrimaryGlow,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(10.dp))
      queue.forEachIndexed { idx, s ->
        val isCurrent = s.id == currentSong.id
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isCurrent) AudiovPrimary.copy(alpha = 0.15f) else Color.Transparent)
            .clickable { onSelectSong(s) }
            .padding(horizontal = 8.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "${idx + 1}.",
            color = if (isCurrent) AudiovPrimaryGlow else AudiovTextMuted,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(24.dp)
          )
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = s.title,
              color = if (isCurrent) AudiovPrimaryGlow else AudiovTextPrimary,
              fontSize = 14.sp,
              fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = s.artist,
              color = AudiovTextSecondary,
              fontSize = 12.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
          if (isCurrent && isPlaying) {
            AnimatedEqualizerBar(color = AudiovSecondary)
          } else {
            Text(
              text = s.formattedDuration,
              color = AudiovTextMuted,
              fontSize = 12.sp
            )
          }
        }
      }
    }
  }
}
