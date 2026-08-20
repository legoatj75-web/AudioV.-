package com.example.ui.screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PlaybackState
import com.example.model.Song
import com.example.ui.components.SongRow
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

data class CategoryCardItem(
  val title: String,
  val startHex: Long,
  val endHex: Long
)

val searchCategories = listOf(
  CategoryCardItem("Synthwave & 80s", 0xFF8B5CF6, 0xFFEC4899),
  CategoryCardItem("Lo-Fi Study Beats", 0xFFF59E0B, 0xFFD97706),
  CategoryCardItem("Electronic & EDM", 0xFF06B6D4, 0xFF3B82F6),
  CategoryCardItem("Deep Ambient", 0xFF6366F1, 0xFFA855F7),
  CategoryCardItem("Chill & Coffee", 0xFF10B981, 0xFF14B8A6),
  CategoryCardItem("Cyberpunk Wave", 0xFFF43F5E, 0xFFFB923C)
)

@Composable
fun SearchScreen(
  allSongs: List<Song>,
  searchQuery: String,
  playbackState: PlaybackState,
  favoriteSongIds: Set<String>,
  onSearchQueryChange: (String) -> Unit,
  onSongClick: (Song, List<Song>) -> Unit,
  onFavoriteClick: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val searchResults = if (searchQuery.isBlank()) {
    emptyList()
  } else {
    allSongs.filter {
      it.title.contains(searchQuery, ignoreCase = true) ||
      it.artist.contains(searchQuery, ignoreCase = true) ||
      it.album.contains(searchQuery, ignoreCase = true) ||
      it.genre.contains(searchQuery, ignoreCase = true)
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(AudiovBackground)
      .padding(horizontal = 16.dp)
      .testTag("search_screen")
  ) {
    Text(
      text = "Search",
      color = AudiovTextPrimary,
      fontSize = 24.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
    )

    // Search Input Field
    OutlinedTextField(
      value = searchQuery,
      onValueChange = onSearchQueryChange,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("search_input_field"),
      placeholder = { Text("Songs, artists, or genres...", color = AudiovTextMuted) },
      leadingIcon = {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = "Search",
          tint = AudiovPrimaryGlow
        )
      },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { onSearchQueryChange("") }) {
            Icon(
              imageVector = Icons.Default.Clear,
              contentDescription = "Clear search",
              tint = AudiovTextSecondary
            )
          }
        }
      },
      singleLine = true,
      shape = RoundedCornerShape(16.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = AudiovSurfaceVariant,
        unfocusedContainerColor = AudiovSurfaceVariant,
        focusedBorderColor = AudiovPrimary,
        unfocusedBorderColor = AudiovDivider,
        focusedTextColor = AudiovTextPrimary,
        unfocusedTextColor = AudiovTextPrimary,
        cursorColor = AudiovPrimaryGlow
      )
    )

    Spacer(modifier = Modifier.height(16.dp))

    if (searchQuery.isNotBlank()) {
      // Show Search Results
      if (searchResults.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 90.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Default.SearchOff,
              contentDescription = null,
              tint = AudiovTextMuted,
              modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "No songs found for \"$searchQuery\"",
              color = AudiovTextSecondary,
              fontSize = 15.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      } else {
        Text(
          text = "Results (${searchResults.size})",
          color = AudiovTextSecondary,
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(bottom = 90.dp)
        ) {
          itemsIndexed(searchResults) { index, song ->
            val isCurrentSong = playbackState.currentSong?.id == song.id
            val isPlayingThis = isCurrentSong && playbackState.isPlaying
            val isFav = favoriteSongIds.contains(song.id)

            SongRow(
              song = song,
              index = index,
              isPlayingThisSong = isPlayingThis,
              isCurrentSong = isCurrentSong,
              isFavorite = isFav,
              onPlayClick = { onSongClick(song, searchResults) },
              onFavoriteClick = { onFavoriteClick(song.id) },
              modifier = Modifier.padding(vertical = 2.dp)
            )
          }
        }
      }
    } else {
      // Explore Categories
      Text(
        text = "Explore Categories",
        color = AudiovTextPrimary,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp)
      )

      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 90.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(searchCategories) { cat ->
          Card(
            modifier = Modifier
              .height(90.dp)
              .clip(RoundedCornerShape(14.dp))
              .clickable { onSearchQueryChange(cat.title.split(" ").first()) }
              .testTag("search_category_${cat.title}"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
          ) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(
                  Brush.linearGradient(
                    listOf(Color(cat.startHex), Color(cat.endHex))
                  )
                )
                .padding(12.dp),
              contentAlignment = Alignment.BottomStart
            ) {
              Text(
                text = cat.title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }
  }
}
