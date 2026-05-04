package com.elimusocial.app.ui.screens.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.elimusocial.app.data.repository.FirestorePost
import com.elimusocial.app.data.repository.FirestoreUser
import com.elimusocial.app.ui.screens.components.FirebasePostCard
import com.elimusocial.app.ui.screens.social.StoriesRow
import com.elimusocial.app.ui.theme.*
import com.elimusocial.app.ui.viewmodels.AuthViewModel
import com.elimusocial.app.ui.viewmodels.FeedViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onNavigate: (String) -> Unit = {},
    authViewModel: AuthViewModel,
    feedViewModel: FeedViewModel = viewModel()
) {
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val feedState by feedViewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf("feed") }
    var selectedFeedTab by remember { mutableStateOf(0) }
    val feedTabs = listOf("For You", "Following", "Groups")

    Scaffold(
        containerColor = DarkBackground,
        topBar = { HomeTopBar(onLogout = onLogout, onNavigate = onNavigate) },
        bottomBar = {
            BottomNavBarHome(
                selectedTab = selectedTab,
                onTabSelected = { route ->
                    when (route) {
                        "messages" -> onNavigate("messages")
                        "ai" -> onNavigate("elimu_ai")
                        else -> selectedTab = route
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
                posts = feedState.posts,
                isLoading = feedState.isLoading,
                likedPostIds = feedState.likedPostIds,
                onLike = { feedViewModel.toggleLike(it) },
                userProfile = authState.userProfile,
                isCreatingPost = feedState.isCreatingPost,
                onCreatePost = { content, imageBytes ->
                    val profile = authState.userProfile ?: return@FeedContent
                    if (imageBytes != null) {
                        feedViewModel.createPostWithImage(content, imageBytes, profile)
                    } else {
                        feedViewModel.createPost(content, profile)
                    }
                },
                onNavigate = onNavigate
            )
            "explore" -> ExploreContent(paddingValues)
            "notifications" -> NotificationsContent(paddingValues)
            "profile" -> ProfileContent(
                paddingValues = paddingValues,
                userProfile = authState.userProfile,
                posts = feedState.posts.filter { it.authorId == authState.currentUser?.uid },
                onNavigate = onNavigate
            )
            else -> FeedContent(
                paddingValues = paddingValues,
                selectedFeedTab = selectedFeedTab,
                feedTabs = feedTabs,
                onTabSelected = { selectedFeedTab = it },
                posts = feedState.posts,
                isLoading = feedState.isLoading,
                likedPostIds = feedState.likedPostIds,
                onLike = { feedViewModel.toggleLike(it) },
                userProfile = authState.userProfile,
                isCreatingPost = feedState.isCreatingPost,
                onCreatePost = { content, imageBytes ->
                    val profile = authState.userProfile ?: return@FeedContent
                    if (imageBytes != null) {
                        feedViewModel.createPostWithImage(content, imageBytes, profile)
                    } else {
                        feedViewModel.createPost(content, profile)
                    }
                },
                onNavigate = onNavigate
            )
        }
    }
}

@Composable
fun BottomNavBarHome(selectedTab: String, onTabSelected: (String) -> Unit) {
    NavigationBar(containerColor = DarkCard, tonalElevation = 0.dp) {
        val items = listOf(
            Triple("feed", Icons.Outlined.Home, Icons.Filled.Home),
            Triple("explore", Icons.Outlined.Search, Icons.Filled.Search),
            Triple("ai", Icons.Outlined.AutoAwesome, Icons.Filled.AutoAwesome),
            Triple("notifications", Icons.Outlined.Notifications, Icons.Filled.Notifications),
            Triple("profile", Icons.Outlined.Person, Icons.Filled.Person)
        )
        items.forEach { (route, outlinedIcon, filledIcon) ->
            NavigationBarItem(
                selected = selectedTab == route,
                onClick = { onTabSelected(route) },
                icon = {
                    Icon(
                        if (selectedTab == route) filledIcon else outlinedIcon,
                        contentDescription = route,
                        modifier = Modifier.size(24.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ElectricPurple,
                    unselectedIconColor = TextMuted,
                    indicatorColor = DarkBackground
                )
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
                        .background(Brush.linearGradient(listOf(ElectricPurple, AccentBlue)), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) { Text("⚡", fontSize = 16.sp) }
                Spacer(modifier = Modifier.width(10.dp))
                Text("Elimu Social", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 18.sp)
            }
        },
        actions = {
            IconButton(onClick = { onNavigate("reels") }) {
                Icon(Icons.Default.VideoLibrary, contentDescription = "Reels", tint = TextSecondary)
            }
            IconButton(onClick = { onNavigate("messages") }) {
                Icon(Icons.Default.Mail, contentDescription = "Messages", tint = TextSecondary)
            }
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More", tint = TextSecondary)
                }
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }, modifier = Modifier.background(DarkCard)) {
                    DropdownMenuItem(text = { Text("Elimu AI", color = LightPurple) }, onClick = { menuExpanded = false; onNavigate("elimu_ai") }, leadingIcon = { Text("🤖") })
                    DropdownMenuItem(text = { Text("Study Planner", color = TextPrimary) }, onClick = { menuExpanded = false; onNavigate("study_planner") }, leadingIcon = { Icon(Icons.Default.CalendarMonth, null, tint = AccentGreen) })
                    DropdownMenuItem(text = { Text("Communities", color = TextPrimary) }, onClick = { menuExpanded = false; onNavigate("communities") }, leadingIcon = { Icon(Icons.Default.Group, null, tint = AccentBlue) })
                    DropdownMenuItem(text = { Text("Events", color = TextPrimary) }, onClick = { menuExpanded = false; onNavigate("events") }, leadingIcon = { Icon(Icons.Default.Event, null, tint = AccentOrange) })
                    DropdownMenuItem(text = { Text("Dashboard", color = TextPrimary) }, onClick = { menuExpanded = false; onNavigate("creator_dashboard") }, leadingIcon = { Icon(Icons.Default.BarChart, null, tint = ElectricPurple) })
                    HorizontalDivider(color = DividerColor)
                    DropdownMenuItem(text = { Text("Settings", color = TextPrimary) }, onClick = { menuExpanded = false; onNavigate("settings") }, leadingIcon = { Icon(Icons.Outlined.Settings, null, tint = TextSecondary) })
                    DropdownMenuItem(text = { Text("Log out", color = AccentRed) }, onClick = { menuExpanded = false; onLogout() }, leadingIcon = { Icon(Icons.Outlined.Logout, null, tint = AccentRed) })
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
    posts: List<FirestorePost>,
    isLoading: Boolean,
    likedPostIds: Set<String>,
    onLike: (String) -> Unit,
    userProfile: FirestoreUser?,
    isCreatingPost: Boolean,
    onCreatePost: (String, ByteArray?) -> Unit,
    onNavigate: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item { StoriesRow() }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                feedTabs.forEachIndexed { index, tab ->
                    FilterChip(
                        selected = selectedFeedTab == index,
                        onClick = { onTabSelected(index) },
                        label = { Text(tab, fontSize = 13.sp, fontWeight = if (selectedFeedTab == index) FontWeight.SemiBold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricPurple, selectedLabelColor = TextPrimary, containerColor = DarkCard, labelColor = TextSecondary),
                        border = FilterChipDefaults.filterChipBorder(enabled = true, selected = selectedFeedTab == index, borderColor = DividerColor, selectedBorderColor = ElectricPurple)
                    )
                }
            }
        }

        item {
            CreatePostInput(
                userProfile = userProfile,
                isCreating = isCreatingPost,
                onPost = onCreatePost
            )
        }

        if (isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ElectricPurple)
                }
            }
        } else if (posts.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✨", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No posts yet", color = TextMuted, fontSize = 16.sp)
                        Text("Be the first to post!", color = TextMuted, fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(posts, key = { it.id }) { post ->
                FirebasePostCard(
                    post = post,
                    isLiked = likedPostIds.contains(post.id),
                    onLike = { onLike(post.id) }
                )
            }
        }
    }
}

