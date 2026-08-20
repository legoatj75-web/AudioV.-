package com.example.model

data class Playlist(
  val id: String,
  val title: String,
  val description: String,
  val coverDrawableRes: Int? = null,
  val gradientStartHex: Long = 0xFF8B5CF6,
  val gradientEndHex: Long = 0xFFEC4899,
  val songs: List<Song> = emptyList()
) {
  val songCount: Int
    get() = songs.size
}
