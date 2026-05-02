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
import com.elimusocial.app.data.models.SampleData
import com.elimusocial.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunitiesScreen(
    onCommunityClick: (Community) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Your Communities", "Discover")

    val discoverCommunities = listOf(
        Community("5", "Python Developers", "Learn and build with Python", members = 4500, newPosts = 18),
        Community("6", "UI/UX Designers", "Design beautiful interfaces", members = 2300, newPosts = 7),
        Community("7", "Math Excellence", "Master mathematics together", members = 1800, newPosts = 15),
        Community("8", "Science Club", "Explore the wonders of science", members = 3100, newPosts = 22)
    )

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Communities", fontWeight = FontWeight.Bold, color = TextPrimary) },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Search, null, tint = TextSecondary)
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Add, null, tint = ElectricPurple)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Tabs
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
                                fontSize = 14.sp
                            )
                        }
                    )
                }
            }

            val list = if (selectedTab == 0) SampleData.communities else discoverCommunities

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(list) { community ->
                    CommunityCard(community = community, onClick = { onCommunityClick(community) })
                }
            }
        }
    }
}

@Composable
fun CommunityCard(community: Community, onClick: () -> Unit) {
    val gradients = listOf(
        listOf(Color(0xFF6C3CE1), Color(0xFF3B82F6)),
        listOf(Color(0xFF059669), Color(0xFF3B82F6)),
        listOf(Color(0xFFD97706), Color(0xFFEF4444)),
        listOf(Color(0xFF7C3AED), Color(0xFFEC4899))
    )
    val gradient = gradients[community.id.hashCode().and(0x7FFFFFFF) % gradients.size]

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkCard, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(gradient)),
            contentAlignment = Alignment.Center
        ) {
            Text(community.name.first().toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(community.name, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(community.description, color = TextMuted, fontSize = 12.sp, maxLines = 1)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "${community.members / 1000.0}K members",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                if (community.newPosts > 0) {
                    Box(
                        modifier = Modifier
                            .background(ElectricPurple.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("${community.newPosts} new", color = LightPurple, fontSize = 11.sp)
                    }
                }
            }
        }

        Icon(Icons.Default.ChevronRight, null, tint = TextMuted, modifier = Modifier.size(20.dp))
    }
}
