package com.elimusocial.app.ui.screens.profile

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
import coil.compose.AsyncImage
import com.elimusocial.app.data.repository.FirestorePost
import com.elimusocial.app.data.repository.FirestoreUser
import com.elimusocial.app.ui.screens.components.FirebasePostCard
import com.elimusocial.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userProfile: FirestoreUser?,
    posts: List<FirestorePost>,
    isOwnProfile: Boolean = true,
    onNavigate: (String) -> Unit = {},
    onBack: (() -> Unit)? = null,
    onAvatarUpload: ((ByteArray) -> Unit)? = null,
    onCoverUpload: ((ByteArray) -> Unit)? = null,
    onSaveProfile: ((String, String, String) -> Unit)? = null
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Posts", "Replies", "Media", "Likes")
    var isFollowing by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val avatarLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { context.contentResolver.openInputStream(it)?.readBytes()?.let { bytes -> onAvatarUpload?.invoke(bytes) } }
    }
    val coverLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { context.contentResolver.openInputStream(it)?.readBytes()?.let { bytes -> onCoverUpload?.invoke(bytes) } }
    }

    if (showEditDialog) {
        EditProfileDialog(
            userProfile = userProfile,
            onDismiss = { showEditDialog = false },
            onSave = { name, bio, location ->
                onSaveProfile?.invoke(name, bio, location)
                showEditDialog = false
            }
        )
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            // Cover photo
            Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                if (!userProfile?.coverUrl.isNullOrEmpty()) {
                    AsyncImage(model = userProfile!!.coverUrl, contentDescription = null,
                        modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Box(modifier = Modifier.fillMaxSize()
                        .background(Brush.linearGradient(listOf(ElectricPurple, AccentBlue))))
                }
                if (isOwnProfile) {
                    IconButton(
                        onClick = { coverLauncher.launch("image/*") },
                        modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
                onBack?.let {
                    IconButton(
                        onClick = it,
                        modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                }
            }

            // Avatar + action buttons
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).offset(y = (-32).dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Avatar with camera button
                Box(modifier = Modifier.size(84.dp)) {
                    Box(
                        modifier = Modifier.size(84.dp).clip(CircleShape)
                            .background(DarkBackground).border(3.dp, DarkBackground, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!userProfile?.avatarUrl.isNullOrEmpty()) {
                            AsyncImage(model = userProfile!!.avatarUrl, contentDescription = null,
                                modifier = Modifier.size(78.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                        } else {
                            Box(modifier = Modifier.size(78.dp).clip(CircleShape).background(ElectricPurple),
                                contentAlignment = Alignment.Center) {
                                Text((userProfile?.name?.firstOrNull() ?: "A").toString(),
                                    color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 30.sp)
                            }
                        }
                    }
                    if (isOwnProfile) {
                        IconButton(
                            onClick = { avatarLauncher.launch("image/*") },
                            modifier = Modifier.size(26.dp).align(Alignment.BottomEnd)
                                .background(ElectricPurple, CircleShape).border(2.dp, DarkBackground, CircleShape)
                        ) {
                            Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                // Action buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (isOwnProfile) {
                        OutlinedButton(
                            onClick = { showEditDialog = true },
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, DividerColor),
                            modifier = Modifier.height(36.dp)
                        ) { Text("Edit profile", color = TextPrimary, fontSize = 13.sp) }
                        OutlinedButton(
                            onClick = { onNavigate("settings") },
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, DividerColor),
                            modifier = Modifier.height(36.dp).width(40.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) { Icon(Icons.Default.Settings, null, tint = TextPrimary, modifier = Modifier.size(18.dp)) }
                    } else {
                        Button(
                            onClick = { isFollowing = !isFollowing },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isFollowing) DarkCard else ElectricPurple),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.height(36.dp)
                        ) { Text(if (isFollowing) "Following" else "Follow", color = TextPrimary, fontSize = 13.sp) }
                        OutlinedButton(
                            onClick = { onNavigate("messages") },
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, DividerColor),
                            modifier = Modifier.height(36.dp)
                        ) { Text("Message", color = TextPrimary, fontSize = 13.sp) }
                    }
                }
            }

            // Profile info
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).offset(y = (-24).dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(userProfile?.name ?: "User", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 22.sp)
                    if (userProfile?.isVerified == true) {
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Filled.Verified, null, tint = ElectricPurple, modifier = Modifier.size(20.dp))
                    }
                }
                Text(userProfile?.username ?: "@user", color = TextMuted, fontSize = 14.sp)
                Text(when (userProfile?.role) {
                    "teacher" -> "👨‍🏫 Teacher"
                    "admin" -> "⚡ Admin"
                    else -> "🎓 Student"
                }, color = LightPurple, fontSize = 12.sp)

                if (!userProfile?.bio.isNullOrEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    Text(userProfile!!.bio, color = TextPrimary, fontSize = 14.sp, lineHeight = 20.sp)
                }

                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (!userProfile?.location.isNullOrEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.LocationOn, null, tint = TextMuted, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(3.dp))
                            Text(userProfile!!.location, color = TextMuted, fontSize = 13.sp)
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.CalendarMonth, null, tint = TextMuted, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(3.dp))
                        Text("Joined March 2023", color = TextMuted, fontSize = 13.sp)
                    }
                }

                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    ProfileStat("${userProfile?.posts ?: 0}", "Posts")
                    ProfileStat(formatCount(userProfile?.followers ?: 0), "Followers") { onNavigate("followers") }
                    ProfileStat("${userProfile?.following ?: 0}", "Following") { onNavigate("following") }
                }
            }

            // Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkBackground,
                contentColor = ElectricPurple,
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size)
                        Box(modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]).height(2.dp).background(ElectricPurple))
                }
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(selected = selectedTab == index, onClick = { selectedTab = index },
                        text = { Text(tab, color = if (selectedTab == index) ElectricPurple else TextMuted, fontSize = 14.sp) })
                }
            }
            HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
        }

        when (selectedTab) {
            0 -> { // Posts
                if (posts.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📝", fontSize = 40.sp)
                                Spacer(Modifier.height(8.dp))
                                Text("No posts yet", color = TextMuted)
                            }
                        }
                    }
                } else {
                    items(posts) { post -> FirebasePostCard(post = post, isLiked = false, onLike = {}) }
                }
            }
            1 -> { // Replies
                item {
                    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Text("No replies yet", color = TextMuted)
                    }
                }
            }
            2 -> { // Media grid
                item {
                    if (posts.filter { it.imageUrl.isNotEmpty() }.isEmpty()) {
                        Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📷", fontSize = 40.sp)
                                Spacer(Modifier.height(8.dp))
                                Text("No media yet", color = TextMuted)
                            }
                        }
                    } else {
                        androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                            columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(3),
                            modifier = Modifier.fillMaxWidth().heightIn(max = 600.dp),
                            contentPadding = PaddingValues(1.dp)
                        ) {
                            items(posts.filter { it.imageUrl.isNotEmpty() }.size) { idx ->
                                val post = posts.filter { it.imageUrl.isNotEmpty() }[idx]
                                AsyncImage(model = post.imageUrl, contentDescription = null,
                                    modifier = Modifier.aspectRatio(1f).padding(1.dp), contentScale = ContentScale.Crop)
                            }
                        }
                    }
                }
            }
            3 -> { // Likes
                item {
                    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Text("Liked posts appear here", color = TextMuted)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileStat(count: String, label: String, onClick: (() -> Unit)? = null) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    ) {
        Text(count, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 18.sp)
        Text(label, color = TextMuted, fontSize = 12.sp)
    }
}

