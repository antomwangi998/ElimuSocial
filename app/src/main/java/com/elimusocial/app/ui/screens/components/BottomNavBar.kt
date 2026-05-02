package com.elimusocial.app.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.elimusocial.app.ui.theme.*

data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String,
    val badgeCount: Int = 0
)

val bottomNavItems = listOf(
    BottomNavItem("Home", Icons.Filled.Home, Icons.Outlined.Home, "feed"),
    BottomNavItem("Explore", Icons.Filled.Search, Icons.Outlined.Search, "explore"),
    BottomNavItem("Create", Icons.Filled.Add, Icons.Filled.Add, "create"),
    BottomNavItem("AI", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "ai"),
    BottomNavItem("Profile", Icons.Filled.Person, Icons.Outlined.Person, "profile")
)

@Composable
fun BottomNavBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurface)
    ) {
        HorizontalDivider(color = DividerColor, thickness = 0.5.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                val isSelected = selectedTab == item.route
                val isCreate = item.route == "create"

                if (isCreate) {
                    // FAB-style center button
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(ElectricPurple, AccentBlue)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { onTabSelected(item.route) }) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Create",
                                tint = TextPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                } else {
                    // Regular nav item
                    Box(contentAlignment = Alignment.TopEnd) {
                        IconButton(
                            onClick = { onTabSelected(item.route) },
                            modifier = Modifier.size(52.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label,
                                    tint = if (isSelected) ElectricPurple else TextMuted,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        // Badge
                        if (item.badgeCount > 0 && !isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(AccentRed, CircleShape)
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-4).dp, y = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.badgeCount.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
