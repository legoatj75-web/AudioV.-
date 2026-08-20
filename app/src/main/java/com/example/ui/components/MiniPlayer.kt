package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PlaybackState
import com.example.ui.theme.AudiovBackground
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

@Composable
fun MiniPlayer(
  playbackState: PlaybackState,
  isFavorite: Boolean,
  onPlayPauseClick: () -> Unit,
  onSkipPreviousClick: () -> Unit,
  onSkipNextClick: () -> Unit,
  onFavoriteClick: () -> Unit,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val song = playbackState.currentSong

  AnimatedVisibility(
    visible = song != null,
    enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(300)) + fadeIn(),
    exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(200)) + fadeOut()
  ) {
    if (song != null) {
      Surface(
        modifier = modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 4.dp)
          .shadow(16.dp, RoundedCornerShape(16.dp), spotColor = AudiovPrimary.copy(alpha = 0.35f))
          .clip(RoundedCornerShape(16.dp))
          .clickable { onClick() }
          .testTag("mini_player_container"),
        color = AudiovCardElevated,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 6.dp
      ) {
        Column(modifier = Modifier.fillMaxWidth()) {
          // Top continuous thin progress bar
          LinearProgressIndicator(
            progress = { playbackState.progressFraction },
            modifier = Modifier
              .fillMaxWidth()
              .height(2.5.dp),
            color = AudiovPrimaryGlow,
            trackColor = AudiovDivider,
          )

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Album Artwork thumbnail
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
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
                  modifier = Modifier.size(44.dp)
                )
              }

              if (playbackState.isPlaying) {
                Box(
                  modifier = Modifier
                    .size(44.dp)
                    .background(Color.Black.copy(alpha = 0.35f)),
                  contentAlignment = Alignment.Center
                ) {
                  AnimatedEqualizerBar(color = Color.White, modifier = Modifier.size(18.dp))
                }
              }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Concise Marquee-animated Text Row for Song Title and Artist
            Row(
              modifier = Modifier
                .weight(1f)
                .basicMarquee(
                  iterations = Int.MAX_VALUE,
                  initialDelayMillis = 1000,
                  velocity = 32.dp
                )
                .padding(end = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = song.title,
                color = AudiovTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
              )
              Text(
                text = "  •  ",
                color = AudiovPrimaryGlow,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
              )
              Text(
                text = song.artist,
                color = AudiovTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
              )
              if (song.genre.isNotBlank()) {
                Text(
                  text = "  (${song.genre})",
                  color = AudiovTextMuted,
                  fontSize = 11.sp,
                  maxLines = 1
                )
              }
            }

            // Controls group: Favorite, Previous, Play/Pause, Next
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
              // Favorite button
              IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                  .size(34.dp)
                  .testTag("mini_player_favorite_button")
              ) {
                Icon(
                  imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                  contentDescription = if (isFavorite) "Unlike" else "Like",
                  tint = if (isFavorite) AudiovTertiary else AudiovTextMuted,
                  modifier = Modifier.size(19.dp)
                )
              }

              // Previous Track button (⏮️ to the left of Play/Pause)
              IconButton(
                onClick = onSkipPreviousClick,
                modifier = Modifier
                  .size(34.dp)
                  .testTag("mini_player_skip_previous_button")
              ) {
                Icon(
                  imageVector = Icons.Default.SkipPrevious,
                  contentDescription = "Previous Track",
                  tint = AudiovTextPrimary,
                  modifier = Modifier.size(22.dp)
                )
              }

              // Play / Pause Button
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(AudiovPrimary)
                  .clickable { onPlayPauseClick() }
                  .testTag("mini_player_play_pause_button"),
                contentAlignment = Alignment.Center
              ) {
                if (playbackState.isBuffering) {
                  CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = AudiovTextPrimary,
                    strokeWidth = 2.dp
                  )
                } else {
                  Icon(
                    imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                    tint = AudiovTextPrimary,
                    modifier = Modifier.size(22.dp)
                  )
                }
              }

              // Skip Next button
              IconButton(
                onClick = onSkipNextClick,
                modifier = Modifier
                  .size(34.dp)
                  .testTag("mini_player_skip_next_button")
              ) {
                Icon(
                  imageVector = Icons.Default.SkipNext,
                  contentDescription = "Next Track",
                  tint = AudiovTextPrimary,
                  modifier = Modifier.size(22.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}
