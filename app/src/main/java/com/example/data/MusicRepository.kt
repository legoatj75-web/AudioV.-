package com.example.data

import com.example.R
import com.example.model.Playlist
import com.example.model.Song

object MusicRepository {

  val sampleSongs: List<Song> = listOf(
    Song(
      id = "song_1",
      title = "Midnight Horizon",
      artist = "Kavinsky Wave",
      album = "Outrun Cyberpunk",
      durationSeconds = 214,
      streamUrl = "https://cdn.pixabay.com/download/audio/2022/05/27/audio_1808fbf07a.mp3?filename=synthwave-80s-110045.mp3",
      genre = "Synthwave",
      releaseYear = "2024",
      coverDrawableRes = R.drawable.album_neon_synthwave,
      gradientStartHex = 0xFF8B5CF6,
      gradientEndHex = 0xFFEC4899,
      playsCount = "1.4M",
      lyrics = "Intro - Synthesizer crescendo\n\n[Verse 1]\nStreets of chrome under neon skies\nReflecting the sunset in your eyes\nEngine humming, the road unwinds\nLeaving all the ghosts behind\n\n[Chorus]\nMidnight horizon, take me away\nBeyond the darkness into the day\nWe drive until the morning light\nEndless roads in the synthwave night\n\n[Drop / Retro Solo]\n\n[Verse 2]\nDigital dreams in eighty-four\nNothing to lose anymore\nTurn up the speed, let the bass line roll\nElectric rhythm inside my soul\n\n[Outro]\nMidnight horizon... fading out."
    ),
    Song(
      id = "song_2",
      title = "Tokyo Rain Cafe",
      artist = "Lofi Boy & Neko",
      album = "Coffee & Beats Vol. 2",
      durationSeconds = 178,
      streamUrl = "https://cdn.pixabay.com/download/audio/2022/01/18/audio_d0a13f69d2.mp3?filename=chill-abstract-intention-12099.mp3",
      genre = "Lo-Fi",
      releaseYear = "2024",
      coverDrawableRes = R.drawable.album_lofi_midnight,
      gradientStartHex = 0xFFF59E0B,
      gradientEndHex = 0xFFD97706,
      playsCount = "890K",
      lyrics = "Rain drops on the window sill\nClock ticking slow and still\nSteam rising from the porcelain cup\nNo hurry to wake up\n\nWarm chords, gentle rhodes\nWalking along quiet roads\nCat purring by the side\nIn this calm we abide."
    ),
    Song(
      id = "song_3",
      title = "Cybernetic Pulse",
      artist = "Aetherium & DJ Null",
      album = "Neon Matrix",
      durationSeconds = 246,
      streamUrl = "https://cdn.pixabay.com/download/audio/2022/03/15/audio_c8bbf3b764.mp3?filename=electronic-future-beats-117997.mp3",
      genre = "Electronic",
      releaseYear = "2024",
      coverDrawableRes = R.drawable.album_cyber_pulse,
      gradientStartHex = 0xFF06B6D4,
      gradientEndHex = 0xFF3B82F6,
      playsCount = "2.1M",
      lyrics = "[Build up]\n0101 frequencies aligning\nSub-bass resonance rising\nQuantum oscillator engaged\n\n[Drop]\nPULSE - SURGE - VIBRATION\nFeel the neural connection\nBass frequency 140 BPM\nEnergy peaking in the matrix\n\n[Outro]\nSystem nominal."
    ),
    Song(
      id = "song_4",
      title = "Starlight Echoes",
      artist = "Celeste Aura",
      album = "Nebula Dreams",
      durationSeconds = 195,
      streamUrl = "https://cdn.pixabay.com/download/audio/2022/10/14/audio_9939f792cb.mp3?filename=ambient-space-piano-124443.mp3",
      genre = "Ambient",
      releaseYear = "2023",
      coverDrawableRes = R.drawable.album_neon_synthwave,
      gradientStartHex = 0xFF6366F1,
      gradientEndHex = 0xFFA855F7,
      playsCount = "450K",
      lyrics = "Floating through the cosmic sea\nDrifting light and gravity\nGentle pads and sparkling stars\nTraveling beyond planet Mars\n\nPeaceful silence in the deep\nCosmic melodies to help you sleep."
    ),
    Song(
      id = "song_5",
      title = "Sunset Boulevard Drive",
      artist = "The Retrograde",
      album = "Pacific Coast Highway",
      durationSeconds = 228,
      streamUrl = "https://cdn.pixabay.com/download/audio/2022/01/26/audio_d0c6ff1101.mp3?filename=tomp-80s-retro-12349.mp3",
      genre = "Synthwave",
      releaseYear = "2024",
      coverDrawableRes = R.drawable.album_neon_synthwave,
      gradientStartHex = 0xFFF43F5E,
      gradientEndHex = 0xFFFB923C,
      playsCount = "1.1M",
      lyrics = "Golden hour on the coast\nMemories we loved the most\nPalm trees swaying in the breeze\nCruising down with effortless ease\n\nTake the wheel, don't look back\nSunset glowing down the track."
    ),
    Song(
      id = "song_6",
      title = "Study Session Chill",
      artist = "Bonsai Tree",
      album = "Lo-Fi Beats to Relax To",
      durationSeconds = 162,
      streamUrl = "https://cdn.pixabay.com/download/audio/2021/08/04/audio_bb630cc098.mp3?filename=lofi-study-112191.mp3",
      genre = "Lo-Fi",
      releaseYear = "2024",
      coverDrawableRes = R.drawable.album_lofi_midnight,
      gradientStartHex = 0xFF10B981,
      gradientEndHex = 0xFF14B8A6,
      playsCount = "720K",
      lyrics = "Notebook open, pencil ready\nKeeping focus calm and steady\nVinyl crackle, gentle drum\nWatching as the thoughts all hum."
    ),
    Song(
      id = "song_7",
      title = "Hyperdrive Accelerate",
      artist = "Vortex Protocol",
      album = "Speed of Light",
      durationSeconds = 205,
      streamUrl = "https://cdn.pixabay.com/download/audio/2022/02/07/audio_c36e4fbb61.mp3?filename=futuristic-beat-11244.mp3",
      genre = "Electronic",
      releaseYear = "2024",
      coverDrawableRes = R.drawable.album_cyber_pulse,
      gradientStartHex = 0xFF3B82F6,
      gradientEndHex = 0xFF8B5CF6,
      playsCount = "1.8M",
      lyrics = "Ignition 3, 2, 1...\nSpeed barrier broken\nWarp speed initiated\nLight bending around the hull\nUnstoppable kinetic energy."
    ),
    Song(
      id = "song_8",
      title = "Velvet Midnight Jazz",
      artist = "Miles Duo",
      album = "Blue Note Reverie",
      durationSeconds = 230,
      streamUrl = "https://cdn.pixabay.com/download/audio/2022/05/16/audio_db5975db34.mp3?filename=smooth-jazz-night-111054.mp3",
      genre = "Chill",
      releaseYear = "2023",
      coverDrawableRes = R.drawable.album_lofi_midnight,
      gradientStartHex = 0xFF84CC16,
      gradientEndHex = 0xFF10B981,
      playsCount = "380K",
      lyrics = "Muted trumpet in the dark\nQuiet footsteps in the park\nSmooth upright bass line walk\nUnder moonlight when we talk."
    )
  )