@Composable
fun CreatePostInput(
    userProfile: FirestoreUser?,
    isCreating: Boolean,
    onPost: (String, ByteArray?) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(ElectricPurple),
            contentAlignment = Alignment.Center
        ) {
            if (!userProfile?.avatarUrl.isNullOrEmpty()) {
                AsyncImage(model = userProfile!!.avatarUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                Text(
                    text = (userProfile?.name?.firstOrNull() ?: "A").toString(),
                    color = TextPrimary, fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier.weight(1f).height(42.dp).clip(RoundedCornerShape(22.dp))
                .background(DarkCard).border(1.dp, DividerColor, RoundedCornerShape(22.dp))
                .clickable { showDialog = true }.padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text("What's on your mind?", color = TextMuted, fontSize = 14.sp)
        }
    }
    HorizontalDivider(color = DividerColor, thickness = 0.5.dp)

    if (showDialog) {
        CreatePostDialog(
            userProfile = userProfile,
            isCreating = isCreating,
            onDismiss = { showDialog = false },
            onPost = { content, imageBytes ->
                onPost(content, imageBytes)
                showDialog = false
            }
        )
    }
}

@Composable
fun CreatePostDialog(
    userProfile: FirestoreUser?,
    isCreating: Boolean,
    onDismiss: () -> Unit,
    onPost: (String, ByteArray?) -> Unit
) {
    var content by remember { mutableStateOf("") }
    var selectedImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val bytes = context.contentResolver.openInputStream(it)?.readBytes()
            selectedImageBytes = bytes
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Create Post", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, null, tint = TextMuted)
                }
            }
        },
        text = {
            Column {
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(ElectricPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Text((userProfile?.name?.firstOrNull() ?: "A").toString(), color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(userProfile?.name ?: "User", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        Text(userProfile?.username ?: "", color = TextMuted, fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = { Text("What's on your mind?", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricPurple,
                        unfocusedBorderColor = DividerColor,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = ElectricPurple
                    ),
                    maxLines = 6
                )
                if (selectedImageBytes != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Image, null, tint = AccentGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Image selected", color = AccentGreen, fontSize = 13.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { selectedImageBytes = null }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, null, tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    IconButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                        Icon(Icons.Outlined.Image, null, tint = AccentBlue)
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.EmojiEmotions, null, tint = AccentOrange)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (content.isNotBlank()) onPost(content, selectedImageBytes) },
                enabled = content.isNotBlank() && !isCreating,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                shape = RoundedCornerShape(20.dp)
            ) {
                if (isCreating) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = TextPrimary, strokeWidth = 2.dp)
                } else {
                    Text("Post", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    )
}

// ===== EXPLORE =====
@Composable
fun ExploreContent(paddingValues: PaddingValues) {
    val trending = listOf(
        Triple("New policy on student loans", "Trending now", "2.5K posts"),
        Triple("Elimu Hackathon 2024", "Trending in Tech", "1.2K posts"),
        Triple("Campus life moments ✨", "Trending in Campus", "987 posts")
    )
    LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text("Explore", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(14.dp))
                    .background(DarkCard).border(1.dp, DividerColor, RoundedCornerShape(14.dp)).padding(horizontal = 14.dp),
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
        items(trending) { (title, tag, count) ->
            TrendingItem(title = title, tag = tag, count = count)
            HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
        }
    }
}

@Composable
fun TrendingItem(title: String, tag: String, count: String) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable {}.padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(tag, color = TextMuted, fontSize = 12.sp)
            Text(title, color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Text(count, color = TextMuted, fontSize = 12.sp)
        }
        Icon(Icons.Default.MoreVert, null, tint = TextMuted, modifier = Modifier.size(18.dp))
    }
}

