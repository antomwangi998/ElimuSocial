package com.elimusocial.app.ui.screens.bookmarks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elimusocial.app.ui.theme.*

data class BookmarkedPost(
    val id: String,
    val author: String,
    val handle: String,
    val content: String,
    val time: String,
    val likes: Int,
    val comments: Int,
    val reposts: Int,
    val type: String = "post" // post, article, video
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "Posts", "Articles", "Videos")

    val bookmarks = listOf(
        BookmarkedPost("1", "Antony Mwangi", "@antony", "Discipline today, freedom tomorrow. 💪\n\nSuccess doesn't come overnight...", "2d", 64, 12, 8),
        BookmarkedPost("2", "Mary Wanjiku", "@mary", "Some beautiful moments from the campus 🌅 #Blessed", "3d", 32, 4, 2),
        BookmarkedPost("3", "Teacher Alex", "@teacher_alex", "5 study tips that actually work 📚\n\n1. Pomodoro technique...", "5d", 85, 23, 15, "article"),
        BookmarkedPost("4", "Tech Hub", "@tech_hub", "The future of AI in education 🤖", "1w", 120, 34, 28, "video"),
        BookmarkedPost("5", "Brian Otieno", "@brian_dev", "Just deployed my first web app! Check it out 🚀", "1w", 45, 18, 9),
    )

    val filtered = when (selectedTab) {
        1 -> bookmarks.filter { it.type == "post" }
        2 -> bookmarks.filter { it.type == "article" }
        3 -> bookmarks.filter { it.type == "video" }
        else -> bookmarks
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) }
                },
                title = { Text("Bookmarks", fontWeight = FontWeight.Bold, color = TextPrimary) },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.FilterList, null, tint = TextSecondary) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkBackground,
                contentColor = ElectricPurple,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        Box(modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]).height(2.dp).background(ElectricPurple))
                    }
                }
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(tab, color = if (selectedTab == index) ElectricPurple else TextMuted, fontSize = 14.sp) }
                    )
                }
            }
            HorizontalDivider(color = DividerColor, thickness = 0.5.dp)

            if (filtered.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔖", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No bookmarks yet", color = TextMuted, fontSize = 16.sp)
                        Text("Save posts to read later", color = TextMuted, fontSize = 13.sp)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filtered, key = { it.id }) { post ->
                        BookmarkedPostItem(post = post)
                        HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun BookmarkedPostItem(post: BookmarkedPost) {
    var liked by remember { mutableStateOf(false) }
    var likeCount by remember { mutableIntStateOf(post.likes) }

    Column(modifier = Modifier.fillMaxWidth().clickable {}.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(ElectricPurple), contentAlignment = Alignment.Center) {
                Text(post.author.firstOrNull()?.toString() ?: "?", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(post.author, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 14.sp)
                Text(post.handle, color = TextMuted, fontSize = 13.sp)
            }
            Text(post.time, color = TextMuted, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = {}, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Filled.Bookmark, null, tint = ElectricPurple, modifier = Modifier.size(18.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(post.content, color = TextPrimary, fontSize = 14.sp, maxLines = 3)
        if (post.type != "post") {
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier.background(DarkCard, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(post.type.replaceFirstChar { it.uppercase() }, color = ElectricPurple, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {}) {
                Icon(Icons.Outlined.ChatBubbleOutline, null, tint = TextMuted, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(post.comments.toString(), color = TextMuted, fontSize = 12.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {}) {
                Icon(Icons.Outlined.Repeat, null, tint = TextMuted, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(post.reposts.toString(), color = TextMuted, fontSize = 12.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { liked = !liked; likeCount = if (liked) likeCount + 1 else likeCount - 1 }) {
                Icon(if (liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, null, tint = if (liked) AccentRed else TextMuted, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(likeCount.toString(), color = TextMuted, fontSize = 12.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {}) {
                Icon(Icons.Outlined.Share, null, tint = TextMuted, modifier = Modifier.size(16.dp))
            }
        }
    }
}
