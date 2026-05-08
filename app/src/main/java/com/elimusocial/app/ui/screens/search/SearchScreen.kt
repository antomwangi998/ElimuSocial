package com.elimusocial.app.ui.screens.search

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elimusocial.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(onBack: () -> Unit, onNavigate: (String) -> Unit = {}) {
    var query by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "Users", "Posts", "Groups", "Hashtags")
    var isSearchActive by remember { mutableStateOf(false) }

    val recentSearches = remember { mutableStateListOf("Python", "Elimu Hackathon", "@teacher_alex", "#ElimuSocial") }

    val users = remember {
        mutableStateListOf(
            listOf("Antony Mwangi", "@antony_mwangi", "Building solutions that empower students", true, false),
            listOf("Mary Wanjiku", "@mary_edu", "Student | Learning everyday 📚", false, false),
            listOf("Brian Otieno", "@brian_dev", "Full Stack Developer | Open source", true, false),
            listOf("Joyce Maina", "@joyce_m", "UI/UX Designer | Creator", false, false),
            listOf("Teacher Alex", "@teacher_alex", "CS Teacher | Mentor", true, false),
            listOf("Code With Joyce", "@joyce_code", "Teaching code to beginners 💻", false, false),
            listOf("Programmer Brian", "@brian_dev2", "Backend engineer", false, false),
        )
    }
    val followStates = remember { mutableStateListOf(*Array(users.size) { false }) }

    val posts = listOf(
        Triple("Antony Mwangi", "5 productivity tips for programmers 💻 #programming", "128 likes"),
        Triple("Tech Hub", "Best programming languages to learn in 2024 🚀", "87 likes"),
        Triple("Mary Wanjiku", "Study tips that actually work 📚 #education", "64 likes"),
        Triple("Teacher Alex", "New resources for Computer Science students 🎓", "201 likes"),
        Triple("Brian Otieno", "Just deployed my first web app! Check it out 🚀", "45 likes"),
    )

    val groups = remember {
        mutableStateListOf(
            listOf("Python Developers", "1.2K members", "💻", "Tech"),
            listOf("Study Together", "2.1K members", "📚", "Education"),
            listOf("CS Hub", "3.4K members", "🖥️", "Tech"),
            listOf("Entrepreneurship Club", "980 members", "🚀", "Business"),
            listOf("Math Excellence", "1.8K members", "📐", "Education"),
            listOf("Writers' Corner", "890 members", "✍️", "Creative"),
        )
    }
    val joinStates = remember { mutableStateListOf(*Array(groups.size) { false }) }

    val hashtags = listOf(
        Pair("#education", "12.4K posts"), Pair("#programming", "8.7K posts"),
        Pair("#teamwork", "5.2K posts"), Pair("#ElimuSocial", "3.1K posts"),
        Pair("#KCSE", "2.8K posts"), Pair("#LearnToCode", "1.9K posts"),
        Pair("#StudyTips", "4.3K posts"), Pair("#TechKenya", "6.1K posts"),
    )

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                title = {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it; isSearchActive = it.isNotEmpty() },
                        placeholder = { Text("Search users, posts, groups...", color = TextMuted, fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricPurple, unfocusedBorderColor = DividerColor,
                            focusedContainerColor = DarkCard, unfocusedContainerColor = DarkCard,
                            focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = ElectricPurple
                        ),
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = TextMuted, modifier = Modifier.size(18.dp)) },
                        trailingIcon = {
                            if (query.isNotEmpty()) IconButton(onClick = { query = ""; isSearchActive = false }) {
                                Icon(Icons.Default.Close, null, tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                        },
                        singleLine = true
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab, containerColor = DarkBackground, contentColor = ElectricPurple,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size)
                        Box(modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]).height(2.dp).background(ElectricPurple))
                }
            ) {
                tabs.forEachIndexed { i, tab ->
                    Tab(selected = selectedTab == i, onClick = { selectedTab = i },
                        text = { Text(tab, color = if (selectedTab == i) ElectricPurple else TextMuted, fontSize = 14.sp) })
                }
            }
            HorizontalDivider(color = DividerColor, thickness = 0.5.dp)

            // Recent searches (when no query)
            if (!isSearchActive && selectedTab == 0) {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Recent searches", color = TextSecondary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            TextButton(onClick = { recentSearches.clear() }) { Text("Clear all", color = ElectricPurple, fontSize = 13.sp) }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                    items(recentSearches) { term ->
                        Row(modifier = Modifier.fillMaxWidth().clickable { query = term; isSearchActive = true }.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.History, null, tint = TextMuted, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(12.dp))
                            Text(term, color = TextPrimary, fontSize = 15.sp, modifier = Modifier.weight(1f))
                            IconButton(onClick = { recentSearches.remove(term) }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Close, null, tint = TextMuted, modifier = Modifier.size(14.dp))
                            }
                        }
                        HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
                    }
                    // Trending
                    item {
                        Spacer(Modifier.height(16.dp))
                        Text("Trending", color = TextSecondary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                    }
                    items(hashtags.take(5)) { (tag, count) ->
                        Row(modifier = Modifier.fillMaxWidth().clickable { query = tag; isSearchActive = true }.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(ElectricPurple.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                                Text("#", color = ElectricPurple, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(tag, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(count, color = TextMuted, fontSize = 12.sp)
                            }
                            Icon(Icons.Default.TrendingUp, null, tint = AccentGreen, modifier = Modifier.size(16.dp))
                        }
                        HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
                    }
                }
            } else {
                // Search results
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                    when (selectedTab) {
                        0 -> { // All
                            item { SectionHeader("Users") { selectedTab = 1 } }
                            items(users.filter { query.isEmpty() || (it[0] as String).contains(query, ignoreCase = true) || (it[1] as String).contains(query, ignoreCase = true) }.take(3).mapIndexed { i, u -> Pair(i, u) }) { (idx, user) ->
                                UserResultItem(name = user[0] as String, handle = user[1] as String, bio = user[2] as String, isVerified = user[3] as Boolean, isFollowing = followStates.getOrElse(idx) { false }, onFollow = { if (idx < followStates.size) followStates[idx] = !followStates[idx] })
                            }
                            item { SectionHeader("Posts") { selectedTab = 2 }; Spacer(Modifier.height(4.dp)) }
                            items(posts.filter { query.isEmpty() || it.second.contains(query, ignoreCase = true) }.take(2)) { (author, content, stat) ->
                                PostResultItem(author = author, content = content, stat = stat)
                            }
                            item { SectionHeader("Groups") { selectedTab = 3 }; Spacer(Modifier.height(4.dp)) }
                            items(groups.filter { query.isEmpty() || (it[0] as String).contains(query, ignoreCase = true) }.take(2).mapIndexed { i, g -> Pair(i, g) }) { (idx, group) ->
                                GroupResultItem(name = group[0] as String, members = group[1] as String, emoji = group[2] as String, isJoined = joinStates.getOrElse(idx) { false }, onJoin = { if (idx < joinStates.size) joinStates[idx] = !joinStates[idx] })
                            }
                        }
                        1 -> { // Users
                            items(users.filter { query.isEmpty() || (it[0] as String).contains(query, ignoreCase = true) || (it[1] as String).contains(query, ignoreCase = true) }.mapIndexed { i, u -> Pair(i, u) }) { (idx, user) ->
                                UserResultItem(name = user[0] as String, handle = user[1] as String, bio = user[2] as String, isVerified = user[3] as Boolean, isFollowing = followStates.getOrElse(idx) { false }, onFollow = { if (idx < followStates.size) followStates[idx] = !followStates[idx] })
                            }
                        }
                        2 -> { // Posts
                            items(posts.filter { query.isEmpty() || it.second.contains(query, ignoreCase = true) }) { (author, content, stat) ->
                                PostResultItem(author = author, content = content, stat = stat)
                            }
                        }
                        3 -> { // Groups
                            items(groups.filter { query.isEmpty() || (it[0] as String).contains(query, ignoreCase = true) }.mapIndexed { i, g -> Pair(i, g) }) { (idx, group) ->
                                GroupResultItem(name = group[0] as String, members = group[1] as String, emoji = group[2] as String, isJoined = joinStates.getOrElse(idx) { false }, onJoin = { if (idx < joinStates.size) joinStates[idx] = !joinStates[idx] })
                            }
                        }
                        4 -> { // Hashtags
                            items(hashtags.filter { query.isEmpty() || it.first.contains(query, ignoreCase = true) }) { (tag, count) ->
                                Row(modifier = Modifier.fillMaxWidth().clickable {}.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(ElectricPurple.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                                        Text("#", color = ElectricPurple, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(tag, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                        Text(count, color = TextMuted, fontSize = 13.sp)
                                    }
                                }
                                HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, onSeeAll: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        TextButton(onClick = onSeeAll) { Text("See all →", color = ElectricPurple, fontSize = 13.sp) }
    }
}

@Composable
fun UserResultItem(name: String, handle: String, bio: String, isVerified: Boolean, isFollowing: Boolean, onFollow: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable {}.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(46.dp).clip(CircleShape).background(ElectricPurple), contentAlignment = Alignment.Center) {
            Text(name.firstOrNull()?.toString() ?: "?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(name, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 14.sp)
                if (isVerified) { Spacer(Modifier.width(4.dp)); Icon(Icons.Default.Verified, null, tint = ElectricPurple, modifier = Modifier.size(14.dp)) }
            }
            Text(handle, color = TextMuted, fontSize = 12.sp)
            if (bio.isNotEmpty()) Text(bio, color = TextSecondary, fontSize = 12.sp, maxLines = 1)
        }
        Button(onClick = onFollow,
            colors = ButtonDefaults.buttonColors(containerColor = if (isFollowing) DarkCard else ElectricPurple),
            shape = RoundedCornerShape(20.dp), contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            modifier = Modifier.height(32.dp), border = if (isFollowing) BorderStroke(1.dp, DividerColor) else null) {
            Text(if (isFollowing) "Following" else "Follow", fontSize = 12.sp, color = TextPrimary)
        }
    }
    HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
}

@Composable
fun PostResultItem(author: String, content: String, stat: String) {
    Row(modifier = Modifier.fillMaxWidth().clickable {}.padding(vertical = 10.dp), verticalAlignment = Alignment.Top) {
        Box(modifier = Modifier.size(38.dp).clip(CircleShape).background(ElectricPurple), contentAlignment = Alignment.Center) {
            Text(author.firstOrNull()?.toString() ?: "?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(author, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 13.sp)
            Text(content, color = TextSecondary, fontSize = 13.sp, maxLines = 2)
            Text(stat, color = TextMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
    HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
}

@Composable
fun GroupResultItem(name: String, members: String, emoji: String, isJoined: Boolean, onJoin: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable {}.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(46.dp).clip(RoundedCornerShape(12.dp)).background(ElectricPurple.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
            Text(emoji, fontSize = 22.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 14.sp)
            Text(members, color = TextMuted, fontSize = 12.sp)
        }
        Button(onClick = onJoin,
            colors = ButtonDefaults.buttonColors(containerColor = if (isJoined) DarkCard else ElectricPurple),
            shape = RoundedCornerShape(20.dp), contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            modifier = Modifier.height(32.dp), border = if (isJoined) BorderStroke(1.dp, DividerColor) else null) {
            Text(if (isJoined) "Joined" else "Join", fontSize = 12.sp, color = TextPrimary)
        }
    }
    HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
}
