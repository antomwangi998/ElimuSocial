package com.elimusocial.app.ui.screens.home

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elimusocial.app.data.models.Post
import com.elimusocial.app.data.models.SampleData
import com.elimusocial.app.data.models.User
import com.elimusocial.app.ui.screens.components.BottomNavBar
import com.elimusocial.app.ui.screens.components.PostCard
import com.elimusocial.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onLogout: () -> Unit, onNavigate: (String) -> Unit = {}) {
    var selectedTab by remember { mutableStateOf("feed") }
    var selectedFeedTab by remember { mutableStateOf(0) }
    val feedTabs = listOf("For You", "Following", "Groups")

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            HomeTopBar(onLogout = onLogout, onNavigate = onNavigate)
        },
        bottomBar = {
            BottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { route ->
                    // Intercept special routes that go to their own screen
                    when (route) {
                        "messages" -> onNavigate("messages")
                        "ai"       -> onNavigate("elimu_ai")
                        else       -> selectedTab = route
                    }
                }
            )
        }
    ) { paddingValues ->
        when (selectedTab) {
            "feed" -> FeedContent(
                paddingValues = paddingValues,
                selectedFeedTab = selectedFeedTab,
                feedTabs = feedTabs,
                onTabSelected = { selectedFeedTab = it },
                posts = SampleData.posts,
                onNavigate = onNavigate
            )
            "explore"       -> ExploreContent(paddingValues)
            "notifications" -> NotificationsContent(paddingValues)
            "profile"       -> ProfileContent(paddingValues, SampleData.users[0], onNavigate)
            else -> FeedContent(
                paddingValues = paddingValues,
                selectedFeedTab = selectedFeedTab,
                feedTabs = feedTabs,
                onTabSelected = { selectedFeedTab = it },
                posts = SampleData.posts,
                onNavigate = onNavigate
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(onLogout: () -> Unit, onNavigate: (String) -> Unit = {}) {
    var menuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            Brush.linearGradient(listOf(ElectricPurple, AccentBlue)),
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⚡", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    "Elimu Social",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 18.sp
                )
            }
        },
        actions = {
            // Reels shortcut
            IconButton(onClick = { onNavigate("reels") }) {
                Icon(Icons.Default.VideoLibrary, contentDescription = "Reels", tint = TextSecondary)
            }
            // Messages shortcut
            IconButton(onClick = { onNavigate("messages") }) {
                Icon(Icons.Default.Mail, contentDescription = "Messages", tint = TextSecondary)
            }
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More", tint = TextSecondary)
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.background(DarkCard)
                ) {
                    DropdownMenuItem(
                        text = { Text("Elimu AI", color = LightPurple) },
                        onClick = { menuExpanded = false; onNavigate("elimu_ai") },
                        leadingIcon = { Text("🤖") }
                    )
                    DropdownMenuItem(
                        text = { Text("Study Planner", color = TextPrimary) },
                        onClick = { menuExpanded = false; onNavigate("study_planner") },
                        leadingIcon = { Icon(Icons.Default.CalendarMonth, null, tint = AccentGreen) }
                    )
                    DropdownMenuItem(
                        text = { Text("Communities", color = TextPrimary) },
                        onClick = { menuExpanded = false; onNavigate("communities") },
                        leadingIcon = { Icon(Icons.Default.Group, null, tint = AccentBlue) }
                    )
                    DropdownMenuItem(
                        text = { Text("Events", color = TextPrimary) },
                        onClick = { menuExpanded = false; onNavigate("events") },
                        leadingIcon = { Icon(Icons.Default.Event, null, tint = AccentOrange) }
                    )
                    DropdownMenuItem(
                        text = { Text("Dashboard", color = TextPrimary) },
                        onClick = { menuExpanded = false; onNavigate("creator_dashboard") },
                        leadingIcon = { Icon(Icons.Default.BarChart, null, tint = ElectricPurple) }
                    )
                    HorizontalDivider(color = DividerColor)
                    DropdownMenuItem(
                        text = { Text("Settings", color = TextPrimary) },
                        onClick = { menuExpanded = false; onNavigate("settings") },
                        leadingIcon = { Icon(Icons.Outlined.Settings, null, tint = TextSecondary) }
                    )
                    DropdownMenuItem(
                        text = { Text("Log out", color = AccentRed) },
                        onClick = { menuExpanded = false; onLogout() },
                        leadingIcon = { Icon(Icons.Outlined.Logout, null, tint = AccentRed) }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
    )
}

@Composable
fun FeedContent(
    paddingValues: PaddingValues,
    selectedFeedTab: Int,
    feedTabs: List<String>,
    onTabSelected: (Int) -> Unit,
    posts: List<Post>,
    onNavigate: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Stories row
        item {
            com.elimusocial.app.ui.screens.social.StoriesRow()
        }

        // Feed Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                feedTabs.forEachIndexed { index, tab ->
                    FilterChip(
                        selected = selectedFeedTab == index,
                        onClick = { onTabSelected(index) },
                        label = {
                            Text(
                                tab,
                                fontSize = 13.sp,
                                fontWeight = if (selectedFeedTab == index) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricPurple,
                            selectedLabelColor = TextPrimary,
                            containerColor = DarkCard,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedFeedTab == index,
                            borderColor = DividerColor,
                            selectedBorderColor = ElectricPurple
                        )
                    )
                }
            }
        }

        // Create Post Input
        item {
            CreatePostInput()
        }

        // Posts
        items(posts) { post ->
            PostCard(post = post)
        }
    }
}

