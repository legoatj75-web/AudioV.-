package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.MusicRepository
import com.example.model.PlaybackState
import com.example.model.Playlist
import com.example.model.Song
import com.example.ui.components.SongRow
import com.example.ui.theme.AudiovBackground
import com.example.ui.theme.AudiovCard
import com.example.ui.theme.AudiovCardElevated
import com.example.ui.theme.AudiovPrimary
import com.example.ui.theme.AudiovPrimaryGlow
import com.example.ui.theme.AudiovSecondary
import com.example.ui.theme.AudiovSurfaceVariant
import com.example.ui.theme.AudiovTertiary
import com.example.ui.theme.AudiovTextMuted
import com.example.ui.theme.AudiovTextPrimary
import com.example.ui.theme.AudiovTextSecondary
import com.example.viewmodel.AudiovNavTab

@Composable
fun HomeScreen(
  allSongs: List<Song>,
  playlists: List<Playlist>,
  selectedGenre: String,
  playbackState: PlaybackState,
  favoriteSongIds: Set<String>,
  onGenreSelect: (String) -> Unit,
  onSongClick: (Song, List<Song>) -> Unit,
  onPlaylistClick: (Playlist) -> Unit,
  onFavoriteClick: (String) -> Unit,
  onNavigateToSearch: () -> Unit,
  modifier: Modifier = Modifier
) {
  val filteredSongs = if (selectedGenre == "All") {
    allSongs
  } else {
    allSongs.filter { it.genre.equals(selectedGenre, ignoreCase = true) }
  }

  val featuredSong = allSongs.firstOrNull()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(AudiovBackground)
      .testTag("home_screen_scroll"),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    // Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(
                Brush.linearGradient(listOf(AudiovPrimary, AudiovSecondary))
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.GraphicEq,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Audiov",
              color = AudiovTextPrimary,
              fontSize = 20.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 0.5.sp
            )
            Text(
              text = "Music Streaming v0.1",
              color = AudiovTextMuted,
              fontSize = 11.sp
            )
          }
        }

        IconButton(
          onClick = onNavigateToSearch,
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(AudiovSurfaceVariant)
            .testTag("home_search_button")
        ) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = AudiovTextPrimary,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }

    // Featured Release Hero Banner
    if (featuredSong != null) {
      item {
        FeaturedHeroCard(
          song = featuredSong,
          isPlaying = playbackState.isPlaying && playbackState.currentSong?.id == featuredSong.id,
          onClick = { onSongClick(featuredSong, allSongs) },
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
      }
    }

    // Genre Filters
    item {
      LazyRow(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(MusicRepository.genres) { genre ->
          val isSelected = genre == selectedGenre
          FilterChip(
            selected = isSelected,
            onClick = { onGenreSelect(genre) },
            label = {
              Text(
                text = genre,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = AudiovPrimary,
              selectedLabelColor = AudiovTextPrimary,
              containerColor = AudiovSurfaceVariant,
              labelColor = AudiovTextSecondary
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.testTag("genre_chip_$genre")
          )
        }
      }
    }

    // Featured Playlists Carousel
    item {
      Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 8.dp)) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Curated Mixes",
            color = AudiovTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "${playlists.size} playlists",
            color = AudiovPrimaryGlow,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
          modifier = Modifier.fillMaxWidth(),
          contentPadding = PaddingValues(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          items(playlists) { playlist ->
            PlaylistCard(
              playlist = playlist,
              onClick = { onPlaylistClick(playlist) }
            )
          }
        }
      }
    }

    // Trending Tracks Section Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (selectedGenre == "All") "Trending Tracks" else "$selectedGenre Tracks",
          color = AudiovTextPrimary,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "${filteredSongs.size} songs",
          color = AudiovTextMuted,
          fontSize = 12.sp
        )
      }
    }

    // Song Rows List
    itemsIndexed(filteredSongs) { index, song ->
      val isCurrentSong = playbackState.currentSong?.id == song.id
      val isPlayingThis = isCurrentSong && playbackState.isPlaying
      val isFav = favoriteSongIds.contains(song.id)

      SongRow(
        song = song,
        index = index,
        isPlayingThisSong = isPlayingThis,
        isCurrentSong = isCurrentSong,
        isFavorite = isFav,
        onPlayClick = { onSongClick(song, filteredSongs) },
        onFavoriteClick = { onFavoriteClick(song.id) },
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
      )
    }
  }
}

@Composable
fun FeaturedHeroCard(
  song: Song,
  isPlaying: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .height(180.dp)
      .clip(RoundedCornerShape(20.dp))
      .clickable { onClick() }
      .testTag("featured_hero_card"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = AudiovCard)
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      // Background Image / Gradient
      if (song.coverDrawableRes != null) {
        Image(
          painter = painterResource(id = song.coverDrawableRes),
          contentDescription = song.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
      }

      // Dark gradient overlay
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.horizontalGradient(
              colors = listOf(
                Color.Black.copy(alpha = 0.88f),
                Color.Black.copy(alpha = 0.55f),
                Color.Transparent
              )
            )
          )
      )

      // Content inside banner
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AudiovPrimary.copy(alpha = 0.4f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.ElectricBolt,
            contentDescription = null,
            tint = AudiovSecondary,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "FEATURED RELEASE",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = song.title,
              color = Color.White,
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "${song.artist} • ${song.genre}",
              color = AudiovTextSecondary,
              fontSize = 13.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }

          Box(
            modifier = Modifier
              .size(48.dp)
              .shadow(12.dp, CircleShape, spotColor = AudiovPrimary)
              .clip(CircleShape)
              .background(AudiovPrimary),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isPlaying) Icons.Default.GraphicEq else Icons.Default.PlayArrow,
              contentDescription = "Play Featured",
              tint = Color.White,
              modifier = Modifier.size(28.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun PlaylistCard(
  playlist: Playlist,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .width(140.dp)
      .clip(RoundedCornerShape(16.dp))
      .clickable { onClick() }
      .testTag("playlist_card_${playlist.id}")
  ) {
    Box(
      modifier = Modifier
        .size(140.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(
          Brush.linearGradient(
            listOf(
              Color(playlist.gradientStartHex),
              Color(playlist.gradientEndHex)
            )
          )
        ),
      contentAlignment = Alignment.Center
    ) {
      if (playlist.coverDrawableRes != null) {
        Image(
          painter = painterResource(id = playlist.coverDrawableRes),
          contentDescription = playlist.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
      }
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.25f))
      )
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(AudiovPrimary.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.PlayArrow,
          contentDescription = "Play Mix",
          tint = Color.White,
          modifier = Modifier.size(20.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = playlist.title,
      color = AudiovTextPrimary,
      fontSize = 13.sp,
      fontWeight = FontWeight.Bold,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
    Text(
      text = "${playlist.songCount} tracks",
      color = AudiovTextMuted,
      fontSize = 11.sp,
      maxLines = 1
    )
  }
}
