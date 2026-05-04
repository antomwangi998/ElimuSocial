package com.elimusocial.app.ui.screens.creator

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elimusocial.app.ui.theme.*

data class Badge(
    val id: String,
    val emoji: String,
    val title: String,
    val description: String,
    val earned: Boolean,
    val rarity: String = "Common" // Common, Rare, Epic, Legendary
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(onBack: () -> Unit) {
    val badges = listOf(
        Badge("1", "⭐", "Top Contributor", "You're among the top 5% active members", true, "Legendary"),
        Badge("2", "📚", "Active Learner", "Completed 10+ learning sessions", true, "Rare"),
        Badge("3", "🔥", "Post Streak", "Posted 7 days in a row", true, "Rare"),
        Badge("4", "🤝", "Helpful One", "Helped 50+ students", true, "Common"),
        Badge("5", "🧠", "Engaging Mind", "Got 100+ replies on your posts", true, "Common"),
        Badge("6", "🏆", "Champion", "Won a community challenge", false, "Epic"),
        Badge("7", "🌟", "Influencer", "Reached 10K followers", false, "Legendary"),
        Badge("8", "💡", "Innovator", "First to try 5 new features", false, "Epic"),
        Badge("9", "📣", "Announcer", "Made 50+ posts", false, "Common"),
        Badge("10", "🎯", "Goal Getter", "Completed all onboarding steps", true, "Common"),
        Badge("11", "👑", "Community Leader", "Admin of 3+ groups", false, "Epic"),
        Badge("12", "✅", "Verified", "Account verified", true, "Rare"),
    )

    val earnedCount = badges.count { it.earned }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) }
                },
                title = { Text("Your Badges", fontWeight = FontWeight.Bold, color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Header stats
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(earnedCount.toString(), fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 28.sp)
                        Text("Earned", color = TextMuted, fontSize = 13.sp)
                    }
                    Box(modifier = Modifier.width(1.dp).height(40.dp).background(DividerColor))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text((badges.size - earnedCount).toString(), fontWeight = FontWeight.Bold, color = TextMuted, fontSize = 28.sp)
                        Text("Locked", color = TextMuted, fontSize = 13.sp)
                    }
                    Box(modifier = Modifier.width(1.dp).height(40.dp).background(DividerColor))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${(earnedCount * 100 / badges.size)}%", fontWeight = FontWeight.Bold, color = ElectricPurple, fontSize = 28.sp)
                        Text("Complete", color = TextMuted, fontSize = 13.sp)
                    }
                }
            }

            // Progress bar
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Progress", color = TextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { earnedCount.toFloat() / badges.size },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = ElectricPurple,
                    trackColor = DarkCard
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("All Badges", fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 15.sp, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(badges, key = { it.id }) { badge ->
                    BadgeItem(badge = badge)
                }
            }
        }
    }
}

@Composable
fun BadgeItem(badge: Badge) {
    val rarityColor = when (badge.rarity) {
        "Legendary" -> AccentYellow
        "Epic" -> ElectricPurple
        "Rare" -> AccentBlue
        else -> AccentGreen
    }

    val bgBrush = if (badge.earned)
        Brush.linearGradient(listOf(rarityColor.copy(alpha = 0.2f), DarkCard))
    else
        Brush.linearGradient(listOf(DarkCard, DarkCard))

    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(0.85f),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
        shape = RoundedCornerShape(14.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(bgBrush), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier.fillMaxSize().padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.size(54.dp).clip(CircleShape)
                        .background(if (badge.earned) rarityColor.copy(alpha = 0.25f) else DarkBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        badge.emoji,
                        fontSize = 26.sp,
                        color = if (badge.earned) androidx.compose.ui.graphics.Color.Unspecified else androidx.compose.ui.graphics.Color.Gray.copy(alpha = 0.4f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(badge.title, color = if (badge.earned) TextPrimary else TextMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, maxLines = 2)
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier.background(rarityColor.copy(alpha = if (badge.earned) 0.2f else 0.1f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(badge.rarity, color = if (badge.earned) rarityColor else TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
            if (!badge.earned) {
                Box(modifier = Modifier.fillMaxSize().background(DarkBackground.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Lock, null, tint = TextMuted.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
