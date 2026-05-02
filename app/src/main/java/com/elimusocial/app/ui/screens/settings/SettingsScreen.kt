package com.elimusocial.app.ui.screens.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elimusocial.app.data.models.SampleData
import com.elimusocial.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, onLogout: () -> Unit) {
    var darkMode by remember { mutableStateOf(true) }
    var showSecurityScreen by remember { mutableStateOf(false) }

    if (showSecurityScreen) {
        SecurityScreen(onBack = { showSecurityScreen = false }, onLogout = onLogout)
        return
    }

    val user = SampleData.users[0]

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) }
                },
                title = { Text("Settings", fontWeight = FontWeight.Bold, color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Profile header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().background(DarkCard, RoundedCornerShape(16.dp)).padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(56.dp).clip(CircleShape).background(ElectricPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(user.name.first().toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(user.name, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Text(user.username, color = TextMuted, fontSize = 13.sp)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = TextMuted)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Settings groups
            item {
                SettingsGroup(title = "Account") {
                    SettingsItem(Icons.Outlined.Person, "Account", "Manage your personal info", {})
                    SettingsItem(Icons.Outlined.Lock, "Privacy & Safety", "Control your privacy", { showSecurityScreen = true })
                    SettingsItem(Icons.Outlined.Notifications, "Notifications", "Customize your alerts", {})
                    SettingsItem(Icons.Outlined.Tune, "Content Preferences", "Choose what you see", {})
                }
            }

            item {
                SettingsGroup(title = "Appearance") {
                    // Dark mode toggle
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(36.dp).background(DarkElevated, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.DarkMode, null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Text("Dark mode", color = TextPrimary, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        Switch(
                            checked = darkMode,
                            onCheckedChange = { darkMode = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ElectricPurple)
                        )
                    }
                    SettingsItem(Icons.Outlined.Language, "Language", "English (en)", {})
                }
            }

            item {
                SettingsGroup(title = "More") {
                    SettingsItem(Icons.Outlined.Storage, "Data Usage", "Manage storage and data", {})
                    SettingsItem(Icons.Outlined.Help, "Help & Support", null, {})
                    SettingsItem(Icons.Outlined.Info, "About Elimu Social", null, {})
                }
            }

            // Logout
            item {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, AccentRed),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = AccentRed.copy(alpha = 0.05f))
                ) {
                    Icon(Icons.Default.Logout, null, tint = AccentRed, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log Out", color = AccentRed, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp))
        Column(modifier = Modifier.fillMaxWidth().background(DarkCard, RoundedCornerShape(16.dp))) {
            content()
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, subtitle: String?, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(36.dp).background(DarkElevated, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = TextSecondary, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextPrimary, fontSize = 14.sp)
            if (subtitle != null) Text(subtitle, color = TextMuted, fontSize = 12.sp)
        }
        Icon(Icons.Default.ChevronRight, null, tint = TextMuted, modifier = Modifier.size(18.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(onBack: () -> Unit, onLogout: () -> Unit) {
    var twoFAEnabled by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                title = { Text("Security", fontWeight = FontWeight.Bold, color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                // 2FA
                Row(
                    modifier = Modifier.fillMaxWidth().background(DarkCard, RoundedCornerShape(14.dp)).padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Shield, null, tint = AccentGreen, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Two-Factor Authentication", color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Text(if (twoFAEnabled) "On >" else "Off", color = if (twoFAEnabled) AccentGreen else TextMuted, fontSize = 12.sp)
                    }
                    Switch(
                        checked = twoFAEnabled,
                        onCheckedChange = { twoFAEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AccentGreen)
                    )
                }
            }

            item {
                SecurityInfoItem(Icons.Default.Devices, "Active Sessions", "3 active sessions >")
                Spacer(modifier = Modifier.height(4.dp))
                SecurityInfoItem(Icons.Default.History, "Login Activity", "See recent logins >")
                Spacer(modifier = Modifier.height(4.dp))
                SecurityInfoItem(Icons.Default.Lock, "Change Password", "Update your password >")
                Spacer(modifier = Modifier.height(4.dp))
                SecurityInfoItem(Icons.Default.VisibilityOff, "Privacy Settings", "Manage who can see your content >")
                Spacer(modifier = Modifier.height(4.dp))
                SecurityInfoItem(Icons.Default.Block, "Blocked Accounts", "12 accounts blocked >")
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("Log Out", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun SecurityInfoItem(icon: ImageVector, title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().background(DarkCard, RoundedCornerShape(14.dp)).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = TextSecondary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Text(title, color = TextPrimary, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(value, color = TextMuted, fontSize = 12.sp)
    }
}