fun formatCount(count: Int): String = when {
    count >= 1_000_000 -> "${count / 1_000_000}M"
    count >= 1_000 -> "${"%.1f".format(count / 1_000.0)}K"
    else -> count.toString()
}

// Edit Profile Dialog
@Composable
fun EditProfileDialog(
    userProfile: FirestoreUser?,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(userProfile?.name ?: "") }
    var bio by remember { mutableStateOf(userProfile?.bio ?: "") }
    var location by remember { mutableStateOf(userProfile?.location ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Edit Profile", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null, tint = TextMuted) }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ProfileEditField(label = "Name", value = name, onValueChange = { name = it })
                ProfileEditField(label = "Bio", value = bio, onValueChange = { bio = it }, maxLines = 3)
                ProfileEditField(label = "Location", value = location, onValueChange = { location = it })
            }
        },
        confirmButton = {
            Button(onClick = { onSave(name, bio, location) },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                shape = RoundedCornerShape(12.dp)) {
                Text("Save", color = TextPrimary, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Composable
fun ProfileEditField(label: String, value: String, onValueChange: (String) -> Unit, maxLines: Int = 1) {
    Column {
        Text(label, color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value, onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            maxLines = maxLines,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricPurple, unfocusedBorderColor = DividerColor,
                focusedContainerColor = DarkBackground, unfocusedContainerColor = DarkBackground,
                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = ElectricPurple
            )
        )
    }
}
