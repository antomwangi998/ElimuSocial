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
import com.elimusocial.app.ui.theme.*

data class Community(
    val id: String,
    val name: String,
    val description: String,
    val members: Int = 0,
    val newPosts: Int = 0,
    val category: String = "General",
    val emoji: String = "👥",
    val isJoined: Boolean = false,
    val isPrivate: Boolean = false
)

data class GroupPost(
    val id: String,
    val author: String,
    val content: String,
    val likes: Int,
    val replies: Int,
    val time: String
)

data class Discussion(
    val id: String,
    val title: String,
    val replies: Int,
    val time: String,
    val isHot: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunitiesScreen(onCommunityClick: (Community) -> Unit = {}) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showCreateGroup by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val myCommunities = remember {
        mutableStateListOf(
            Community("1", "Computer Science Hub", "Students who love coding, tech & solutions", 1200, 25, "Tech", "💻", isJoined = true),
            Community("2", "Elimu Announcements", "Official updates from Elimu", 3400, 8, "Official", "📢", isJoined = true),
            Community("3", "Entrepreneurship Club", "Grow ideas, build solutions", 980, 12, "Business", "🚀", isJoined = true),
            Community("4", "Study Together", "Learn and grow together", 2100, 30, "Education", "📚", isJoined = true),
        )
    }

    val discover = listOf(
        Community("5", "Python Developers", "Learn and build with Python", 4500, 18, "Tech", "🐍"),
        Community("6", "UI/UX Designers", "Design beautiful interfaces", 2300, 7, "Design", "🎨"),
        Community("7", "Math Excellence", "Master mathematics together", 1800, 15, "Education", "📐"),
        Community("8", "Science Club", "Explore the wonders of science", 3100, 22, "Science", "🔬"),
        Community("9", "Writers' Corner", "For creative minds", 890, 5, "Creative", "✍️"),
        Community("10", "Career & Jobs", "Opportunities and career tips", 5200, 33, "Career", "💼"),
    )

    if (showCreateGroup) {
        CreateGroupDialog(onDismiss = { showCreateGroup = false }, onCreate = { name, desc ->
            myCommunities.add(Community(System.currentTimeMillis().toString(), name, desc, 1, 0, "General", "👥", isJoined = true))
            showCreateGroup = false
        })
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Communities", fontWeight = FontWeight.Bold, color = TextPrimary) },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Search, null, tint = TextSecondary) }
                    IconButton(onClick = { showCreateGroup = true }) { Icon(Icons.Default.Add, null, tint = ElectricPurple) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTab, containerColor = DarkBackground, contentColor = ElectricPurple,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size)
                        Box(modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]).height(2.dp).background(ElectricPurple))
                }
            ) {
                listOf("Your Communities", "Discover").forEachIndexed { index, tab ->
                    Tab(selected = selectedTab == index, onClick = { selectedTab = index },
                        text = { Text(tab, color = if (selectedTab == index) ElectricPurple else TextMuted, fontSize = 14.sp) })
                }
            }
            HorizontalDivider(color = DividerColor, thickness = 0.5.dp)

            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (selectedTab == 0) {
                    if (myCommunities.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("👥", fontSize = 48.sp)
                                    Text("You haven't joined any groups yet", color = TextMuted, fontSize = 15.sp)
                                    Button(onClick = { selectedTab = 1 }, colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple), shape = RoundedCornerShape(20.dp)) {
                                        Text("Discover Groups")
                                    }
                                }
                            }
                        }
                    } else {
                        items(myCommunities, key = { it.id }) { community ->
                            CommunityCard(community = community, showJoin = false, onCommunityClick = { onCommunityClick(community) }, onJoin = {})
                        }
                    }
                } else {
                    item {
                        OutlinedTextField(
                            value = searchQuery, onValueChange = { searchQuery = it },
                            placeholder = { Text("Search communities...", color = TextMuted, fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, null, tint = TextMuted, modifier = Modifier.size(18.dp)) },
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricPurple, unfocusedBorderColor = DividerColor, focusedContainerColor = DarkCard, unfocusedContainerColor = DarkCard, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = ElectricPurple),
                            singleLine = true
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                    val filtered = discover.filter { searchQuery.isEmpty() || it.name.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true) }
                    items(filtered, key = { it.id }) { community ->
                        var joined by remember { mutableStateOf(myCommunities.any { it.id == community.id }) }
                        CommunityCard(community = community, showJoin = true, isJoined = joined,
                            onCommunityClick = { onCommunityClick(community) },
                            onJoin = { joined = !joined; if (joined) myCommunities.add(community.copy(isJoined = true)) else myCommunities.removeIf { it.id == community.id } })
                    }
                }
            }
        }
    }
}

