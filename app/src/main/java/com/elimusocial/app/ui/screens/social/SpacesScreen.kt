package com.elimusocial.app.ui.screens.social

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elimusocial.app.ui.theme.*
import kotlinx.coroutines.delay

data class Space(
    val id: String,
    val title: String,
    val host: String,
    val speakers: List<String>,
    val listeners: Int,
    val isLive: Boolean = true,
    val category: String = "Education"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpacesScreen(onBack: () -> Unit) {
    var activeSpace by remember { mutableStateOf<Space?>(null) }
    var showCreateSpace by remember { mutableStateOf(false) }

    val spaces = listOf(
        Space("1", "Tech Talk Tomorrow: AI in Education", "Alex Otieno", listOf("Mary W.", "Brian", "Joyce M."), 156),
        Space("2", "KCSE Revision — Chemistry", "Teacher Alex", listOf("Student1", "Student2"), 89),
        Space("3", "Startup Ideas Discussion", "Brian Otieno", listOf("Mary W.", "John"), 234),
        Space("4", "Study Music & Focus Session 🎵", "Joyce Maina", listOf("Alex"), 45),
    )

    if (activeSpace != null) {
        SpaceRoomScreen(space = activeSpace!!, onLeave = { activeSpace = null })
        return
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                title = { Text("Spaces 🎙️", fontWeight = FontWeight.Bold, color = TextPrimary) },
                actions = {
                    IconButton(onClick = { showCreateSpace = true }) {
                        Icon(Icons.Default.Add, null, tint = ElectricPurple)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Start a space button
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { showCreateSpace = true },
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(ElectricPurple, AccentBlue)))
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(ElectricPurple.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Mic, null, tint = ElectricPurple, modifier = Modifier.size(26.dp))
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Start a Space 🎙️", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Host a live audio conversation", color = TextMuted, fontSize = 13.sp)
                        }
                        Icon(Icons.Default.ChevronRight, null, tint = ElectricPurple)
                    }
                }
            }

            item {
                Text("Live Spaces", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            items(spaces) { space ->
                SpaceCard(space = space, onJoin = { activeSpace = space })
            }
        }
    }

    if (showCreateSpace) {
        CreateSpaceDialog(onDismiss = { showCreateSpace = false }, onCreate = { title ->
            activeSpace = Space("new", title, "You", emptyList(), 1, true)
            showCreateSpace = false
        })
    }
}

