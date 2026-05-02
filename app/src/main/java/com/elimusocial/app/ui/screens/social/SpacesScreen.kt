package com.elimusocial.app.ui.screens.social

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elimusocial.app.ui.theme.*

data class SpacePerson(val name: String, val role: SpaceRole, val isSpeaking: Boolean = false)
enum class SpaceRole { HOST, SPEAKER, LISTENER }

@Composable
fun SpacesScreen(onBack: () -> Unit) {
    var isMuted by remember { mutableStateOf(true) }
    var hasRequested by remember { mutableStateOf(false) }

    val host = SpacePerson("Alex Otieno", SpaceRole.HOST, isSpeaking = true)
    val speakers = listOf(
        SpacePerson("Mary W.", SpaceRole.SPEAKER, isSpeaking = false),
        SpacePerson("Brian", SpaceRole.SPEAKER, isSpeaking = true),
        SpacePerson("Joyce M.", SpaceRole.SPEAKER, isSpeaking = false)
    )
    val listeners = listOf(
        SpacePerson("Sarah", SpaceRole.LISTENER),
        SpacePerson("Tom", SpaceRole.LISTENER),
        SpacePerson("Amina", SpaceRole.LISTENER),
        SpacePerson("Peter", SpaceRole.LISTENER),
        SpacePerson("Grace", SpaceRole.LISTENER),
        SpacePerson("+152 more", SpaceRole.LISTENER)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, null, tint = TextSecondary)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Tech Talk Tomorrow", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text("23 speaking · 156 listening", color = TextSecondary, fontSize = 12.sp)
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.MoreHoriz, null, tint = TextSecondary)
            }
        }

        HorizontalDivider(color = DividerColor)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Host section
            Text("Host", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))
            SpacePersonTile(person = host, size = 72)

            Spacer(modifier = Modifier.height(24.dp))

            // Speakers
            Text("Speakers", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                speakers.forEach { SpacePersonTile(person = it, size = 60) }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Listeners
            Text("Listeners", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.height(160.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                userScrollEnabled = false
            ) {
                items(listeners) { person ->
                    SpacePersonTile(person = person, size = 52, showName = true)
                }
            }
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .padding(16.dp)
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Request to speak button
            if (!hasRequested) {
                Button(
                    onClick = { hasRequested = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple)
                ) {
                    Icon(Icons.Default.RecordVoiceOver, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Request to speak", fontWeight = FontWeight.SemiBold)
                }
            } else {
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
                ) {
                    Icon(if (isMuted) Icons.Default.MicOff else Icons.Default.Mic, null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isMuted) "Unmute to speak" else "Speaking...", fontWeight = FontWeight.SemiBold)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Mute
                SpaceControlButton(
                    icon = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    label = if (isMuted) "Muted" else "Live",
                    tint = if (isMuted) TextMuted else AccentGreen,
                    onClick = { isMuted = !isMuted }
                )

                // Share
                SpaceControlButton(Icons.Default.Share, "Share", TextSecondary, {})

                // Leave
                SpaceControlButton(
                    icon = Icons.Default.ExitToApp,
                    label = "Leave",
                    tint = AccentRed,
                    onClick = onBack
                )
            }
        }
    }
}

@Composable
fun SpacePersonTile(person: SpacePerson, size: Int, showName: Boolean = true) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            // Speaking ring
            if (person.isSpeaking) {
                Box(
                    modifier = Modifier
                        .size((size + 8).dp)
                        .background(AccentGreen.copy(alpha = 0.25f), CircleShape)
                )
            }
            Box(
                modifier = Modifier
                    .size(size.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(ElectricPurple, AccentBlue))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = person.name.first().toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = (size / 3).sp
                )
            }

            // Host crown
            if (person.role == SpaceRole.HOST) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(20.dp)
                        .background(AccentYellow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👑", fontSize = 10.sp)
                }
            }

            // Mic indicator
            if (person.role != SpaceRole.LISTENER) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(20.dp)
                        .background(
                            if (person.isSpeaking) AccentGreen else DarkCard,
                            CircleShape
                        )
                        .border(1.dp, DarkBackground, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (person.isSpeaking) Icons.Default.Mic else Icons.Default.MicOff,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        if (showName) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                person.name,
                color = TextSecondary,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
fun SpaceControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(DarkCard, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = tint, fontSize = 11.sp)
    }
}