@Composable
fun CommunityCard(community: Community, showJoin: Boolean, isJoined: Boolean = true, onCommunityClick: () -> Unit, onJoin: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onCommunityClick),
        colors = CardDefaults.cardColors(containerColor = DarkCard), shape = RoundedCornerShape(14.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(Brush.linearGradient(listOf(ElectricPurple.copy(alpha = 0.3f), AccentBlue.copy(alpha = 0.3f)))), contentAlignment = Alignment.Center) {
                Text(community.emoji, fontSize = 26.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(community.name, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    if (community.isPrivate) {
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.Lock, null, tint = TextMuted, modifier = Modifier.size(12.dp))
                    }
                }
                Text(community.description, color = TextMuted, fontSize = 12.sp, maxLines = 1)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("${formatMemberCount(community.members)} members", color = TextSecondary, fontSize = 11.sp)
                    if (community.newPosts > 0)
                        Text("${community.newPosts} new posts", color = AccentGreen, fontSize = 11.sp)
                }
            }
            if (showJoin) {
                Spacer(Modifier.width(8.dp))
                Button(onClick = onJoin,
                    colors = ButtonDefaults.buttonColors(containerColor = if (isJoined) DarkBackground else ElectricPurple),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(32.dp),
                    border = if (isJoined) BorderStroke(1.dp, DividerColor) else null) {
                    Text(if (isJoined) "Joined" else "Join", color = TextPrimary, fontSize = 12.sp)
                }
            } else {
                Icon(Icons.Default.ChevronRight, null, tint = TextMuted)
            }
        }
    }
}

