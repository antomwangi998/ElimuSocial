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
    onLike: () -> Unit
) {
    var liked by remember(isLiked) { mutableStateOf(isLiked) }
    var likeCount by remember(post.likes) { mutableIntStateOf(post.likes) }
    var isBookmarked by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().background(DarkBackground)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Avatar
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(ElectricPurple),
                contentAlignment = Alignment.Center
            ) {
                if (post.authorAvatarUrl.isNotEmpty()) {
                    AsyncImage(model = post.authorAvatarUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Text(post.authorName.firstOrNull()?.toString() ?: "?", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(post.authorName, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 15.sp)
                        if (post.authorVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Filled.Verified, null, tint = ElectricPurple, modifier = Modifier.size(16.dp))
                        }
                    }
                    Text(formatTimestamp(post.createdAt), color = TextMuted, fontSize = 12.sp)
                }
                Text(post.authorUsername, color = TextMuted, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(post.content, color = TextPrimary, fontSize = 15.sp, lineHeight = 22.sp)

                if (post.imageUrl.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    AsyncImage(
                        model = post.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    PostActionButton(
                        icon = if (liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        count = likeCount,
                        tint = if (liked) AccentRed else TextMuted,
                        onClick = {
                            liked = !liked
                            likeCount = if (liked) likeCount + 1 else likeCount - 1
                            onLike()
                        }
                    )
                    PostActionButton(icon = Icons.Outlined.ChatBubbleOutline, count = post.comments, tint = TextMuted, onClick = {})
                    PostActionButton(icon = Icons.Outlined.Repeat, count = post.reposts, tint = TextMuted, onClick = {})
                    IconButton(onClick = { isBookmarked = !isBookmarked }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            null,
                            tint = if (isBookmarked) ElectricPurple else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
        HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
    }
}

fun formatTimestamp(millis: Long): String {
    val diff = System.currentTimeMillis() - millis
    return when {
        diff < 60_000 -> "just now"
        diff < 3_600_000 -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> "${diff / 3_600_000}h ago"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(millis))
    }
}

@Composable
fun PostActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, count: Int, tint: Color, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable(onClick = onClick)) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp))
        if (count > 0) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (count >= 1000) "${count / 1000}K" else count.toString(), color = TextMuted, fontSize = 13.sp)
        }
    }
}
