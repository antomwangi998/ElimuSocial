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
import com.elimusocial.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, onLogout: () -> Unit) {
    var currentScreen by remember { mutableStateOf("main") }
    var darkMode by remember { mutableStateOf(true) }

    when (currentScreen) {
        "security" -> SecurityScreen(onBack = { currentScreen = "main" }, onLogout = onLogout)
        "notifications" -> NotificationSettingsScreen(onBack = { currentScreen = "main" })
        "appearance" -> AppearanceScreen(onBack = { currentScreen = "main" }, darkMode = darkMode, onToggleDark = { darkMode = !darkMode })
        "language" -> LanguageScreen(onBack = { currentScreen = "main" })
        "privacy" -> PrivacyScreen(onBack = { currentScreen = "main" })
        else -> MainSettingsScreen(
            onBack = onBack,
            onLogout = onLogout,
            onNavigate = { currentScreen = it },
            darkMode = darkMode,
            onToggleDark = { darkMode = !darkMode }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainSettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onNavigate: (String) -> Unit,
    darkMode: Boolean,
    onToggleDark: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = DarkCard,
            title = { Text("Log out", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to log out?", color = TextSecondary) },
            confirmButton = {
                Button(onClick = { showLogoutDialog = false; onLogout() }, colors = ButtonDefaults.buttonColors(containerColor = AccentRed)) {
                    Text("Log out", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLogoutDialog = false }, border = BorderStroke(1.dp, DividerColor)) {
                    Text("Cancel", color = TextPrimary)
                }
            }
        )
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                title = { Text("Settings", fontWeight = FontWeight.Bold, color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            item {
                // Profile card
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {},
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(ElectricPurple), contentAlignment = Alignment.Center) {
                            Text("A", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Antony Mwangi", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                            Text("@antony_mwangi", color = TextMuted, fontSize = 14.sp)
                        }
                        Icon(Icons.Default.ChevronRight, null, tint = TextMuted)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                SettingsSectionTitle("Account")
            }
            item { SettingsItem(icon = Icons.Outlined.Person, title = "Account", subtitle = "Manage your personal info", onClick = {}) }
            item { SettingsItem(icon = Icons.Outlined.Lock, title = "Privacy & Safety", subtitle = "Control your privacy", onClick = { onNavigate("privacy") }) }
            item { SettingsItem(icon = Icons.Outlined.Security, title = "Security", subtitle = "Password, 2FA, sessions", onClick = { onNavigate("security") }) }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                SettingsSectionTitle("Preferences")
            }
            item { SettingsItem(icon = Icons.Outlined.Notifications, title = "Notifications", subtitle = "Customize your alerts", onClick = { onNavigate("notifications") }) }
            item { SettingsItem(icon = Icons.Outlined.Palette, title = "Appearance", subtitle = "Dark mode, theme colors", onClick = { onNavigate("appearance") }) }
            item { SettingsItem(icon = Icons.Outlined.Language, title = "Language", subtitle = "English (US)", onClick = { onNavigate("language") }) }
            item { SettingsItem(icon = Icons.Outlined.DataUsage, title = "Data Usage", subtitle = "Manage storage and data", onClick = {}) }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                SettingsSectionTitle("Content")
            }
            item { SettingsItem(icon = Icons.Outlined.TuneOutlined, title = "Content Preferences", subtitle = "Choose what you see", onClick = {}) }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                SettingsSectionTitle("About")
            }
            item { SettingsItem(icon = Icons.Outlined.HelpOutline, title = "Help & Support", subtitle = "Get help, send feedback", onClick = {}) }
            item { SettingsItem(icon = Icons.Outlined.Info, title = "About Elimu Social", subtitle = "Version 1.0.0", onClick = {}) }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, AccentRed.copy(alpha = 0.4f))
                ) {
                    Icon(Icons.Outlined.Logout, null, tint = AccentRed, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log Out", color = AccentRed, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(title, color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 4.dp, start = 4.dp))
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit, trailing: (@Composable () -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(DarkCard), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = TextSecondary, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = TextMuted, fontSize = 12.sp)
        }
        trailing?.invoke() ?: Icon(Icons.Default.ChevronRight, null, tint = TextMuted, modifier = Modifier.size(20.dp))
    }
}

// ===== SECURITY SCREEN =====
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(onBack: () -> Unit, onLogout: () -> Unit) {
    var twoFAEnabled by remember { mutableStateOf(false) }

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
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp)) {
            item {
                SecurityItem(title = "Two-Factor Authentication", subtitle = "Add extra security to your account") {
                    Switch(checked = twoFAEnabled, onCheckedChange = { twoFAEnabled = it }, colors = SwitchDefaults.colors(checkedThumbColor = TextPrimary, checkedTrackColor = ElectricPurple))
                }
                HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
                SecurityItem(title = "Active Sessions", subtitle = "3 active sessions", onClick = {}) { Icon(Icons.Default.ChevronRight, null, tint = TextMuted) }
                HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
                SecurityItem(title = "Login Activity", subtitle = "See recent logins", onClick = {}) { Icon(Icons.Default.ChevronRight, null, tint = TextMuted) }
                HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
                SecurityItem(title = "Change Password", subtitle = "Update your password", onClick = {}) { Icon(Icons.Default.ChevronRight, null, tint = TextMuted) }
                HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
                SecurityItem(title = "Privacy Settings", subtitle = "Manage who can see your content", onClick = {}) { Icon(Icons.Default.ChevronRight, null, tint = TextMuted) }
                HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
                SecurityItem(title = "Blocked Accounts", subtitle = "12 accounts blocked", onClick = {}) { Icon(Icons.Default.ChevronRight, null, tint = TextMuted) }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                    shape = RoundedCornerShape(14.dp)
                ) { Text("Log Out", color = Color.White, fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}

@Composable
fun SecurityItem(title: String, subtitle: String, onClick: (() -> Unit)? = null, trailing: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().let { if (onClick != null) it.clickable(onClick = onClick) else it }.padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = TextMuted, fontSize = 12.sp)
        }
        trailing()
    }
}

