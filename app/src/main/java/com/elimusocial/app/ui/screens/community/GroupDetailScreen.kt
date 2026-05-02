package com.elimusocial.app.ui.screens.community

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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elimusocial.app.data.models.Community
import com.elimusocial.app.ui.theme.*

data class Discussion(
    val id: String,
    val title: String,
    val replies: Int,
    val timeAgo: String,
    val likes: Int,
    val isAnswered: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    community: Community,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Posts", "Topics", "Members", "Media")

    val discussions = listOf(
        Discussion("1", "How do I start with open source?", 12, "1h ago", 15),
        Discussion("2", "Best resources for learning Python?", 8, "4h ago", 9, isAnswered = true),
        Discussion("3", "Share your project idea", 22, "5h ago", 18),
        Discussion("4", "Laptop recommendations for developers?", 14, "6h ago", 11),
        Discussion("5", "What programming language are you currently learning?", 24, "3h ago", 36)
    )

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = TextPrimary)
                    }
                },
                title = {},
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Search, null, tint = TextSecondary) }
                    IconButton(onClick = {}) { Icon(Icons.Default.Notifications, null, tint = TextSecondary) }
                    IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, null, tint = TextSecondary) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = ElectricPurple,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Start a Discussion", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(
                            Brush.linearGradient(listOf(ElectricPurple, AccentBlue))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(community.name.first().toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(community.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Public Group · ${community.members / 1000.0}K members", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                }

                // Info section
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(community.description, color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Admins row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Admins", color = TextMuted, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                            listOf("A", "B", "C").forEach { letter ->
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(ElectricPurple)
                                        .border(2.dp, DarkBackground, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(letter, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("+3", color = TextMuted, fontSize = 12.sp)
                    }
                }

                HorizontalDivider(color = DividerColor)
            }

            // Tabs
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = DarkBackground,
                    contentColor = ElectricPurple,
                    indicator = { tabPositions ->
                        if (selectedTab < tabPositions.size) {
                            Box(
                                modifier = Modifier
                                    .tabIndicatorOffset(tabPositions[selectedTab])
                                    .height(2.dp)
                                    .background(ElectricPurple)
                            )
                        }
                    }
                ) {
                    tabs.forEachIndexed { i, tab ->
                        Tab(
                            selected = selectedTab == i,
                            onClick = { selectedTab = i },
                            text = {
                                Text(
                                    tab,
                                    color = if (selectedTab == i) ElectricPurple else TextMuted,
                                    fontSize = 13.sp
                                )
                            }
                        )
                    }
                }
                HorizontalDivider(color = DividerColor)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Discussions
            if (selectedTab == 0) {
                items(discussions) { disc ->
                    DiscussionItem(discussion = disc)
                    HorizontalDivider(color = DividerColor, modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

@Composable
fun DiscussionItem(discussion: Discussion) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {}
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (discussion.isAnswered) AccentGreen.copy(alpha = 0.2f)
                    else ElectricPurple.copy(alpha = 0.15f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (discussion.isAnswered) Icons.Default.CheckCircle else Icons.Default.Forum,
                null,
                tint = if (discussion.isAnswered) AccentGreen else ElectricPurple,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(discussion.title, color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.ChatBubbleOutline, null, tint = TextMuted, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("${discussion.replies} replies", color = TextMuted, fontSize = 12.sp)
                }
                Text("· ${discussion.timeAgo}", color = TextMuted, fontSize = 12.sp)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.FavoriteBorder, null, tint = TextMuted, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text(discussion.likes.toString(), color = TextMuted, fontSize = 12.sp)
        }
    }
}
