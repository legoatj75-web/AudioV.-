package com.example.model

import androidx.annotation.DrawableRes

data class Song(
  val id: String,
  val title: String,
  val artist: String,
  val album: String,
  val durationSeconds: Int,
  val streamUrl: String,
  val genre: String,
  val releaseYear: String = "2024",
  @DrawableRes val coverDrawableRes: Int? = null,
  val gradientStartHex: Long = 0xFF8B5CF6,
  val gradientEndHex: Long = 0xFF06B6D4,
  val playsCount: String = "128K",
  val isFavorite: Boolean = false,
  val lyrics: String = "Intro (Instrumental)\n\n[Verse 1]\nNeon lights in the rain\nWaves vibrating through the air\nElectric heartbeat in my veins\nLost in the sound without a care\n\n[Chorus]\nTake me higher into the sound\nWhere rhythm flows and beats rebound\nIn this sonic space we found\nWe'll never touch the ground\n\n[Solo / Drop]\n\n[Outro]\nFading waves into the night..."
) {
  val formattedDuration: String
    get() {
      val minutes = durationSeconds / 60
      val seconds = durationSeconds % 60
      return String.format("%d:%02d", minutes, seconds)
    }
}
