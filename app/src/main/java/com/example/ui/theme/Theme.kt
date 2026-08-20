package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
  primary = AudiovPrimary,
  onPrimary = AudiovTextPrimary,
  primaryContainer = AudiovSurfaceVariant,
  onPrimaryContainer = AudiovPrimaryGlow,
  secondary = AudiovSecondary,
  onSecondary = AudiovBackground,
  secondaryContainer = AudiovSurfaceVariant,
  onSecondaryContainer = AudiovSecondary,
  tertiary = AudiovTertiary,
  onTertiary = AudiovTextPrimary,
  background = AudiovBackground,
  onBackground = AudiovTextPrimary,
  surface = AudiovSurface,
  onSurface = AudiovTextPrimary,
  surfaceVariant = AudiovSurfaceVariant,
  onSurfaceVariant = AudiovTextSecondary,
  outline = AudiovDivider,
  outlineVariant = AudiovCardElevated
)

private val LightColorScheme = DarkColorScheme // Audiov is an immersive dark audio experience

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  val colorScheme = DarkColorScheme
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      window.statusBarColor = AudiovBackground.toArgb()
      window.navigationBarColor = AudiovBackground.toArgb()
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
      WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