@Composable
fun SpaceCard(space: Space, onJoin: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onJoin),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.background(AccentGreen.copy(alpha = 0.15f), RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                    Text("🎙️ LIVE", color = AccentGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(8.dp))
                Text(space.category, color = TextMuted, fontSize = 11.sp)
                Spacer(Modifier.weight(1f))
                Icon(Icons.Default.Headset, null, tint = TextMuted, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("${space.listeners}", color = TextMuted, fontSize = 12.sp)
            }
            Spacer(Modifier.height(10.dp))
            Text(space.title, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(Modifier.height(12.dp))

            // Host + speakers
            Text("Host", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(ElectricPurple).border(2.dp, AccentGreen, CircleShape), contentAlignment = Alignment.Center) {
                    Text(space.host.firstOrNull()?.toString() ?: "?", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(8.dp))
                Text(space.host, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.width(6.dp))
                Box(modifier = Modifier.background(AccentGreen.copy(alpha = 0.15f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text("Host", color = AccentGreen, fontSize = 10.sp)
                }
            }

            if (space.speakers.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Text("Speakers", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    space.speakers.take(3).forEachIndexed { idx, speaker ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(30.dp).clip(CircleShape).background(
                                listOf(AccentBlue, AccentOrange, AccentGreen)[idx % 3]
                            ), contentAlignment = Alignment.Center) {
                                Text(speaker.firstOrNull()?.toString() ?: "?", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(4.dp))
                            Text(speaker, color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onJoin,
                    modifier = Modifier.weight(1f).height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                    shape = RoundedCornerShape(20.dp)
                ) { Text("Join", fontWeight = FontWeight.SemiBold) }
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.height(40.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, DividerColor)
                ) { Icon(Icons.Default.NotificationsNone, null, tint = TextPrimary, modifier = Modifier.size(18.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpaceRoomScreen(space: Space, onLeave: () -> Unit) {
    var isMuted by remember { mutableStateOf(true) }
    var handRaised by remember { mutableStateOf(false) }
    var listenerCount by remember { mutableIntStateOf(space.listeners) }
    val allSpeakers = remember { mutableStateListOf(space.host) + space.speakers }

    // Simulate speaking animation
    val speaking = remember { mutableStateOf(space.host) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            speaking.value = allSpeakers.random()
        }
    }

    // Simulate listener count changing
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            listenerCount += (-3..8).random()
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onLeave) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                title = { Text("Space 🎙️", fontWeight = FontWeight.Bold, color = TextPrimary) },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Share, null, tint = TextSecondary) }
                    IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, null, tint = TextSecondary) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Room info
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)) {
                Text(space.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Headset, null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("$listenerCount listening", color = TextMuted, fontSize = 13.sp)
                    Spacer(Modifier.width(12.dp))
                    Box(modifier = Modifier.size(6.dp).background(AccentGreen, CircleShape))
                    Spacer(Modifier.width(4.dp))
                    Text("Live", color = AccentGreen, fontSize = 13.sp)
                }
            }

            HorizontalDivider(color = DividerColor)

            LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                // Speakers section
                item {
                    Text("Speakers · ${allSpeakers.size}", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))

                    val rows = allSpeakers.chunked(3)
                    rows.forEach { row ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                            row.forEach { speaker ->
                                val isSpeaking = speaking.value == speaker
                                SpeakerAvatar(name = speaker, isHost = speaker == space.host, isSpeaking = isSpeaking, modifier = Modifier.weight(1f))
                            }
                            // fill empty slots
                            repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }

                // Listeners section
                item {
                    HorizontalDivider(color = DividerColor)
                    Spacer(Modifier.height(16.dp))
                    Text("Listeners · $listenerCount", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    // Show some listener avatars
                    val listenerNames = listOf("John K.", "Sarah K.", "Code Club", "Joyce M.", "Student1", "Student2", "+${listenerCount - 6}")
                    val rows2 = listenerNames.chunked(4)
                    rows2.forEach { row ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            row.forEach { name ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                    Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(DarkCard), contentAlignment = Alignment.Center) {
                                        Text(name.firstOrNull()?.toString() ?: "+", color = TextSecondary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(name.split(" ").first(), color = TextMuted, fontSize = 10.sp, maxLines = 1)
                                }
                            }
                            repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }

            // Bottom controls
            Column(modifier = Modifier.fillMaxWidth().background(DarkSurface)) {
                HorizontalDivider(color = DividerColor)
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp).navigationBarsPadding(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    // Leave quietly
                    TextButton(onClick = onLeave) {
                        Text("Leave quietly", color = AccentRed, fontWeight = FontWeight.SemiBold)
                    }

                    // Center controls
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Raise hand
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = { handRaised = !handRaised },
                                modifier = Modifier.size(52.dp).background(if (handRaised) AccentOrange.copy(alpha = 0.2f) else DarkCard, CircleShape)
                            ) {
                                Text(if (handRaised) "✋" else "🖐", fontSize = 22.sp)
                            }
                            Text(if (handRaised) "Lower" else "Raise", color = TextMuted, fontSize = 10.sp)
                        }
                        // Mute/Unmute
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = { isMuted = !isMuted },
                                modifier = Modifier.size(52.dp).background(if (isMuted) DarkCard else ElectricPurple.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Icon(if (isMuted) Icons.Default.MicOff else Icons.Default.Mic, null, tint = if (isMuted) TextMuted else ElectricPurple, modifier = Modifier.size(24.dp))
                            }
                            Text(if (isMuted) "Unmute" else "Muted", color = TextMuted, fontSize = 10.sp)
                        }
                    }

                    // Share / invite
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = {}, modifier = Modifier.size(52.dp).background(DarkCard, CircleShape)) {
                            Icon(Icons.Default.PersonAdd, null, tint = TextSecondary, modifier = Modifier.size(22.dp))
                        }
                        Text("Invite", color = TextMuted, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SpeakerAvatar(name: String, isHost: Boolean, isSpeaking: Boolean, modifier: Modifier = Modifier) {
    val scale by animateFloatAsState(if (isSpeaking) 1.08f else 1f, animationSpec = spring(), label = "scale")
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Box(contentAlignment = Alignment.Center) {
            if (isSpeaking) {
                Box(modifier = Modifier.size(68.dp).scale(scale).clip(CircleShape).border(2.dp, AccentGreen, CircleShape).background(Color.Transparent))
            }
            Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(if (isHost) ElectricPurple else AccentBlue).border(if (isSpeaking) 2.dp else 0.dp, AccentGreen, CircleShape), contentAlignment = Alignment.Center) {
                Text(name.firstOrNull()?.toString() ?: "?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            }
            if (isSpeaking) {
                Icon(Icons.Default.Mic, null, tint = AccentGreen, modifier = Modifier.size(16.dp).align(Alignment.BottomEnd).background(DarkBackground, CircleShape).padding(2.dp))
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(name.split(" ").first(), color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 1)
        if (isHost) {
            Text("Host", color = ElectricPurple, fontSize = 10.sp)
        }
    }
}

@Composable
fun CreateSpaceDialog(onDismiss: () -> Unit, onCreate: (String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Education") }
    val categories = listOf("Education", "Tech", "Business", "Study", "Social", "Entertainment")

    AlertDialog(
        onDismissRequest = onDismiss, containerColor = DarkCard,
        title = { Text("Start a Space", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    placeholder = { Text("Space title...", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricPurple, unfocusedBorderColor = DividerColor, focusedContainerColor = DarkBackground, unfocusedContainerColor = DarkBackground, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = ElectricPurple)
                )
                Text("Category", color = TextSecondary, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    categories.forEach { cat ->
                        FilterChip(selected = selectedCategory == cat, onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricPurple, selectedLabelColor = Color.White, containerColor = DarkBackground, labelColor = TextMuted))
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { if (title.isNotBlank()) onCreate(title) }, colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple), enabled = title.isNotBlank()) {
                Icon(Icons.Default.Mic, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Start Space")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) } }
    )
}
