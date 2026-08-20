package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Song
import com.example.ui.theme.AudiovCardElevated
import com.example.ui.theme.AudiovPrimary
import com.example.ui.theme.AudiovPrimaryGlow
import com.example.ui.theme.AudiovSecondary
import com.example.ui.theme.AudiovSurfaceVariant
import com.example.ui.theme.AudiovTertiary
import com.example.ui.theme.AudiovTextMuted
import com.example.ui.theme.AudiovTextPrimary
import com.example.ui.theme.AudiovTextSecondary

@Composable
fun SongRow(
  song: Song,
  index: Int,
  isPlayingThisSong: Boolean,
  isCurrentSong: Boolean,
  isFavorite: Boolean,
  onPlayClick: () -> Unit,
  onFavoriteClick: () -> Unit,
  onViewLyrics: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var menuExpanded by remember { mutableStateOf(false) }

  val bgModifier = if (isCurrentSong) {
    Modifier.background(
      color = AudiovPrimary.copy(alpha = 0.12f),
      shape = RoundedCornerShape(12.dp)
    )
  } else {
    Modifier
  }

  Row(
    modifier = modifier
      .fillMaxWidth()
      .then(bgModifier)
      .clip(RoundedCornerShape(12.dp))
      .clickable { onPlayClick() }
      .padding(horizontal = 12.dp, vertical = 8.dp)
      .testTag("song_row_${song.id}"),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Artwork thumbnail or gradient box
    Box(
      modifier = Modifier
        .size(52.dp)
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
          modifier = Modifier.size(52.dp)
        )
      }

      if (isCurrentSong) {
        Box(
          modifier = Modifier
            .size(52.dp)
            .background(Color.Black.copy(alpha = 0.45f)),
          contentAlignment = Alignment.Center
        ) {
          if (isPlayingThisSong) {
            AnimatedEqualizerBar(color = AudiovSecondary)
          } else {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "Playing",
              tint = AudiovSecondary,
              modifier = Modifier.size(24.dp)
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.width(14.dp))

    // Song info (Title & Artist)
    Column(
      modifier = Modifier
        .weight(1f)
        .padding(end = 8.dp),
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        text = song.title,
        color = if (isCurrentSong) AudiovPrimaryGlow else AudiovTextPrimary,
        fontSize = 15.sp,
        fontWeight = if (isCurrentSong) FontWeight.Bold else FontWeight.SemiBold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Spacer(modifier = Modifier.height(2.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = song.artist,
          color = AudiovTextSecondary,
          fontSize = 13.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = " • ",
          color = AudiovTextMuted,
          fontSize = 12.sp
        )
        Text(
          text = song.formattedDuration,
          color = AudiovTextMuted,
          fontSize = 12.sp
        )
      }
    }

    // Favorite button
    IconButton(
      onClick = onFavoriteClick,
      modifier = Modifier
        .size(40.dp)
        .testTag("favorite_button_${song.id}")
    ) {
      Icon(
        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
        contentDescription = if (isFavorite) "Unlike" else "Like",
        tint = if (isFavorite) AudiovTertiary else AudiovTextMuted,
        modifier = Modifier.size(20.dp)
      )
    }

    // Menu overflow
    Box {
      IconButton(
        onClick = { menuExpanded = true },
        modifier = Modifier.size(36.dp)
      ) {
        Icon(
          imageVector = Icons.Default.MoreVert,
          contentDescription = "Options",
          tint = AudiovTextMuted,
          modifier = Modifier.size(18.dp)
        )
      }

      DropdownMenu(
        expanded = menuExpanded,
        onDismissRequest = { menuExpanded = false },
        modifier = Modifier.background(AudiovCardElevated)
      ) {
        DropdownMenuItem(
          text = { Text("Play Now", color = AudiovTextPrimary) },
          onClick = {
            menuExpanded = false
            onPlayClick()
          }
        )
        DropdownMenuItem(
          text = { Text("View Lyrics", color = AudiovTextPrimary) },
          onClick = {
            menuExpanded = false
            onViewLyrics()
          }
        )
        DropdownMenuItem(
          text = {
            Text(
              if (isFavorite) "Remove from Favorites" else "Add to Favorites",
              color = AudiovTextPrimary
            )
          },
          onClick = {
            menuExpanded = false
            onFavoriteClick()
          }
        )
      }
    }
  }
}

@Composable
fun AnimatedEqualizerBar(
  color: Color = AudiovSecondary,
  modifier: Modifier = Modifier
) {
  val transition = rememberInfiniteTransition(label = "eq_transition")

  val bar1 by transition.animateFloat(
    initialValue = 0.2f,
    targetValue = 0.9f,
    animationSpec = infiniteRepeatable(
      animation = tween(400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "bar1"
  )

  val bar2 by transition.animateFloat(
    initialValue = 0.8f,
    targetValue = 0.3f,
    animationSpec = infiniteRepeatable(
      animation = tween(550, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "bar2"
  )

  val bar3 by transition.animateFloat(
    initialValue = 0.3f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(350, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "bar3"
  )

  Row(
    modifier = modifier
      .size(24.dp)
      .padding(horizontal = 2.dp),
    horizontalArrangement = Arrangement.spacedBy(2.5.dp),
    verticalAlignment = Alignment.Bottom
  ) {
    Box(
      modifier = Modifier
        .width(3.dp)
        .fillMaxHeight(bar1)
        .clip(RoundedCornerShape(2.dp))
        .background(color)
    )
    Box(
      modifier = Modifier
        .width(3.dp)
        .fillMaxHeight(bar3)
        .clip(RoundedCornerShape(2.dp))
        .background(color)
    )
    Box(
      modifier = Modifier
        .width(3.dp)
        .fillMaxHeight(bar2)
        .clip(RoundedCornerShape(2.dp))
        .background(color)
    )
  }
}
