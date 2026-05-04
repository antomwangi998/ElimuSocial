package com.elimusocial.app.ui.screens.creator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elimusocial.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(onBack: () -> Unit) {
    var selectedPeriod by remember { mutableStateOf("Last 7 days") }
    val periods = listOf("Last 7 days", "Last 30 days", "Last 3 months")
    var periodExpanded by remember { mutableStateOf(false) }

    // Fake chart data points
    val chartData = listOf(0.3f, 0.5f, 0.4f, 0.7f, 0.6f, 0.9f, 0.8f)
    val days = listOf("6 May", "7 May", "8 May", "9 May", "10 May", "11 May", "12 May")

    val topPosts = listOf(
        Triple("Discipline today, freedom tomorrow.", "8 May", "12.3K"),
        Triple("5 tips to improve your coding skills 🚀", "6 May", "8.7K"),
        Triple("Just finished our CS project! 🔥", "10 May", "6.2K"),
    )

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                title = { Text("Analytics", fontWeight = FontWeight.Bold, color = TextPrimary) },
                actions = {
                    Box {
                        TextButton(onClick = { periodExpanded = true }) {
                            Text(selectedPeriod, color = ElectricPurple, fontSize = 13.sp)
                            Icon(Icons.Default.KeyboardArrowDown, null, tint = ElectricPurple, modifier = Modifier.size(16.dp))
                        }
                        DropdownMenu(expanded = periodExpanded, onDismissRequest = { periodExpanded = false }, modifier = Modifier.background(DarkCard)) {
                            periods.forEach { period ->
                                DropdownMenuItem(text = { Text(period, color = TextPrimary) }, onClick = { selectedPeriod = period; periodExpanded = false })
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            // Stats grid
            item {
                Text("Overview", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AnalyticsStatCard(label = "Impressions", value = "12.4K", change = "+18.5%", positive = true, modifier = Modifier.weight(1f))
                    AnalyticsStatCard(label = "Engagement", value = "2.6K", change = "+21.3%", positive = true, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AnalyticsStatCard(label = "Profile Visits", value = "1.3K", change = "+15.7%", positive = true, modifier = Modifier.weight(1f))
                    AnalyticsStatCard(label = "New Followers", value = "+156", change = "+12.5%", positive = true, modifier = Modifier.weight(1f))
                }
            }

            // Engagement chart
            item {
                Card(colors = CardDefaults.cardColors(containerColor = DarkCard), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Engagement Over Time", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Icon(Icons.Default.OpenInFull, null, tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        // Simple bar chart
                        Row(
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            chartData.forEachIndexed { index, value ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier.width(28.dp).height((value * 80).dp)
                                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                            .background(Brush.verticalGradient(listOf(ElectricPurple, AccentBlue.copy(alpha = 0.6f))))
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            days.forEach { day ->
                                Text(day.take(1) + day.drop(2).take(1), color = TextMuted, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            // Top posts
            item {
                Text("Top Performing Posts", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(topPosts.size) { index ->
                val (content, date, impressions) = topPosts[index]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(content, color = TextPrimary, fontSize = 14.sp, maxLines = 2, fontWeight = FontWeight.Medium)
                            Text(date, color = TextMuted, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(horizontalAlignment = Alignment.End) {
                            Text(impressions, color = ElectricPurple, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("impressions", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }
            }

            // Audience breakdown
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Audience", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = DarkCard), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        AudienceBar("Students", 0.65f, ElectricPurple)
                        AudienceBar("Teachers", 0.20f, AccentBlue)
                        AudienceBar("Professionals", 0.10f, AccentGreen)
                        AudienceBar("Others", 0.05f, AccentOrange)
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsStatCard(label: String, value: String, change: String, positive: Boolean, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = DarkCard), shape = RoundedCornerShape(14.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(label, color = TextMuted, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(if (positive) Icons.Default.TrendingUp else Icons.Default.TrendingDown, null, tint = if (positive) AccentGreen else AccentRed, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(change, color = if (positive) AccentGreen else AccentRed, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun AudienceBar(label: String, fraction: Float, color: Color) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = TextSecondary, fontSize = 13.sp, modifier = Modifier.width(100.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = DarkBackground
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("${(fraction * 100).toInt()}%", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(32.dp))
    }
}