fun formatMemberCount(count: Int): String = if (count >= 1000) "${"%.1f".format(count / 1000.0)}K" else count.toString()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(community: Community, onBack: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showPostDialog by remember { mutableStateOf(false) }
    val posts = remember {
        mutableStateListOf(
            GroupPost("1", "Brian Otieno", "What programming language are you currently learning?", 24, 36, "2h"),
            GroupPost("2", "Mary Wanjiku", "Just built a To-Do app with React! Check it out 🚀", 18, 12, "3h"),
            GroupPost("3", "Teacher Alex", "New resources for CS students posted in Topics tab! 📚", 45, 8, "5h"),
        )
    }
    val discussions = listOf(
        Discussion("1", "How do I start with open source?", 12, "1h ago", isHot = true),
        Discussion("2", "Best resources for learning Python?", 8, "4h ago", isHot = true),
        Discussion("3", "Share your project idea", 22, "5h ago"),
        Discussion("4", "Laptop recommendations for developers?", 14, "6h ago"),
    )
    val tabs = listOf("Posts", "Topics", "Members", "Media")

    if (showPostDialog) {
        CreateGroupPostDialog(communityName = community.name, onDismiss = { showPostDialog = false },
            onCreate = { content ->
                posts.add(0, GroupPost(System.currentTimeMillis().toString(), "You", content, 0, 0, "Just now"))
                showPostDialog = false
            })
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                title = { Text(community.name, fontWeight = FontWeight.Bold, color = TextPrimary) },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Search, null, tint = TextSecondary) }
                    IconButton(onClick = {}) { Icon(Icons.Default.Notifications, null, tint = TextSecondary) }
                    IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, null, tint = TextSecondary) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showPostDialog = true }, containerColor = ElectricPurple, shape = CircleShape) {
                Icon(Icons.Default.Add, null, tint = Color.White)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Header banner
            Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(Brush.linearGradient(listOf(ElectricPurple, AccentBlue))), contentAlignment = Alignment.Center) {
                Text(community.emoji, fontSize = 40.sp)
            }
            // Community info
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(community.name, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 18.sp)
                        Text(community.description, color = TextSecondary, fontSize = 13.sp)
                    }
                    if (!community.isPrivate) {
                        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple), shape = RoundedCornerShape(20.dp), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                            Text("+ Invite", fontSize = 13.sp)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("${formatMemberCount(community.members)} members", color = TextSecondary, fontSize = 13.sp)
                    Text("${community.newPosts} new", color = AccentGreen, fontSize = 13.sp)
                }
            }
            HorizontalDivider(color = DividerColor)

            TabRow(selectedTabIndex = selectedTab, containerColor = DarkBackground, contentColor = ElectricPurple,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) Box(modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]).height(2.dp).background(ElectricPurple))
                }) {
                tabs.forEachIndexed { index, tab ->
                    Tab(selected = selectedTab == index, onClick = { selectedTab = index },
                        text = { Text(tab, color = if (selectedTab == index) ElectricPurple else TextMuted, fontSize = 13.sp) })
                }
            }
            HorizontalDivider(color = DividerColor, thickness = 0.5.dp)

            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                when (selectedTab) {
                    0 -> { // Posts
                        items(posts, key = { it.id }) { post ->
                            GroupPostCard(post = post)
                        }
                    }
                    1 -> { // Discussions
                        item {
                            Button(onClick = {}, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple), shape = RoundedCornerShape(12.dp)) {
                                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Start a Discussion")
                            }
                        }
                        items(discussions, key = { it.id }) { disc ->
                            Card(modifier = Modifier.fillMaxWidth().clickable {}, colors = CardDefaults.cardColors(containerColor = DarkCard), shape = RoundedCornerShape(12.dp)) {
                                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(ElectricPurple.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Forum, null, tint = ElectricPurple, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(disc.title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                                            if (disc.isHot) Box(modifier = Modifier.background(AccentRed.copy(alpha = 0.15f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                                Text("🔥 Hot", color = AccentRed, fontSize = 10.sp)
                                            }
                                        }
                                        Text("${disc.replies} replies · ${disc.time}", color = TextMuted, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                    2 -> { // Members
                        items(5) { index ->
                            val names = listOf("Brian Otieno", "Mary Wanjiku", "Teacher Alex", "Joyce Maina", "John Kamau")
                            val roles = listOf("Admin", "Member", "Admin", "Member", "Member")
                            Row(modifier = Modifier.fillMaxWidth().clickable {}.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(ElectricPurple), contentAlignment = Alignment.Center) {
                                    Text(names[index].first().toString(), color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(names[index], color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    Text("@${names[index].lowercase().replace(" ", "_")}", color = TextMuted, fontSize = 12.sp)
                                }
                                Box(modifier = Modifier.background(if (roles[index] == "Admin") ElectricPurple.copy(alpha = 0.2f) else DarkCard, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                    Text(roles[index], color = if (roles[index] == "Admin") ElectricPurple else TextMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                    3 -> { // Media
                        item {
                            Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("📷", fontSize = 40.sp)
                                    Text("No media shared yet", color = TextMuted)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GroupPostCard(post: GroupPost) {
    var liked by remember { mutableStateOf(false) }
    var likeCount by remember { mutableIntStateOf(post.likes) }
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkCard), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(38.dp).clip(CircleShape).background(ElectricPurple), contentAlignment = Alignment.Center) {
                    Text(post.author.first().toString(), color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(post.author, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(post.time, color = TextMuted, fontSize = 12.sp)
                }
                IconButton(onClick = {}, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.MoreVert, null, tint = TextMuted, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(post.content, color = TextPrimary, fontSize = 14.sp)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { liked = !liked; likeCount = if (liked) likeCount + 1 else likeCount - 1 }) {
                    Icon(if (liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, null, tint = if (liked) AccentRed else TextMuted, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(likeCount.toString(), color = TextMuted, fontSize = 13.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {}) {
                    Icon(Icons.Outlined.ChatBubbleOutline, null, tint = TextMuted, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(post.replies.toString(), color = TextMuted, fontSize = 13.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {}) {
                    Icon(Icons.Outlined.Share, null, tint = TextMuted, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun CreateGroupDialog(onDismiss: () -> Unit, onCreate: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isPrivate by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss, containerColor = DarkCard,
        title = { Text("Create Group", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, placeholder = { Text("Group name", color = TextMuted) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricPurple, unfocusedBorderColor = DividerColor, focusedContainerColor = DarkBackground, unfocusedContainerColor = DarkBackground, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = ElectricPurple))
                OutlinedTextField(value = description, onValueChange = { description = it }, placeholder = { Text("Description (optional)", color = TextMuted) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), maxLines = 3, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricPurple, unfocusedBorderColor = DividerColor, focusedContainerColor = DarkBackground, unfocusedContainerColor = DarkBackground, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = ElectricPurple))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("Private group", color = TextPrimary, modifier = Modifier.weight(1f))
                    Switch(checked = isPrivate, onCheckedChange = { isPrivate = it }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ElectricPurple))
                }
            }
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank()) onCreate(name, description) }, colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple), enabled = name.isNotBlank()) {
                Text("Create", color = Color.White)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) } }
    )
}

@Composable
fun CreateGroupPostDialog(communityName: String, onDismiss: () -> Unit, onCreate: (String) -> Unit) {
    var content by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss, containerColor = DarkCard,
        title = { Text("Post to $communityName", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(value = content, onValueChange = { content = it },
                placeholder = { Text("Share something with the group...", color = TextMuted) },
                modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricPurple, unfocusedBorderColor = DividerColor, focusedContainerColor = DarkBackground, unfocusedContainerColor = DarkBackground, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = ElectricPurple),
                maxLines = 6)
        },
        confirmButton = {
            Button(onClick = { if (content.isNotBlank()) onCreate(content) }, colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple), enabled = content.isNotBlank()) {
                Text("Post", color = Color.White)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) } }
    )
}