  val samplePlaylists: List<Playlist> = listOf(
    Playlist(
      id = "pl_1",
      title = "Midnight Synthwave",
      description = "High-octane synth bass & retro futuristic neon vibes for late night cruising.",
      coverDrawableRes = R.drawable.album_neon_synthwave,
      gradientStartHex = 0xFF8B5CF6,
      gradientEndHex = 0xFFEC4899,
      songs = listOf(sampleSongs[0], sampleSongs[4], sampleSongs[2], sampleSongs[6])
    ),
    Playlist(
      id = "pl_2",
      title = "Focus & Lo-Fi Flow",
      description = "Soothing lofi beats, rain sounds, and mellow guitar chords for deep study.",
      coverDrawableRes = R.drawable.album_lofi_midnight,
      gradientStartHex = 0xFFF59E0B,
      gradientEndHex = 0xFFD97706,
      songs = listOf(sampleSongs[1], sampleSongs[5], sampleSongs[7], sampleSongs[3])
    ),
    Playlist(
      id = "pl_3",
      title = "Cyber Electronic Rave",
      description = "Heart-thumping EDM, heavy bass drops, and cybernetic rhythmic energy.",
      coverDrawableRes = R.drawable.album_cyber_pulse,
      gradientStartHex = 0xFF06B6D4,
      gradientEndHex = 0xFF3B82F6,
      songs = listOf(sampleSongs[2], sampleSongs[6], sampleSongs[0], sampleSongs[4])
    ),
    Playlist(
      id = "pl_4",
      title = "Deep Ambient Space",
      description = "Ethereal soundscapes, binaural tones, and peaceful cosmic pads for relaxation.",
      coverDrawableRes = R.drawable.album_neon_synthwave,
      gradientStartHex = 0xFF6366F1,
      gradientEndHex = 0xFFA855F7,
      songs = listOf(sampleSongs[3], sampleSongs[1], sampleSongs[7])
    )
  )

  val genres: List<String> = listOf(
    "All", "Synthwave", "Lo-Fi", "Electronic", "Ambient", "Chill"
  )
}
