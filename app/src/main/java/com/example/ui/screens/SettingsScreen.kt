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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.model.StreamingQuality
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

val eqPresets = listOf("Electronic Glow", "Deep Bass Boost", "Vocal Clarity", "Chill Lo-Fi", "Flat Studio")
val sleepTimerOptions = listOf(null to "Off", 15 to "15m", 30 to "30m", 45 to "45m", 60 to "60m")

@Composable
fun SettingsScreen(
  streamingQuality: StreamingQuality,
  crossfadeSeconds: Int,
  bassBoostEnabled: Boolean,
  equalizerPreset: String,
  sleepTimerMinutes: Int?,
  onQualityChange: (StreamingQuality) -> Unit,
  onCrossfadeChange: (Int) -> Unit,
  onBassBoostToggle: () -> Unit,
  onEqualizerPresetChange: (String) -> Unit,
  onSleepTimerChange: (Int?) -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(AudiovBackground)
      .testTag("settings_screen"),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
  ) {
    item {
      Text(
        text = "Audio & Settings",
        color = AudiovTextPrimary,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp)
      )
    }

    // Streaming Quality Section
    item {
      SettingsCard(title = "Streaming Quality", icon = Icons.Default.HighQuality) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          StreamingQuality.values().forEach { quality ->
            val isSelected = quality == streamingQuality
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(if (isSelected) AudiovPrimary.copy(alpha = 0.15f) else AudiovSurfaceVariant)
                .clickable { onQualityChange(quality) }
                .padding(horizontal = 14.dp, vertical = 10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = quality.label,
                  color = if (isSelected) AudiovPrimaryGlow else AudiovTextPrimary,
                  fontSize = 14.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
                Text(
                  text = quality.bitrate,
                  color = AudiovTextMuted,
                  fontSize = 12.sp
                )
              }
              if (isSelected) {
                Box(
                  modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(AudiovPrimaryGlow)
                )
              }
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(14.dp))
    }

    // Sleep Timer Section
    item {
      SettingsCard(title = "Sleep Timer", icon = Icons.Default.Bedtime) {
        Column {
          Text(
            text = if (sleepTimerMinutes != null) "Stopping playback in $sleepTimerMinutes min" else "Playback will continue normally",
            color = if (sleepTimerMinutes != null) AudiovSecondary else AudiovTextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 10.dp)
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            sleepTimerOptions.forEach { (mins, label) ->
              val isSelected = sleepTimerMinutes == mins
              FilterChip(
                selected = isSelected,
                onClick = { onSleepTimerChange(mins) },
                label = { Text(label, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = AudiovPrimary,
                  selectedLabelColor = Color.White,
                  containerColor = AudiovSurfaceVariant,
                  labelColor = AudiovTextSecondary
                ),
                shape = RoundedCornerShape(16.dp)
              )
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(14.dp))
    }

    // Equalizer & Audio FX Section
    item {
      SettingsCard(title = "Sound Equalizer & DSP", icon = Icons.Default.Equalizer) {
        Column {
          // Bass boost toggle
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Sub-Bass Enhancer", color = AudiovTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
              Text("Dynamic harmonic low-end boost", color = AudiovTextMuted, fontSize = 12.sp)
            }
            Switch(
              checked = bassBoostEnabled,
              onCheckedChange = { onBassBoostToggle() },
              colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AudiovPrimary
              )
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text("Presets", color = AudiovTextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
          Spacer(modifier = Modifier.height(6.dp))

          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            eqPresets.forEach { preset ->
              val isSelected = preset == equalizerPreset
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) AudiovPrimary.copy(alpha = 0.15f) else Color.Transparent)
                  .clickable { onEqualizerPresetChange(preset) }
                .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = preset,
                  color = if (isSelected) AudiovPrimaryGlow else AudiovTextPrimary,
                  fontSize = 13.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
                if (isSelected) {
                  Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = AudiovPrimaryGlow,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(14.dp))
    }

    // Crossfade Section
    item {
      SettingsCard(title = "Gapless & Crossfade", icon = Icons.Default.Tune) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Crossfade Duration", color = AudiovTextPrimary, fontSize = 14.sp)
            Text("${crossfadeSeconds}s", color = AudiovPrimaryGlow, fontSize = 14.sp, fontWeight = FontWeight.Bold)
          }
          Slider(
            value = crossfadeSeconds.toFloat(),
            onValueChange = { onCrossfadeChange(it.toInt()) },
            valueRange = 0f..12f,
            steps = 11,
            colors = SliderDefaults.colors(
              thumbColor = AudiovPrimaryGlow,
              activeTrackColor = AudiovPrimary,
              inactiveTrackColor = AudiovDivider
            )
          )
        }
      }
      Spacer(modifier = Modifier.height(14.dp))
    }

    // About Audiov App Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AudiovCardElevated),
        shape = RoundedCornerShape(16.dp)
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
              .background(Brush.linearGradient(listOf(AudiovPrimary, AudiovSecondary))),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.width(14.dp))
          Column {
            Text(
              text = "Audiov v0.1",
              color = AudiovTextPrimary,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "High Fidelity Music Streaming Platform",
              color = AudiovTextSecondary,
              fontSize = 12.sp
            )
            Text(
              text = "Clean Jetpack Compose Architecture",
              color = AudiovTextMuted,
              fontSize = 11.sp
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(90.dp))
    }
  }
}

@Composable
fun SettingsCard(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  content: @Composable () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = AudiovCard),
    shape = RoundedCornerShape(16.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 12.dp)
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = AudiovPrimaryGlow,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = title,
          color = AudiovTextPrimary,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
      }
      content()
    }
  }
}
