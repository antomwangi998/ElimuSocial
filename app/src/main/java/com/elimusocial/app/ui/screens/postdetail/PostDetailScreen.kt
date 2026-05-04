package com.elimusocial.app.ui.screens.postdetail

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elimusocial.app.ui.theme.*

data class Comment(
    val id: String,
    val author: String,
    val handle: String,
    val content: String,
    val time: String,
    val likes: Int = 0,
    val replies: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    authorName: String = "Antony Mwangi",
    authorHandle: String = "@antony",
    postContent: String = "Just finished our computer science project! 🔥💻 Teamwork makes the dream work! 💪",
    postTime: String = "10:30 AM · May 12, 2024 · Elimu Social for Web",
    likes: Int = 128,
    commentsCount: Int = 32,
    reposts: Int = 18,
    onBack: () -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    var isLiked by remember { mutableStateOf(false) }
    var likeCount by remember { mutableIntStateOf(likes) }
    var isBookmarked by remember { mutableStateOf(false) }

    val comments = remember {
        listOf(
            Comment("1", "Mary Wanjiku", "@mary", "Great work team! 🔥🔥", "2h", likes = 24, replies = 2),
            Comment("2", "John Kamau", "@john", "This is awesome! 🙌🙌", "1h", likes = 12, replies = 1),
            Comment("3", "Sarah Kimani", "@sarah", "Proud of you guys! Keep it up 💯", "30m", likes = 8),
            Comment("4", "Teacher Alex", "@teacher_alex", "Excellent work! This is what innovation looks like.", "15m", likes = 31, replies = 3),
            Comment("5", "Brian Otieno", "@brian_dev", "Can you share the GitHub repo? 👀", "5m", likes = 6),
        )
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) }
                },
                title = { Text("Post", fontWeight = FontWeight.Bold, color = TextPrimary) },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, null, tint = TextSecondary) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        bottomBar = {
            // Comment input
            Row(
                modifier = Modifier.fillMaxWidth().background(DarkCard).padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(ElectricPurple), contentAlignment = Alignment.Center) {
                    Text("A", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(10.dp))
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text("Post your reply...", color = TextMuted, fontSize = 14.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricPurple, unfocusedBorderColor = DividerColor,
                        focusedContainerColor = DarkBackground, unfocusedContainerColor = DarkBackground,
                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = ElectricPurple
                    ),
                    maxLines = 3,
                    singleLine = false
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { commentText = "" },
                    enabled = commentText.isNotBlank(),
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(if (commentText.isNotBlank()) ElectricPurple else DarkCard)
                ) {
                    Icon(Icons.Default.Send, null, tint = if (commentText.isNotBlank()) TextPrimary else TextMuted, modifier = Modifier.size(20.dp))
                }
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Original post
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(ElectricPurple), contentAlignment = Alignment.Center) {
                            Text(authorName.firstOrNull()?.toString() ?: "?", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(authorName, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Filled.Verified, null, tint = ElectricPurple, modifier = Modifier.size(16.dp))
                            }
                            Text(authorHandle, color = TextMuted, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, null, tint = TextMuted) }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(postContent, color = TextPrimary, fontSize = 18.sp, lineHeight = 26.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(postTime, color = TextMuted, fontSize = 13.sp)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DividerColor)

                    // Stats row
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        Text("$reposts ", fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 14.sp)
                        Text("Reposts", color = TextMuted, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("$likeCount ", fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 14.sp)
                        Text("Likes", color = TextMuted, fontSize = 14.sp)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DividerColor)

                    // Action buttons
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        IconButton(onClick = {}) { Icon(Icons.Outlined.ChatBubbleOutline, null, tint = TextMuted) }
                        IconButton(onClick = {}) { Icon(Icons.Outlined.Repeat, null, tint = TextMuted) }
                        IconButton(onClick = { isLiked = !isLiked; likeCount = if (isLiked) likeCount + 1 else likeCount - 1 }) {
                            Icon(if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, null, tint = if (isLiked) AccentRed else TextMuted)
                        }
                        IconButton(onClick = { isBookmarked = !isBookmarked }) {
                            Icon(if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder, null, tint = if (isBookmarked) ElectricPurple else TextMuted)
                        }
                        IconButton(onClick = {}) { Icon(Icons.Outlined.Share, null, tint = TextMuted) }
                    }
                    HorizontalDivider(color = DividerColor)
                }
            }

            // Comments header
            item {
                Text("Most relevant replies ▾", color = TextMuted, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }

            items(comments, key = { it.id }) { comment ->
                CommentItem(comment = comment)
                HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    var liked by remember { mutableStateOf(false) }
    var likeCount by remember { mutableIntStateOf(comment.likes) }

    Row(modifier = Modifier.fillMaxWidth().clickable {}.padding(16.dp), verticalAlignment = Alignment.Top) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(DarkCard), contentAlignment = Alignment.Center) {
            Text(comment.author.firstOrNull()?.toString() ?: "?", color = TextPrimary, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(comment.author, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(comment.handle, color = TextMuted, fontSize = 13.sp)
                }
                Text(comment.time, color = TextMuted, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(comment.content, color = TextPrimary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {}) {
                    Icon(Icons.Outlined.ChatBubbleOutline, null, tint = TextMuted, modifier = Modifier.size(16.dp))
                    if (comment.replies > 0) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(comment.replies.toString(), color = TextMuted, fontSize = 12.sp)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { liked = !liked; likeCount = if (liked) likeCount + 1 else likeCount - 1 }) {
                    Icon(if (liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, null, tint = if (liked) AccentRed else TextMuted, modifier = Modifier.size(16.dp))
                    if (likeCount > 0) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(likeCount.toString(), color = TextMuted, fontSize = 12.sp)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {}) {
                    Icon(Icons.Outlined.Repeat, null, tint = TextMuted, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
