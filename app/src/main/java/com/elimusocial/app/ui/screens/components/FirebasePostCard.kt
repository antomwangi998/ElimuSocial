package com.elimusocial.app.ui.screens.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.elimusocial.app.data.repository.FirestorePost
import com.elimusocial.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FirebasePostCard(
    post: FirestorePost,
    isLiked: Boolean,
    onLike: () -> Unit,
    onPostClick: ((String) -> Unit)? = null,
    onProfileClick: ((String) -> Unit)? = null
) {
    var liked by remember(isLiked) { mutableStateOf(isLiked) }
    var likeCount by remember(post.likes) { mutableIntStateOf(post.likes) }
    var reposted by remember { mutableStateOf(false) }
    var repostCount by remember(post.reposts) { mutableIntStateOf(post.reposts) }
    var bookmarked by remember { mutableStateOf(false) }
    var showShareSheet by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }

    if (showShareSheet) {
        ShareSheet(post = post, onDismiss = { showShareSheet = false })
    }

    Column(modifier = Modifier.fillMaxWidth().background(DarkBackground)
        .clickable { onPostClick?.invoke(post.id) }) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top) {

            // Avatar
            Box(modifier = Modifier.size(46.dp).clip(CircleShape).background(ElectricPurple)
                .clickable { onProfileClick?.invoke(post.authorId) },
                contentAlignment = Alignment.Center) {
                if (post.authorAvatarUrl.isNotEmpty()) {
                    AsyncImage(model = post.authorAvatarUrl, contentDescription = null,
                        modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Text(post.authorName.firstOrNull()?.toString() ?: "?",
                        color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Header
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(post.authorName, fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary, fontSize = 15.sp)
                                if (post.authorVerified) {
                                    Spacer(Modifier.width(4.dp))
                                    Icon(Icons.Filled.Verified, null, tint = ElectricPurple,
                                        modifier = Modifier.size(15.dp))
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(post.authorUsername, color = TextMuted, fontSize = 13.sp)
                                Text(" · ", color = TextMuted, fontSize = 13.sp)
                                Text(formatTimestamp(post.createdAt), color = TextMuted, fontSize = 13.sp)
                            }
                        }
                    }
                    Box {
                        IconButton(onClick = { showMoreMenu = true },
                            modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.MoreVert, null, tint = TextMuted, modifier = Modifier.size(18.dp))
                        }
                        DropdownMenu(expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false },
                            modifier = Modifier.background(DarkCard)) {
                            DropdownMenuItem(text = { Text("Copy link", color = TextPrimary) },
                                onClick = { showMoreMenu = false },
                                leadingIcon = { Icon(Icons.Default.Link, null, tint = TextSecondary) })
                            DropdownMenuItem(text = { Text("Share via...", color = TextPrimary) },
                                onClick = { showMoreMenu = false; showShareSheet = true },
                                leadingIcon = { Icon(Icons.Default.Share, null, tint = TextSecondary) })
                            DropdownMenuItem(text = { Text("Not interested", color = TextPrimary) },
                                onClick = { showMoreMenu = false },
                                leadingIcon = { Icon(Icons.Default.NotInterested, null, tint = TextSecondary) })
                            DropdownMenuItem(text = { Text("Report", color = AccentRed) },
                                onClick = { showMoreMenu = false },
                                leadingIcon = { Icon(Icons.Default.Flag, null, tint = AccentRed) })
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Post type badge
                if (post.type != "post") {
                    Box(modifier = Modifier.background(
                        when (post.type) {
                            "educational" -> AccentGreen.copy(alpha = 0.15f)
                            "reel" -> AccentBlue.copy(alpha = 0.15f)
                            "poll" -> ElectricPurple.copy(alpha = 0.15f)
                            else -> DarkCard
                        }, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(post.type.replaceFirstChar { it.uppercase() },
                            color = when (post.type) {
                                "educational" -> AccentGreen
                                "reel" -> AccentBlue
                                "poll" -> ElectricPurple
                                else -> TextMuted
                            }, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(6.dp))
                }

                // Content
                Text(post.content, color = TextPrimary, fontSize = 15.sp, lineHeight = 22.sp)

                // Hashtags
                val hashtags = post.content.split(" ").filter { it.startsWith("#") }
                if (hashtags.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        hashtags.take(3).forEach { tag ->
                            Text(tag, color = AccentBlue, fontSize = 13.sp,
                                modifier = Modifier.clickable {})
                        }
                    }
                }

                // Image
                if (post.imageUrl.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    AsyncImage(model = post.imageUrl, contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop)
                }

                Spacer(Modifier.height(12.dp))

                // Action buttons
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {

                    // Like
                    ActionBtn(
                        icon = if (liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        count = likeCount,
                        tint = if (liked) AccentRed else TextMuted,
                        onClick = {
                            liked = !liked
                            likeCount = if (liked) likeCount + 1 else likeCount - 1
                            onLike()
                        }
                    )
                    // Comment
                    ActionBtn(icon = Icons.Outlined.ChatBubbleOutline, count = post.comments,
                        tint = TextMuted, onClick = { onPostClick?.invoke(post.id) })
                    // Repost
                    ActionBtn(
                        icon = Icons.Outlined.Repeat,
                        count = repostCount,
                        tint = if (reposted) AccentGreen else TextMuted,
                        onClick = {
                            reposted = !reposted
                            repostCount = if (reposted) repostCount + 1 else repostCount - 1
                        }
                    )
                    // Share
                    ActionBtn(icon = Icons.Outlined.Share, count = 0, tint = TextMuted,
                        onClick = { showShareSheet = true })
                    // Bookmark
                    IconButton(onClick = { bookmarked = !bookmarked },
                        modifier = Modifier.size(32.dp)) {
                        Icon(
                            if (bookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            null,
                            tint = if (bookmarked) ElectricPurple else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
        HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
    }
}

@Composable
fun ActionBtn(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    tint: Color,
    onClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(onClick = onClick).padding(vertical = 4.dp, horizontal = 4.dp)) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp))
        if (count > 0) {
            Spacer(Modifier.width(4.dp))
            Text(if (count >= 1000) "${"%.1f".format(count / 1000.0)}K" else count.toString(),
                color = TextMuted, fontSize = 13.sp)
        }
    }
}

// Share sheet bottom dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareSheet(post: FirestorePost, onDismiss: () -> Unit) {
    val shareApps = listOf(
        Triple("WhatsApp", "💬", Color(0xFF25D366)),
        Triple("Instagram", "📸", Color(0xFFE1306C)),
        Triple("Facebook", "👥", Color(0xFF1877F2)),
        Triple("X (Twitter)", "🐦", Color.White),
        Triple("Copy Link", "🔗", TextMuted),
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = { Text("Share", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                // Post preview
                Card(colors = CardDefaults.cardColors(containerColor = DarkBackground),
                    shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(ElectricPurple),
                            contentAlignment = Alignment.Center) {
                            Text(post.authorName.firstOrNull()?.toString() ?: "?",
                                color = TextPrimary, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(post.authorName, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text(post.content, color = TextSecondary, fontSize = 13.sp, maxLines = 2)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("Share to", color = TextMuted, fontSize = 12.sp)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    shareApps.forEach { (name, emoji, _) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onDismiss() }) {
                            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(DarkBackground),
                                contentAlignment = Alignment.Center) {
                                Text(emoji, fontSize = 22.sp)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(name.split(" ")[0], color = TextMuted, fontSize = 10.sp)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("Send in Elimu Social", color = TextMuted, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf("Mary", "John", "Brian", "Joyce", "More").forEach { name ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onDismiss() }) {
                            Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(ElectricPurple),
                                contentAlignment = Alignment.Center) {
                                Text(if (name == "More") "+" else name.firstOrNull()?.toString() ?: "?",
                                    color = TextPrimary, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(name, color = TextMuted, fontSize = 10.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) }
        }
    )
}

fun formatTimestamp(millis: Long): String {
    val diff = System.currentTimeMillis() - millis
    return when {
        diff < 60_000 -> "just now"
        diff < 3_600_000 -> "${diff / 60_000}m"
        diff < 86_400_000 -> "${diff / 3_600_000}h"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(millis))
    }
}