// ===== NOTIFICATIONS =====
@Composable
fun NotificationsContent(paddingValues: PaddingValues) {
    Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🔔", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Notifications", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("You're all caught up!", color = TextMuted, fontSize = 14.sp)
        }
    }
}

// ===== PROFILE =====
@Composable
fun ProfileContent(
    paddingValues: PaddingValues,
    userProfile: FirestoreUser?,
    posts: List<FirestorePost>,
    onNavigate: (String) -> Unit = {}
) {
    var selectedProfileTab by remember { mutableStateOf(0) }
    val profileTabs = listOf("Posts", "Replies", "Media", "Likes")

    LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        item {
            Box(modifier = Modifier.fillMaxWidth().height(140.dp).background(Brush.linearGradient(listOf(ElectricPurple, AccentBlue))))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).offset(y = (-24).dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Box(
                    modifier = Modifier.size(80.dp).clip(CircleShape).background(DarkBackground).border(3.dp, DarkBackground, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (!userProfile?.avatarUrl.isNullOrEmpty()) {
                        AsyncImage(model = userProfile!!.avatarUrl, contentDescription = null, modifier = Modifier.size(74.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                    } else {
                        Box(modifier = Modifier.size(74.dp).clip(CircleShape).background(ElectricPurple), contentAlignment = Alignment.Center) {
                            Text((userProfile?.name?.firstOrNull() ?: "A").toString(), color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                        }
                    }
                }
                OutlinedButton(onClick = { onNavigate("settings") }, shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, DividerColor), modifier = Modifier.height(36.dp)) {
                    Text("Edit profile", color = TextPrimary, fontSize = 13.sp)
                }
            }

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).offset(y = (-16).dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(userProfile?.name ?: "User", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 20.sp)
                    if (userProfile?.isVerified == true) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Filled.Verified, null, tint = ElectricPurple, modifier = Modifier.size(18.dp))
                    }
                }
                Text(userProfile?.username ?: "", color = TextMuted, fontSize = 14.sp)
                if (!userProfile?.bio.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(userProfile!!.bio, color = TextPrimary, fontSize = 14.sp)
                }
                if (!userProfile?.location.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.LocationOn, null, tint = TextMuted, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(userProfile!!.location, color = TextMuted, fontSize = 13.sp)
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    StatItem(count = userProfile?.posts?.toString() ?: "0", label = "Posts")
                    StatItem(count = formatCount(userProfile?.followers ?: 0), label = "Followers")
                    StatItem(count = userProfile?.following?.toString() ?: "0", label = "Following")
                }
            }

            ScrollableTabRow(
                selectedTabIndex = selectedProfileTab,
                containerColor = DarkBackground,
                contentColor = ElectricPurple,
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    if (selectedProfileTab < tabPositions.size) {
                        Box(modifier = Modifier.tabIndicatorOffset(tabPositions[selectedProfileTab]).height(2.dp).background(ElectricPurple))
                    }
                }
            ) {
                profileTabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedProfileTab == index,
                        onClick = { selectedProfileTab = index },
                        text = { Text(tab, color = if (selectedProfileTab == index) ElectricPurple else TextMuted, fontSize = 14.sp) }
                    )
                }
            }
            HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
        }

        if (posts.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No posts yet", color = TextMuted)
                }
            }
        } else {
            items(posts) { post ->
                FirebasePostCard(post = post, isLiked = false, onLike = {})
            }
        }
    }
}

fun formatCount(count: Int): String = if (count >= 1000) "${count / 1000.0}K" else count.toString()

@Composable
fun StatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 18.sp)
        Text(label, color = TextMuted, fontSize = 12.sp)
    }
}
