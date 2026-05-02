package com.elimusocial.app.ui.screens.creator

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elimusocial.app.ui.theme.*

data class Badge(
    val id: String,
    val emoji: String,
    val name: String,
    val description: String,
    val color: Color,
    val isEarned: Boolean,
    val progress: Float = 1f
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(onBack: () -> Unit) {
    val badges = listOf(
        Badge("1", "⭐", "Top Contributor", "You're among the top 5% active members", AccentYellow, true),
        Badge("2", "📚", "Active Learner", "Completed 10+ learning sessions", AccentBlue, true),
        Badge("3", "🔥", "Post Streak", "Posted 7 days in a row", AccentOrange, true),
        Badge("4", "🤝", "Helpful One", "Received 50+ helpful replies", AccentGreen, true),
        Badge("5", "🧠", "Engaging Mind", "Got 100+ comments on posts", ElectricPurple, true),
        Badge("6", "🚀", "Early Adopter", "Joined in the first month", LightPurple, true),
        Badge("7", "🌟", "Rising Star", "Gained 500 followers", AccentYellow, false, 0.6f),
        Badge("8", "💬", "Conversationalist", "Sent 200+ messages", AccentBlue, false, 0.4f),
        Badge("9", "🏆", "Champion", "Won a community challenge", AccentOrange, false, 0.1f),
        Badge("10", "✍️", "Content Creator", "Published 50+ posts", AccentGreen, false, 0.75f),
        Badge("11", "🎓", "Scholar", "Completed a full learning path", ElectricPurple, false, 0.3f),
        Badge("12", "👑", "Community Leader", "Admin of 3+ groups", AccentYellow, false, 0.0f)
    )

    val earned = badges.count { it.isEarned }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) }
                },
                title = { Text("Your Badges", fontWeight = FontWeight.Bold, color = TextPrimary) },
                actions = {
                    TextButton(onClick = {}) {
                        Text("See all", color = ElectricPurple, fontSize = 13.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Progress header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(ElectricPurple, AccentBlue)))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("$earned / ${badges.size} Badges Earned", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { earned.toFloat() / badges.size },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Keep going! You're doing great 🎯", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("⭐", fontSize = 44.sp)
                }
            }

            // Badges grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(badges) { badge ->
                    BadgeCard(badge = badge)
                }
            }
        }
    }
}

@Composable
fun BadgeCard(badge: Badge) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkCard)
            .alpha(if (badge.isEarned) 1f else 0.5f)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Badge circle
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        if (badge.isEarned)
                            Brush.radialGradient(listOf(badge.color.copy(alpha = 0.3f), badge.color.copy(alpha = 0.05f)))
                        else
                            Brush.radialGradient(listOf(DividerColor, DividerColor)),
                        CircleShape
                    )
                    .border(
                        width = 2.dp,
                        color = if (badge.isEarned) badge.color else DividerColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(badge.emoji, fontSize = 28.sp)
            }

            if (!badge.isEarned && badge.progress > 0f) {
                CircularProgressIndicator(
                    progress = { badge.progress },
                    modifier = Modifier.size(72.dp),
                    color = badge.color,
                    trackColor = DividerColor,
                    strokeWidth = 3.dp
                )
            }

            if (badge.isEarned) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(20.dp)
                        .background(AccentGreen, CircleShape)
                        .border(2.dp, DarkCard, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(12.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(badge.name, color = if (badge.isEarned) TextPrimary else TextMuted, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, textAlign = TextAlign.Center)

        if (!badge.isEarned && badge.progress > 0f) {
            Spacer(modifier = Modifier.height(3.dp))
            Text("${(badge.progress * 100).toInt()}%", color = badge.color, fontSize = 10.sp)
        }
    }
}
