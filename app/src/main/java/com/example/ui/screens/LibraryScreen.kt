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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.model.PlaybackState
import com.example.model.Playlist
import com.example.model.Song
import com.example.ui.components.SongRow
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

@Composable
fun LibraryScreen(
  allSongs: List<Song>,
  playlists: List<Playlist>,
  favoriteSongIds: Set<String>,
  playbackState: PlaybackState,
  onSongClick: (Song, List<Song>) -> Unit,
  onPlaylistClick: (Playlist) -> Unit,
  onFavoriteClick: (String) -> Unit,
  onCreatePlaylist: (String, String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTabIndex by remember { mutableIntStateOf(0) }
  var showCreateDialog by remember { mutableStateOf(false) }
  var newPlaylistTitle by remember { mutableStateOf("") }
  var newPlaylistDesc by remember { mutableStateOf("") }

  val favoriteSongs = allSongs.filter { favoriteSongIds.contains(it.id) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(AudiovBackground)
      .testTag("library_screen")
  ) {
    // Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Your Library",
        color = AudiovTextPrimary,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
      )

      IconButton(
        onClick = { showCreateDialog = true },
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(AudiovPrimary)
          .testTag("create_playlist_button")
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = "New Playlist",
          tint = Color.White,
          modifier = Modifier.size(22.dp)
        )
      }
    }

    // Tabs: Playlists / Liked Songs / All Tracks
    val tabs = listOf("Playlists (${playlists.size})", "Liked Songs (${favoriteSongs.size})", "All Songs")
    TabRow(
      selectedTabIndex = selectedTabIndex,
      containerColor = AudiovBackground,
      contentColor = AudiovPrimaryGlow,
      indicator = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
          modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
          color = AudiovPrimary
        )
      },
      divider = { Box(modifier = Modifier.height(1.dp).background(AudiovDivider)) }
    ) {
      tabs.forEachIndexed { index, title ->
        Tab(
          selected = selectedTabIndex == index,
          onClick = { selectedTabIndex = index },
          text = {
            Text(
              text = title,
              fontSize = 13.sp,
              fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
              color = if (selectedTabIndex == index) AudiovPrimaryGlow else AudiovTextMuted
            )
          }
        )
      }
    }

    // Content based on tab
    when (selectedTabIndex) {
      0 -> {
        // Playlists tab
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
          // Liked Songs Quick Card
          item {
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable {
                  if (favoriteSongs.isNotEmpty()) {
                    onSongClick(favoriteSongs.first(), favoriteSongs)
                  }
                },
              colors = CardDefaults.cardColors(containerColor = AudiovCardElevated),
              shape = RoundedCornerShape(16.dp)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                      Brush.linearGradient(listOf(AudiovTertiary, AudiovPrimary))
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                  )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "Liked Songs",
                    color = AudiovTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "${favoriteSongs.size} favorite tracks",
                    color = AudiovTextSecondary,
                    fontSize = 13.sp
                  )
                }

                Box(
                  modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AudiovPrimary),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play Liked",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                  )
                }
              }
            }
          }

          items(playlists) { pl ->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(14.dp))
                .clickable { onPlaylistClick(pl) }
                .testTag("playlist_item_${pl.id}"),
              colors = CardDefaults.cardColors(containerColor = AudiovCard),
              shape = RoundedCornerShape(14.dp)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                      Brush.linearGradient(listOf(Color(pl.gradientStartHex), Color(pl.gradientEndHex)))
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  if (pl.coverDrawableRes != null) {
                    Image(
                      painter = painterResource(id = pl.coverDrawableRes),
                      contentDescription = pl.title,
                      contentScale = ContentScale.Crop,
                      modifier = Modifier.fillMaxSize()
                    )
                  }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = pl.title,
                    color = AudiovTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = pl.description,
                    color = AudiovTextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = "${pl.songCount} tracks",
                    color = AudiovPrimaryGlow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                  )
                }
              }
            }
          }

          item { Spacer(modifier = Modifier.height(80.dp)) }
        }
      }

      1 -> {
        // Liked Songs Tab
        if (favoriteSongs.isEmpty()) {
          Box(
            modifier = Modifier.fillMaxSize().padding(bottom = 90.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = AudiovTextMuted,
                modifier = Modifier.size(56.dp)
              )
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "No liked songs yet",
                color = AudiovTextSecondary,
                fontSize = 15.sp
              )
            }
          }
        } else {
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
          ) {
            itemsIndexed(favoriteSongs) { index, song ->
              val isCurrent = playbackState.currentSong?.id == song.id
              SongRow(
                song = song,
                index = index,
                isPlayingThisSong = isCurrent && playbackState.isPlaying,
                isCurrentSong = isCurrent,
                isFavorite = true,
                onPlayClick = { onSongClick(song, favoriteSongs) },
                onFavoriteClick = { onFavoriteClick(song.id) },
                modifier = Modifier.padding(vertical = 2.dp)
              )
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
          }
        }
      }

      2 -> {
        // All Songs Tab
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
        ) {
          itemsIndexed(allSongs) { index, song ->
            val isCurrent = playbackState.currentSong?.id == song.id
            val isFav = favoriteSongIds.contains(song.id)
            SongRow(
              song = song,
              index = index,
              isPlayingThisSong = isCurrent && playbackState.isPlaying,
              isCurrentSong = isCurrent,
              isFavorite = isFav,
              onPlayClick = { onSongClick(song, allSongs) },
              onFavoriteClick = { onFavoriteClick(song.id) },
              modifier = Modifier.padding(vertical = 2.dp)
            )
          }
          item { Spacer(modifier = Modifier.height(80.dp)) }
        }
      }
    }
  }

  // Create Playlist Dialog
  if (showCreateDialog) {
    AlertDialog(
      onDismissRequest = { showCreateDialog = false },
      containerColor = AudiovCardElevated,
      title = {
        Text("Create Playlist", color = AudiovTextPrimary, fontWeight = FontWeight.Bold)
      },
      text = {
        Column {
          OutlinedTextField(
            value = newPlaylistTitle,
            onValueChange = { newPlaylistTitle = it },
            label = { Text("Playlist Name") },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = AudiovPrimary,
              unfocusedBorderColor = AudiovDivider,
              focusedTextColor = AudiovTextPrimary,
              unfocusedTextColor = AudiovTextPrimary
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = newPlaylistDesc,
            onValueChange = { newPlaylistDesc = it },
            label = { Text("Description") },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = AudiovPrimary,
              unfocusedBorderColor = AudiovDivider,
              focusedTextColor = AudiovTextPrimary,
              unfocusedTextColor = AudiovTextPrimary
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (newPlaylistTitle.isNotBlank()) {
              onCreatePlaylist(newPlaylistTitle, newPlaylistDesc)
              newPlaylistTitle = ""
              newPlaylistDesc = ""
              showCreateDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = AudiovPrimary)
        ) {
          Text("Create", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { showCreateDialog = false }) {
          Text("Cancel", color = AudiovTextMuted)
        }
      }
    )
  }
}
