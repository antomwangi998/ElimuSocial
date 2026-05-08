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
import androidx.compose.ui.graphics.Color
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
    var selectedFeedTab by remember { mutableIntStateOf(0) }
    var showCreatePost by remember { mutableStateOf(false) }
    val feedTabs = listOf("For You", "Following", "Groups")

    if (showCreatePost) {
        CreatePostDialog(
            userProfile = authState.userProfile,
            isCreating = feedState.isCreatingPost,
            onDismiss = { showCreatePost = false },
            onPost = { content, imageBytes ->
                val profile = authState.userProfile ?: return@CreatePostDialog
                if (imageBytes != null) feedViewModel.createPostWithImage(content, imageBytes, profile)
                else feedViewModel.createPost(content, profile)
                showCreatePost = false
            }
        )
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = { HomeTopBar(onLogout = onLogout, onNavigate = onNavigate) },
        bottomBar = {
            BottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { route ->
                    when (route) {
                        "create" -> showCreatePost = true
                        "search" -> onNavigate("search")
                        "notifications" -> onNavigate("notifications")
                        else -> selectedTab = route
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTab == "feed") {
                FloatingActionButton(
                    onClick = { showCreatePost = true },
                    containerColor = ElectricPurple,
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.Default.Add, "Create post", tint = Color.White, modifier = Modifier.size(28.dp))
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
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
                onCreatePost = { showCreatePost = true },
                onNavigate = onNavigate,
                onPostClick = { onNavigate("post_detail") }
            )
            "explore" -> ExploreContent(paddingValues, onNavigate)
            "notifications" -> com.elimusocial.app.ui.screens.notifications.NotificationsContent(paddingValues)
            "profile" -> com.elimusocial.app.ui.screens.profile.ProfileScreen(
                userProfile = authState.userProfile,
                posts = feedState.posts.filter { it.authorId == authState.currentUser?.uid },
                isOwnProfile = true,
                onNavigate = onNavigate,
                onAvatarUpload = { bytes -> authViewModel.uploadAvatar(bytes) },
                onCoverUpload = { bytes -> authViewModel.uploadCover(bytes) },
                onSaveProfile = { name, bio, location -> authViewModel.updateProfile(name, bio, location) }
            )
        }
    }
}