// ===== NOTIFICATION SETTINGS =====
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(onBack: () -> Unit) {
    val settings = remember {
        mutableStateListOf(
            Triple("Mentions", "Notify me when someone mentions me", true),
            Triple("Messages", "Notify me about new messages", true),
            Triple("Likes & Reactions", "Notify me about likes and reactions", true),
            Triple("Comments", "Notify me about comments", false),
            Triple("Groups", "Notify me about group activities", true),
            Triple("AI Alerts", "Notify me about AI suggestions", false),
            Triple("Email Notifications", "Receive email updates", false),
        )
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                title = { Text("Notification Settings", fontWeight = FontWeight.Bold, color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp)) {
            items(settings.size) { index ->
                val (title, subtitle, enabled) = settings[index]
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        Text(subtitle, color = TextMuted, fontSize = 12.sp)
                    }
                    Switch(
                        checked = enabled,
                        onCheckedChange = { settings[index] = Triple(title, subtitle, it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = TextPrimary, checkedTrackColor = ElectricPurple)
                    )
                }
                HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
            }
        }
    }
}

// ===== APPEARANCE =====
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceScreen(onBack: () -> Unit, darkMode: Boolean, onToggleDark: () -> Unit) {
    val themeColors = listOf(AccentBlue, ElectricPurple, AccentGreen, AccentOrange, AccentRed)
    var selectedColor by remember { mutableIntStateOf(1) }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                title = { Text("Appearance", fontWeight = FontWeight.Bold, color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Card(colors = CardDefaults.cardColors(containerColor = DarkCard), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Theme", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        listOf("Light", "Dark", "System").forEachIndexed { index, label ->
                            val isSelected = (index == 1 && darkMode) || (index == 0 && !darkMode)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp))
                                        .background(if (index == 0) Color.White else DarkBackground)
                                        .border(2.dp, if (isSelected) ElectricPurple else DividerColor, RoundedCornerShape(12.dp))
                                        .clickable { if (index == 0) { if (darkMode) onToggleDark() } else if (index == 1) { if (!darkMode) onToggleDark() } },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        if (index == 0) Icons.Default.LightMode else if (index == 1) Icons.Default.DarkMode else Icons.Default.SettingsSuggest,
                                        null,
                                        tint = if (index == 0) DarkBackground else TextPrimary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(label, color = if (isSelected) ElectricPurple else TextMuted, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Card(colors = CardDefaults.cardColors(containerColor = DarkCard), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Theme Color", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        themeColors.forEachIndexed { index, color ->
                            Box(
                                modifier = Modifier.size(36.dp).clip(CircleShape).background(color)
                                    .border(2.dp, if (selectedColor == index) TextPrimary else Color.Transparent, CircleShape)
                                    .clickable { selectedColor = index },
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedColor == index) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ===== LANGUAGE =====
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(onBack: () -> Unit) {
    var selected by remember { mutableStateOf("English") }
    val languages = listOf("English", "Kiswahili", "Français", "العربية", "Portuguese", "Español")

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                title = { Text("Language", fontWeight = FontWeight.Bold, color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp)) {
            items(languages.size) { index ->
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { selected = languages[index] }.padding(vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(languages[index], color = if (selected == languages[index]) ElectricPurple else TextPrimary, fontSize = 15.sp, fontWeight = if (selected == languages[index]) FontWeight.SemiBold else FontWeight.Normal)
                    if (selected == languages[index]) Icon(Icons.Default.Check, null, tint = ElectricPurple)
                }
                HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
            }
        }
    }
}

// ===== PRIVACY =====
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    val settings = remember {
        mutableStateListOf(
            Triple("Private Account", "Only approved followers see your posts", false),
            Triple("Hide Online Status", "Don't show when you're active", false),
            Triple("Hide Read Receipts", "Don't show when you read messages", false),
            Triple("Allow Tags", "Let others tag you in posts", true),
            Triple("Allow DMs from everyone", "Receive messages from anyone", true),
        )
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                title = { Text("Privacy & Safety", fontWeight = FontWeight.Bold, color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp)) {
            items(settings.size) { index ->
                val (title, subtitle, enabled) = settings[index]
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        Text(subtitle, color = TextMuted, fontSize = 12.sp)
                    }
                    Switch(checked = enabled, onCheckedChange = { settings[index] = Triple(title, subtitle, it) }, colors = SwitchDefaults.colors(checkedThumbColor = TextPrimary, checkedTrackColor = ElectricPurple))
                }
                HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
            }
        }
    }
}