@Composable
fun CreatePostInput() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(ElectricPurple),
            contentAlignment = Alignment.Center
        ) {
            Text("A", color = TextPrimary, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(42.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(DarkCard)
                .border(1.dp, DividerColor, RoundedCornerShape(22.dp))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text("What's on your mind?", color = TextMuted, fontSize = 14.sp)
        }
    }
    HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
}

// ===== EXPLORE SCREEN =====
@Composable
fun ExploreContent(paddingValues: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text("Explore", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))

            // Search bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkCard)
                    .border(1.dp, DividerColor, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Search, null, tint = TextMuted, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Search posts, people, groups...", color = TextMuted, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("What's happening", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))
        }

        val trending = listOf(
            Triple("New policy on student loans", "Trending now", "2.5K posts"),
            Triple("Elimu Hackathon 2024", "Trending in Tech", "1.2K posts"),
            Triple("Campus life moments ✨", "Trending in Campus", "987 posts")
        )

        items(trending) { (title, tag, count) ->
            TrendingItem(title = title, tag = tag, count = count)
            HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text("Who to follow", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(SampleData.users.takeLast(3)) { user ->
            SuggestedUserItem(user = user)
        }
    }
}

@Composable
fun TrendingItem(title: String, tag: String, count: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {}
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(tag, color = TextMuted, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(title, color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(count, color = TextMuted, fontSize = 12.sp)
        }
        Icon(Icons.Default.MoreVert, null, tint = TextMuted, modifier = Modifier.size(18.dp))
    }
}

@Composable
fun SuggestedUserItem(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(ElectricPurple),
            contentAlignment = Alignment.Center
        ) {
            Text(user.name.first().toString(), color = TextPrimary, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(user.name, color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Text(user.username, color = TextMuted, fontSize = 12.sp)
        }
        OutlinedButton(
            onClick = {},
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, ElectricPurple),
            modifier = Modifier.height(34.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            Text("Follow", color = ElectricPurple, fontSize = 13.sp)
        }
    }
}

// ===== NOTIFICATIONS SCREEN =====
@Composable
fun NotificationsContent(paddingValues: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text("Notifications", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(SampleData.notifications) { notification ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (!notification.isRead) DarkCard else DarkBackground,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(ElectricPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        notification.actor.name.first().toString(),
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "${notification.actor.name} ${notification.message}",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(notification.timestamp, color = TextMuted, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

// ===== PROFILE SCREEN =====
@Composable
fun ProfileContent(paddingValues: PaddingValues, user: User, onNavigate: (String) -> Unit = {}) {
    var selectedProfileTab by remember { mutableStateOf(0) }
    val profileTabs = listOf("Posts", "Replies", "Media", "Likes")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        item {
            // Cover image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        Brush.linearGradient(listOf(ElectricPurple, AccentBlue))
                    )
            )

            // Avatar + Edit button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-24).dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(DarkBackground)
                        .border(3.dp, DarkBackground, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(74.dp)
                            .clip(CircleShape)
                            .background(ElectricPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            user.name.first().toString(),
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )
                    }
                }

                OutlinedButton(
                    onClick = {},
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, DividerColor),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Edit profile", color = TextPrimary, fontSize = 13.sp)
                }
            }

            // User info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-16).dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(user.name, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 20.sp)
                    if (user.isVerified) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Filled.Verified, null, tint = ElectricPurple, modifier = Modifier.size(18.dp))
                    }
                }
                Text(user.username, color = TextMuted, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(user.bio, color = TextPrimary, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.LocationOn, null, tint = TextMuted, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(user.location, color = TextMuted, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    StatItem(count = user.posts.toString(), label = "Posts")
                    StatItem(count = "${user.followers / 1000.0}K", label = "Followers")
                    StatItem(count = user.following.toString(), label = "Following")
                }
            }

            // Profile tabs
            ScrollableTabRow(
                selectedTabIndex = selectedProfileTab,
                containerColor = DarkBackground,
                contentColor = ElectricPurple,
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    if (selectedProfileTab < tabPositions.size) {
                        Box(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[selectedProfileTab])
                                .height(2.dp)
                                .background(ElectricPurple)
                        )
                    }
                }
            ) {
                profileTabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedProfileTab == index,
                        onClick = { selectedProfileTab = index },
                        text = {
                            Text(
                                tab,
                                color = if (selectedProfileTab == index) ElectricPurple else TextMuted,
                                fontSize = 14.sp
                            )
                        }
                    )
                }
            }

            HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
        }

        items(SampleData.posts.take(3)) { post ->
            PostCard(post = post)
        }
    }
}

@Composable
fun StatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 18.sp)
        Text(label, color = TextMuted, fontSize = 12.sp)
    }
}
