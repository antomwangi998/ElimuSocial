package com.elimusocial.app.ui.screens.creator

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elimusocial.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorDashboardScreen(onBack: () -> Unit) {
    var selectedRange by remember { mutableStateOf("Last 7 days") }
    val ranges = listOf("Last 7 days", "Last 30 days", "Last 90 days")
    var rangeExpanded by remember { mutableStateOf(false) }

    val topPosts = listOf(
        Triple("Discipline today, freedom tomorrow 💪", "8 May", 2300),
        Triple("5 productivity tips for programmers 👨‍💻", "10 May", 1800),
        Triple("Just finished computer science project! 🔥", "12 May", 1200)
    )

    // Sparkline data (relative heights 0f..1f)
    val chartData = listOf(0.3f, 0.5f, 0.4f, 0.7f, 0.6f, 0.9f, 0.8f)
    val chartDays = listOf("6 May", "8 May", "10 May", "12 May")

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) }
                },
                title = { Text("Dashboard", fontWeight = FontWeight.Bold, color = TextPrimary) },
                actions = {
                    Box {
                        TextButton(onClick = { rangeExpanded = true }) {
                            Text(selectedRange, color = ElectricPurple, fontSize = 13.sp)
                            Icon(Icons.Default.ArrowDropDown, null, tint = ElectricPurple)
                        }
                        DropdownMenu(
                            expanded = rangeExpanded,
                            onDismissRequest = { rangeExpanded = false },
                            modifier = Modifier.background(DarkCard)
                        ) {
                            ranges.forEach { range ->
                                DropdownMenuItem(
                                    text = { Text(range, color = TextPrimary, fontSize = 13.sp) },
                                    onClick = { selectedRange = range; rangeExpanded = false }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats grid
            item {
                Text("Overview", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DashboardStatCard("Reach", "12.4K", "+16.2%", true, modifier = Modifier.weight(1f))
                    DashboardStatCard("Engagement", "2.6K", "+21.3%", true, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DashboardStatCard("Profile Visits", "1.3K", "+15.7%", true, modifier = Modifier.weight(1f))
                    DashboardStatCard("New Followers", "+156", "+12.5%", true, modifier = Modifier.weight(1f))
                }
            }

            // Engagement Chart
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkCard, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text("Engagement over time", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Simple bar chart
                    Row(
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        chartData.forEach { value ->
                            Box(
                                modifier = Modifier
                                    .width(28.dp)
                                    .fillMaxHeight(value)
                                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                    .background(
                                        Brush.verticalGradient(listOf(ElectricPurple, LightPurple.copy(alpha = 0.5f)))
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        chartDays.forEach { day ->
                            Text(day, color = TextMuted, fontSize = 10.sp)
                        }
                    }
                }
            }

            // Top performing posts
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Top performing posts", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    TextButton(onClick = {}) { Text("See all", color = ElectricPurple, fontSize = 12.sp) }
                }

                Spacer(modifier = Modifier.height(8.dp))

                topPosts.forEach { (title, date, likes) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkCard, RoundedCornerShape(12.dp))
                            .clickable {}
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(title, color = TextPrimary, fontSize = 13.sp, maxLines = 1)
                            Text(date, color = TextMuted, fontSize = 11.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Favorite, null, tint = AccentRed, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (likes >= 1000) "${likes / 1000}.${(likes % 1000) / 100}K" else likes.toString(),
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Quick actions
            item {
                Text("Quick Actions", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionButton(Icons.Default.BarChart, "Analytics", ElectricPurple, Modifier.weight(1f))
                    QuickActionButton(Icons.Default.AttachMoney, "Monetize", AccentGreen, Modifier.weight(1f))
                    QuickActionButton(Icons.Default.EmojiEvents, "Badges", AccentYellow, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun DashboardStatCard(label: String, value: String, change: String, isPositive: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(DarkCard, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Text(label, color = TextMuted, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                null,
                tint = if (isPositive) AccentGreen else AccentRed,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(change, color = if (isPositive) AccentGreen else AccentRed, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun QuickActionButton(icon: ImageVector, label: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(DarkCard, RoundedCornerShape(14.dp))
            .clickable {}
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(color.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(label, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
