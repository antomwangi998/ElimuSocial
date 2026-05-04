package com.elimusocial.app.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elimusocial.app.ui.theme.*

// ===== STEP 2 - FOLLOW PEOPLE =====
@Composable
fun FollowPeopleScreen(onContinue: () -> Unit, onSkip: () -> Unit) {
    val suggestedUsers = listOf(
        Triple("Mary Wanjiku", "@mary_edu", false),
        Triple("Brian Otieno", "@brian_dev", false),
        Triple("Joyce Maina", "@joyce_m", false),
        Triple("Alex Kamau", "@alex_tech", false),
        Triple("Teacher Alex", "@teacher_alex", false),
        Triple("Code Club", "@code_club", false),
        Triple("Study Together", "@study_together", false),
    )
    val followed = remember { mutableStateListOf<String>() }

    Box(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 40.dp)) {
                Text("Follow people", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("you know", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("You can always change this later.", color = TextMuted, fontSize = 14.sp)
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(suggestedUsers.size) { index ->
                    val (name, handle, _) = suggestedUsers[index]
                    val isFollowing = followed.contains(name)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(46.dp).clip(CircleShape).background(ElectricPurple), contentAlignment = Alignment.Center) {
                            Text(name.firstOrNull()?.toString() ?: "?", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(name, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 15.sp)
                            Text(handle, color = TextMuted, fontSize = 13.sp)
                        }
                        Button(
                            onClick = { if (isFollowing) followed.remove(name) else followed.add(name) },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isFollowing) DarkCard else ElectricPurple),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                            modifier = Modifier.height(36.dp),
                            border = if (isFollowing) androidx.compose.foundation.BorderStroke(1.dp, DividerColor) else null
                        ) {
                            Text(if (isFollowing) "Following" else "Follow", color = TextPrimary, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Bottom buttons
            Column(modifier = Modifier.padding(24.dp)) {
                Button(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple)
                ) {
                    Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = onSkip, modifier = Modifier.fillMaxWidth()) {
                    Text("Skip for now", color = TextMuted, fontSize = 14.sp)
                }
            }
        }
    }
}

// ===== STEP 3 - CHOOSE GOALS =====
@Composable
fun ChooseGoalsScreen(onContinue: () -> Unit) {
    val goals = listOf(
        Triple("🎓", "Learn new things", "Discover resources & study tools"),
        Triple("📢", "Share knowledge", "Post & inspire your community"),
        Triple("🤝", "Build my network", "Connect with peers & mentors"),
        Triple("📡", "Stay updated", "Follow trends & news"),
        Triple("🚀", "Grow my brand", "Build your personal profile"),
        Triple("💰", "Earn opportunities", "Monetize your knowledge"),
    )
    val selected = remember { mutableStateListOf<String>() }

    Box(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Progress dots
            Row(modifier = Modifier.fillMaxWidth().padding(top = 50.dp, bottom = 8.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                listOf(false, false, true).forEach { isActive ->
                    Box(modifier = Modifier.size(if (isActive) 24.dp else 8.dp, 8.dp).clip(RoundedCornerShape(4.dp)).background(if (isActive) ElectricPurple else DarkCard))
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                Text("What brings you", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("to Elimu Social?", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Choose your main goals", color = TextMuted, fontSize = 14.sp)
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(goals.size) { index ->
                    val (emoji, title, subtitle) = goals[index]
                    val isSelected = selected.contains(title)
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable {
                            if (isSelected) selected.remove(title) else selected.add(title)
                        },
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) ElectricPurple.copy(alpha = 0.15f) else DarkCard),
                        shape = RoundedCornerShape(14.dp),
                        border = if (isSelected) CardDefaults.outlinedCardBorder() else null
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(if (isSelected) ElectricPurple.copy(alpha = 0.3f) else DarkBackground),
                                contentAlignment = Alignment.Center
                            ) { Text(emoji, fontSize = 22.sp) }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(title, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                Text(subtitle, color = TextMuted, fontSize = 12.sp)
                            }
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, null, tint = ElectricPurple, modifier = Modifier.size(22.dp))
                            }
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(24.dp)) {
                Button(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                    enabled = selected.isNotEmpty()
                ) {
                    Text("Let's Go", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
