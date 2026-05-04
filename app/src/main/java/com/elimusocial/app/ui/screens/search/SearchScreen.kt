package com.elimusocial.app.ui.screens.search

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elimusocial.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(onBack: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "Users", "Posts", "Groups", "Hashtags")

    val users = listOf(
        Triple("Antony Mwangi", "@antony_mwangi", true),
        Triple("Mary Wanjiku", "@mary_edu", false),
        Triple("Brian Otieno", "@brian_dev", false),
        Triple("Joyce Maina", "@joyce_m", true),
        Triple("Teacher Alex", "@teacher_alex", false),
        Triple("Code With Joyce", "@joyce_code", false),
        Triple("Programmer Brian", "@brian_dev2", false),
    )

    val posts = listOf(
        Pair("Antony Mwangi", "5 productivity tips for programmers 💻"),
        Pair("Tech Hub", "Best programming languages in 2024"),
        Pair("Mary Wanjiku", "Study tips that actually work 📚"),
        Pair("Teacher Alex", "New resources for Computer Science students"),
    )

    val groups = listOf(
        Triple("Python Developers", "1.2K members", "💻"),
        Triple("Study Together", "2.1K members", "📚"),
        Triple("CS Hub", "3.4K members", "🖥️"),
        Triple("Entrepreneurship Club", "980 members", "🚀"),
    )

    val hashtags = listOf(
        Pair("#education", "12.4K posts"),
        Pair("#programming", "8.7K posts"),
        Pair("#teamwork", "5.2K posts"),
        Pair("#ElimuSocial", "3.1K posts"),
        Pair("#KCSE", "2.8K posts"),
        Pair("#LearnToCode", "1.9K posts"),
    )

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) }
                },
                title = {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Search users, posts, groups...", color = TextMuted, fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricPurple, unfocusedBorderColor = DividerColor,
                            focusedContainerColor = DarkCard, unfocusedContainerColor = DarkCard,
                            focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                            cursorColor = ElectricPurple
                        ),
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = TextMuted, modifier = Modifier.size(18.dp)) },
                        singleLine = true
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkBackground,
                contentColor = ElectricPurple,
                edgePadding = 16.dp,
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

            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                when (selectedTab) {
                    0 -> { // All
                        item {
                            Text("Users", fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        items(users.take(3)) { (name, handle, verified) ->
                            SearchUserItem(name = name, handle = handle, verified = verified)
                        }
                        item {
                            TextButton(onClick = { selectedTab = 1 }) {
                                Text("See all users →", color = ElectricPurple, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Posts", fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        items(posts.take(2)) { (author, content) ->
                            SearchPostItem(author = author, content = content)
                        }
                        item {
                            TextButton(onClick = { selectedTab = 2 }) {
                                Text("See all posts →", color = ElectricPurple, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Groups", fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        items(groups.take(2)) { (name, members, emoji) ->
                            SearchGroupItem(name = name, members = members, emoji = emoji)
                        }
                    }
                    1 -> { // Users
                        items(users.filter { query.isEmpty() || it.first.contains(query, ignoreCase = true) || it.second.contains(query, ignoreCase = true) }) { (name, handle, verified) ->
                            SearchUserItem(name = name, handle = handle, verified = verified)
                        }
                    }
                    2 -> { // Posts
                        items(posts.filter { query.isEmpty() || it.second.contains(query, ignoreCase = true) }) { (author, content) ->
                            SearchPostItem(author = author, content = content)
                        }
                    }
                    3 -> { // Groups
                        items(groups.filter { query.isEmpty() || it.first.contains(query, ignoreCase = true) }) { (name, members, emoji) ->
                            SearchGroupItem(name = name, members = members, emoji = emoji)
                        }
                    }
                    4 -> { // Hashtags
                        items(hashtags.filter { query.isEmpty() || it.first.contains(query, ignoreCase = true) }) { (tag, count) ->
                            SearchHashtagItem(tag = tag, count = count)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchUserItem(name: String, handle: String, verified: Boolean) {
    var following by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth().clickable {}.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(ElectricPurple), contentAlignment = Alignment.Center) {
            Text(name.firstOrNull()?.toString() ?: "?", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(name, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 14.sp)
                if (verified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.Verified, null, tint = ElectricPurple, modifier = Modifier.size(14.dp))
                }
            }
            Text(handle, color = TextMuted, fontSize = 13.sp)
        }
        Button(
            onClick = { following = !following },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (following) DarkCard else ElectricPurple
            ),
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            modifier = Modifier.height(32.dp)
        ) {
            Text(if (following) "Following" else "Follow", fontSize = 12.sp, color = TextPrimary)
        }
    }
}

@Composable
fun SearchPostItem(author: String, content: String) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable {}.padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(ElectricPurple), contentAlignment = Alignment.Center) {
            Text(author.firstOrNull()?.toString() ?: "?", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(author, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 13.sp)
            Text(content, color = TextSecondary, fontSize = 13.sp, maxLines = 2)
        }
    }
    HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
}

@Composable
fun SearchGroupItem(name: String, members: String, emoji: String) {
    var joined by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth().clickable {}.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(DarkCard),
            contentAlignment = Alignment.Center
        ) { Text(emoji, fontSize = 22.sp) }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 14.sp)
            Text(members, color = TextMuted, fontSize = 12.sp)
        }
        Button(
            onClick = { joined = !joined },
            colors = ButtonDefaults.buttonColors(containerColor = if (joined) DarkCard else ElectricPurple),
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            modifier = Modifier.height(32.dp)
        ) { Text(if (joined) "Joined" else "Join", fontSize = 12.sp, color = TextPrimary) }
    }
}

@Composable
fun SearchHashtagItem(tag: String, count: String) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable {}.padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(DarkCard), contentAlignment = Alignment.Center) {
            Text("#", color = ElectricPurple, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(tag, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 14.sp)
            Text(count, color = TextMuted, fontSize = 12.sp)
        }
    }
    HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
}