@Composable
fun BottomNavBar(selectedTab: String, onTabSelected: (String) -> Unit) {
    NavigationBar(containerColor = DarkCard, tonalElevation = 0.dp) {
        val items = listOf(
            Triple("feed", Icons.Outlined.Home, Icons.Filled.Home),
            Triple("search", Icons.Outlined.Search, Icons.Filled.Search),
            Triple("create", Icons.Outlined.AddCircleOutline, Icons.Filled.AddCircle),
            Triple("notifications", Icons.Outlined.Notifications, Icons.Filled.Notifications),
            Triple("profile", Icons.Outlined.Person, Icons.Filled.Person)
        )
        items.forEach { (route, outlined, filled) ->
            NavigationBarItem(
                selected = selectedTab == route,
                onClick = { onTabSelected(route) },
                icon = {
                    Icon(
                        if (selectedTab == route) filled else outlined,
                        contentDescription = route,
                        modifier = Modifier.size(if (route == "create") 30.dp else 24.dp),
                        tint = when {
                            route == "create" -> ElectricPurple
                            selectedTab == route -> ElectricPurple
                            else -> TextMuted
                        }
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
                    modifier = Modifier.size(32.dp).background(
                        Brush.linearGradient(listOf(ElectricPurple, AccentBlue)), RoundedCornerShape(8.dp)
                    ), contentAlignment = Alignment.Center
                ) { Text("⚡", fontSize = 16.sp) }
                Spacer(Modifier.width(10.dp))
                Text("Elimu Social", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 18.sp)
            }
        },
        actions = {
            IconButton(onClick = { onNavigate("reels") }) {
                Icon(Icons.Default.VideoLibrary, "Reels", tint = TextSecondary)
            }
            IconButton(onClick = { onNavigate("messages") }) {
                Icon(Icons.Default.Mail, "Messages", tint = TextSecondary)
            }
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, "More", tint = TextSecondary)
                }
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.background(DarkCard)) {
                    DropdownMenuItem(text = { Text("Elimu AI 🤖", color = LightPurple) },
                        onClick = { menuExpanded = false; onNavigate("elimu_ai") })
                    DropdownMenuItem(text = { Text("Study Planner", color = TextPrimary) },
                        onClick = { menuExpanded = false; onNavigate("study_planner") },
                        leadingIcon = { Icon(Icons.Default.CalendarMonth, null, tint = AccentGreen) })
                    DropdownMenuItem(text = { Text("Communities", color = TextPrimary) },
                        onClick = { menuExpanded = false; onNavigate("communities") },
                        leadingIcon = { Icon(Icons.Default.Group, null, tint = AccentBlue) })
                    DropdownMenuItem(text = { Text("Events", color = TextPrimary) },
                        onClick = { menuExpanded = false; onNavigate("events") },
                        leadingIcon = { Icon(Icons.Default.Event, null, tint = AccentOrange) })
                    DropdownMenuItem(text = { Text("Live Stream 🔴", color = TextPrimary) },
                        onClick = { menuExpanded = false; onNavigate("live_stream") })
                    DropdownMenuItem(text = { Text("Spaces 🎙️", color = TextPrimary) },
                        onClick = { menuExpanded = false; onNavigate("spaces") })
                    DropdownMenuItem(text = { Text("Polls", color = TextPrimary) },
                        onClick = { menuExpanded = false; onNavigate("polls") },
                        leadingIcon = { Icon(Icons.Default.BarChart, null, tint = ElectricPurple) })
                    DropdownMenuItem(text = { Text("Bookmarks", color = TextPrimary) },
                        onClick = { menuExpanded = false; onNavigate("bookmarks") },
                        leadingIcon = { Icon(Icons.Default.Bookmark, null, tint = AccentYellow) })
                    DropdownMenuItem(text = { Text("Analytics", color = TextPrimary) },
                        onClick = { menuExpanded = false; onNavigate("analytics") },
                        leadingIcon = { Icon(Icons.Default.BarChart, null, tint = AccentGreen) })
                    DropdownMenuItem(text = { Text("Creator Dashboard", color = TextPrimary) },
                        onClick = { menuExpanded = false; onNavigate("creator_dashboard") },
                        leadingIcon = { Icon(Icons.Default.Dashboard, null, tint = AccentBlue) })
                    HorizontalDivider(color = DividerColor)
                    DropdownMenuItem(text = { Text("Settings", color = TextPrimary) },
                        onClick = { menuExpanded = false; onNavigate("settings") },
                        leadingIcon = { Icon(Icons.Outlined.Settings, null, tint = TextSecondary) })
                    DropdownMenuItem(text = { Text("Log out", color = AccentRed) },
                        onClick = { menuExpanded = false; onLogout() },
                        leadingIcon = { Icon(Icons.Outlined.Logout, null, tint = AccentRed) })
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
    onCreatePost: () -> Unit,
    onNavigate: (String) -> Unit = {},
    onPostClick: (String) -> Unit = {}
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues),
        contentPadding = PaddingValues(bottom = 80.dp)) {
        item { StoriesRow() }
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                feedTabs.forEachIndexed { index, tab ->
                    FilterChip(
                        selected = selectedFeedTab == index,
                        onClick = { onTabSelected(index) },
                        label = { Text(tab, fontSize = 13.sp, fontWeight = if (selectedFeedTab == index) FontWeight.SemiBold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricPurple, selectedLabelColor = TextPrimary,
                            containerColor = DarkCard, labelColor = TextSecondary),
                        border = FilterChipDefaults.filterChipBorder(enabled = true, selected = selectedFeedTab == index,
                            borderColor = DividerColor, selectedBorderColor = ElectricPurple)
                    )
                }
            }
        }
        item { CreatePostBar(userProfile = userProfile, onClick = onCreatePost) }
        if (isLoading) {
            item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ElectricPurple)
                }
            }
        } else if (posts.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("✨", fontSize = 52.sp)
                        Text("No posts yet", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Text("Be the first to post!", color = TextMuted, fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = onCreatePost, colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                            shape = RoundedCornerShape(20.dp)) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Create Post")
                        }
                    }
                }
            }
        } else {
            items(posts, key = { it.id }) { post ->
                FirebasePostCard(post = post, isLiked = likedPostIds.contains(post.id),
                    onLike = { onLike(post.id) }, onPostClick = { onPostClick(post.id) })
            }
        }
    }
}

