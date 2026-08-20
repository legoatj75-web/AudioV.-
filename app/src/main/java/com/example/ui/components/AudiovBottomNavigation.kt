package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AudiovBackground
import com.example.ui.theme.AudiovPrimary
import com.example.ui.theme.AudiovPrimaryGlow
import com.example.ui.theme.AudiovSurface
import com.example.ui.theme.AudiovSurfaceVariant
import com.example.ui.theme.AudiovTextMuted
import com.example.ui.theme.AudiovTextPrimary
import com.example.viewmodel.AudiovNavTab

data class NavItem(
  val tab: AudiovNavTab,
  val title: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector
)

val navigationItems = listOf(
  NavItem(AudiovNavTab.HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home),
  NavItem(AudiovNavTab.SEARCH, "Search", Icons.Filled.Search, Icons.Outlined.Search),
  NavItem(AudiovNavTab.LIBRARY, "Library", Icons.Filled.LibraryMusic, Icons.Outlined.LibraryMusic),
  NavItem(AudiovNavTab.SETTINGS, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
)

@Composable
fun AudiovBottomNavigation(
  activeTab: AudiovNavTab,
  onTabSelected: (AudiovNavTab) -> Unit,
  modifier: Modifier = Modifier
) {
  NavigationBar(
    modifier = modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .testTag("audiov_bottom_navigation"),
    containerColor = AudiovSurface,
    tonalElevation = 8.dp
  ) {
    navigationItems.forEach { item ->
      val isSelected = activeTab == item.tab
      NavigationBarItem(
        selected = isSelected,
        onClick = { onTabSelected(item.tab) },
        icon = {
          Icon(
            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
            contentDescription = item.title,
            modifier = Modifier.size(24.dp)
          )
        },
        label = {
          Text(
            text = item.title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
          )
        },
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = AudiovTextPrimary,
          selectedTextColor = AudiovPrimaryGlow,
          indicatorColor = AudiovPrimary,
          unselectedIconColor = AudiovTextMuted,
          unselectedTextColor = AudiovTextMuted
        ),
        modifier = Modifier.testTag("nav_tab_${item.title.lowercase()}")
      )
    }
  }
}