@Composable
fun CreatePostBar(userProfile: FirestoreUser?, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)
        .clickable(onClick = onClick), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(42.dp).clip(CircleShape).background(ElectricPurple),
            contentAlignment = Alignment.Center) {
            if (!userProfile?.avatarUrl.isNullOrEmpty()) {
                AsyncImage(model = userProfile!!.avatarUrl, contentDescription = null,
                    modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                Text((userProfile?.name?.firstOrNull() ?: "A").toString(),
                    color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.width(12.dp))
        Box(modifier = Modifier.weight(1f).height(42.dp).clip(RoundedCornerShape(22.dp))
            .background(DarkCard).border(1.dp, DividerColor, RoundedCornerShape(22.dp))
            .padding(horizontal = 16.dp), contentAlignment = Alignment.CenterStart) {
            Text("What's on your mind?", color = TextMuted, fontSize = 14.sp)
        }
        Spacer(Modifier.width(10.dp))
        IconButton(onClick = onClick, modifier = Modifier.size(42.dp).background(ElectricPurple.copy(alpha = 0.15f), CircleShape)) {
            Icon(Icons.Default.Image, null, tint = ElectricPurple, modifier = Modifier.size(20.dp))
        }
    }
    HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
}

@Composable
fun CreatePostDialog(
    userProfile: FirestoreUser?,
    isCreating: Boolean,
    onDismiss: () -> Unit,
    onPost: (String, ByteArray?) -> Unit
) {
    var content by remember { mutableStateOf("") }
    var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var selectedType by remember { mutableStateOf("post") }
    val context = LocalContext.current

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { imageBytes = context.contentResolver.openInputStream(it)?.readBytes() }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        modifier = Modifier.fillMaxWidth(),
        title = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Create Post", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Row {
                    TextButton(onClick = { if (content.isNotBlank()) onPost(content, imageBytes) },
                        enabled = content.isNotBlank() && !isCreating) {
                        if (isCreating) CircularProgressIndicator(Modifier.size(16.dp), color = ElectricPurple, strokeWidth = 2.dp)
                        else Text("Post", color = ElectricPurple, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null, tint = TextMuted) }
                }
            }
        },
        text = {
            Column {
                Row(verticalAlignment = Alignment.Top) {
                    Box(modifier = Modifier.size(42.dp).clip(CircleShape).background(ElectricPurple),
                        contentAlignment = Alignment.Center) {
                        Text((userProfile?.name?.firstOrNull() ?: "A").toString(),
                            color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(userProfile?.name ?: "User", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        Row {
                            listOf("post","educational","poll").forEach { type ->
                                FilterChip(selected = selectedType == type, onClick = { selectedType = type },
                                    label = { Text(type.replaceFirstChar { it.uppercase() }, fontSize = 10.sp) },
                                    modifier = Modifier.height(24.dp).padding(end = 4.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ElectricPurple, selectedLabelColor = TextPrimary,
                                        containerColor = DarkBackground, labelColor = TextMuted))
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = content, onValueChange = { content = it },
                    placeholder = { Text("What's happening?", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = ElectricPurple),
                    maxLines = 8
                )
                if (imageBytes != null) {
                    Card(colors = CardDefaults.cardColors(containerColor = DarkBackground), shape = RoundedCornerShape(8.dp)) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Image, null, tint = AccentGreen, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Image attached", color = AccentGreen, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            IconButton(onClick = { imageBytes = null }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Close, null, tint = TextMuted, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
                HorizontalDivider(color = DividerColor, modifier = Modifier.padding(vertical = 8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = { imageLauncher.launch("image/*") }) {
                        Icon(Icons.Outlined.Image, "Photo", tint = AccentBlue)
                    }
                    IconButton(onClick = {}) { Icon(Icons.Outlined.VideoCall, "Video", tint = AccentGreen) }
                    IconButton(onClick = {}) { Icon(Icons.Outlined.Poll, "Poll", tint = ElectricPurple) }
                    IconButton(onClick = {}) { Icon(Icons.Outlined.EmojiEmotions, "Emoji", tint = AccentOrange) }
                    IconButton(onClick = {}) { Icon(Icons.Outlined.LocationOn, "Location", tint = AccentRed) }
                    IconButton(onClick = {}) { Icon(Icons.Outlined.Tag, "Tag", tint = TextMuted) }
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
fun ExploreContent(paddingValues: PaddingValues, onNavigate: (String) -> Unit) {
    val trending = listOf(
        Triple("New policy on student loans", "Trending now", "2.5K posts"),
        Triple("Elimu Hackathon 2024", "Trending in Tech", "1.2K posts"),
        Triple("Campus life moments ✨", "Trending in Campus", "987 posts"),
        Triple("AI in Education", "Trending in Learning", "3.1K posts"),
        Triple("KCSE Results 2024", "Trending in Kenya", "8.7K posts"),
    )
    val suggested = listOf("Teacher Alex", "Code Club", "Study Together", "Brian Otieno")

    LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)) {
        item {
            Text("Explore", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(14.dp))
                .background(DarkCard).border(1.dp, DividerColor, RoundedCornerShape(14.dp))
                .clickable { onNavigate("search") }.padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Search, null, tint = TextMuted, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Text("Search posts, people, groups...", color = TextMuted, fontSize = 14.sp)
            }
            Spacer(Modifier.height(20.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("What's happening", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                TextButton(onClick = {}) { Text("See all", color = ElectricPurple, fontSize = 13.sp) }
            }
        }
        items(trending) { (title, tag, count) ->
            Row(modifier = Modifier.fillMaxWidth().clickable {}.padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(tag, color = TextMuted, fontSize = 12.sp)
                    Text(title, color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                    Text(count, color = TextMuted, fontSize = 12.sp)
                }
                IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, null, tint = TextMuted, modifier = Modifier.size(18.dp)) }
            }
            HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
        }
        item {
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Who to follow", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                TextButton(onClick = { onNavigate("search") }) { Text("View all", color = ElectricPurple, fontSize = 13.sp) }
            }
        }
        items(suggested) { name ->
            var following by remember { mutableStateOf(false) }
            Row(modifier = Modifier.fillMaxWidth().clickable {}.padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(ElectricPurple),
                    contentAlignment = Alignment.Center) {
                    Text(name.firstOrNull()?.toString() ?: "?", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(name, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text("@${name.lowercase().replace(" ", "_")}", color = TextMuted, fontSize = 12.sp)
                }
                Button(onClick = { following = !following },
                    colors = ButtonDefaults.buttonColors(containerColor = if (following) DarkCard else ElectricPurple),
                    shape = RoundedCornerShape(20.dp), contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(32.dp)) {
                    Text(if (following) "Following" else "Follow", fontSize = 12.sp, color = TextPrimary)
                }
            }
        }
    }
}
