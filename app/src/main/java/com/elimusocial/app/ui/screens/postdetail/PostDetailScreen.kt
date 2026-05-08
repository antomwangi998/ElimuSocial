package com.elimusocial.app.ui.screens.postdetail

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.elimusocial.app.data.repository.FirestorePost
import com.elimusocial.app.ui.screens.components.ActionBtn
import com.elimusocial.app.ui.screens.components.formatTimestamp
import com.elimusocial.app.ui.theme.*
import kotlinx.coroutines.launch

data class Comment(
    val id: String,
    val author: String,
    val handle: String,
    val content: String,
    val time: String,
    var likes: Int = 0,
    val replies: MutableList<Comment> = mutableListOf(),
    var isLiked: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    post: FirestorePost = FirestorePost(
        id = "demo",
        authorName = "Antony Mwangi",
        authorUsername = "@antony",
        authorVerified = true,
        content = "Just finished our computer science project! 🔥💻 Teamwork makes the dream work! 💪\n\n#teamwork #coding #ElimuSocial",
        likes = 128,
        comments = 32,
        reposts = 18,
        createdAt = System.currentTimeMillis() - 3_600_000
    ),
    isLiked: Boolean = false,
    onLike: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var commentText by remember { mutableStateOf("") }
    var replyingTo by remember { mutableStateOf<Comment?>(null) }
    var postLiked by remember { mutableStateOf(isLiked) }
    var likeCount by remember { mutableIntStateOf(post.likes) }
    var reposted by remember { mutableStateOf(false) }
    var repostCount by remember { mutableIntStateOf(post.reposts) }
    var bookmarked by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val comments = remember {
        mutableStateListOf(
            Comment("1", "Mary Wanjiku", "@mary", "Great work team! 🔥🔥 You guys crushed it!", "2h", likes = 24),
            Comment("2", "John Kamau", "@john", "This is awesome! 🙌🙌", "1h", likes = 12,
                replies = mutableListOf(
                    Comment("2r1", "Antony Mwangi", "@antony", "Thanks John! 🙏", "45m", likes = 3),
                )),
            Comment("3", "Sarah Kimani", "@sarah", "Proud of you guys! Keep it up 💯", "30m", likes = 8),
            Comment("4", "Teacher Alex", "@teacher_alex", "Excellent work! This is what innovation looks like.", "15m", likes = 31,
                replies = mutableListOf(
                    Comment("4r1", "Antony Mwangi", "@antony", "Thank you Teacher Alex! 🙏", "10m", likes = 7),
                    Comment("4r2", "Mary Wanjiku", "@mary", "Agreed! They worked so hard 💪", "8m", likes = 4),
                )),
            Comment("5", "Brian Otieno", "@brian_dev", "Can you share the GitHub repo? 👀", "5m", likes = 6),
        )
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                title = { Text("Post", fontWeight = FontWeight.Bold, color = TextPrimary) },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Share, null, tint = TextSecondary) }
                    IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, null, tint = TextSecondary) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth().background(DarkSurface)) {
                if (replyingTo != null) {
                    Row(modifier = Modifier.fillMaxWidth().background(ElectricPurple.copy(alpha = 0.1f)).padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Replying to ", color = TextMuted, fontSize = 13.sp)
                        Text(replyingTo!!.handle, color = ElectricPurple, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = { replyingTo = null }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Close, null, tint = TextMuted, modifier = Modifier.size(14.dp))
                        }
                    }
                }
                HorizontalDivider(color = DividerColor)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp).imePadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(ElectricPurple), contentAlignment = Alignment.Center) {
                        Text("A", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(10.dp))
                    OutlinedTextField(
                        value = commentText, onValueChange = { commentText = it },
                        placeholder = { Text(if (replyingTo != null) "Write a reply..." else "Post your reply...", color = TextMuted, fontSize = 14.sp) },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricPurple, unfocusedBorderColor = DividerColor,
                            focusedContainerColor = DarkBackground, unfocusedContainerColor = DarkBackground,
                            focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = ElectricPurple
                        ), maxLines = 4
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                if (replyingTo != null) {
                                    val parentIdx = comments.indexOfFirst { it.id == replyingTo!!.id }
                                    if (parentIdx >= 0) {
                                        comments[parentIdx].replies.add(Comment(System.currentTimeMillis().toString(), "You", "@you", commentText, "Now"))
                                        comments[parentIdx] = comments[parentIdx].copy()
                                    }
                                    replyingTo = null
                                } else {
                                    comments.add(0, Comment(System.currentTimeMillis().toString(), "You", "@you", commentText, "Now"))
                                }
                                commentText = ""
                                scope.launch { listState.animateScrollToItem(0) }
                            }
                        },
                        enabled = commentText.isNotBlank(),
                        modifier = Modifier.size(42.dp).background(if (commentText.isNotBlank()) ElectricPurple else DarkCard, CircleShape)
                    ) {
                        Icon(Icons.Default.Send, null, tint = if (commentText.isNotBlank()) Color.White else TextMuted, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize().padding(padding)) {
            // Original post
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(ElectricPurple), contentAlignment = Alignment.Center) {
                            if (post.authorAvatarUrl.isNotEmpty()) {
                                AsyncImage(model = post.authorAvatarUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            } else {
                                Text(post.authorName.firstOrNull()?.toString() ?: "?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(post.authorName, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                                if (post.authorVerified) {
                                    Spacer(Modifier.width(4.dp))
                                    Icon(Icons.Filled.Verified, null, tint = ElectricPurple, modifier = Modifier.size(16.dp))
                                }
                            }
                            Text(post.authorUsername, color = TextMuted, fontSize = 14.sp)
                        }
                        IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, null, tint = TextMuted) }
                    }

                    Spacer(Modifier.height(14.dp))
                    Text(buildAnnotatedString {
                        post.content.split(" ").forEach { word ->
                            if (word.startsWith("#") || word.startsWith("@")) {
                                withStyle(SpanStyle(color = AccentBlue)) { append("$word ") }
                            } else {
                                withStyle(SpanStyle(color = TextPrimary)) { append("$word ") }
                            }
                        }
                    }, fontSize = 17.sp, lineHeight = 26.sp)

                    if (post.imageUrl.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        AsyncImage(model = post.imageUrl, contentDescription = null, modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                    }

                    Spacer(Modifier.height(14.dp))
                    Text(formatTimestamp(post.createdAt) + " · Elimu Social", color = TextMuted, fontSize = 13.sp)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DividerColor)

                    // Stats
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp), modifier = Modifier.fillMaxWidth()) {
                        if (repostCount > 0) { StatText(repostCount.toString(), "Reposts") }
                        if (likeCount > 0) { StatText(likeCount.toString(), "Likes") }
                        if (post.comments > 0) { StatText(post.comments.toString(), "Replies") }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DividerColor)

                    // Actions
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        ActionBtn(icon = Icons.Outlined.ChatBubbleOutline, count = 0, tint = TextMuted, onClick = {})
                        ActionBtn(icon = Icons.Outlined.Repeat, count = repostCount, tint = if (reposted) AccentGreen else TextMuted, onClick = { reposted = !reposted; repostCount = if (reposted) repostCount + 1 else repostCount - 1 })
                        ActionBtn(icon = if (postLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, count = likeCount, tint = if (postLiked) AccentRed else TextMuted, onClick = { postLiked = !postLiked; likeCount = if (postLiked) likeCount + 1 else likeCount - 1; onLike() })
                        ActionBtn(icon = if (bookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder, count = 0, tint = if (bookmarked) ElectricPurple else TextMuted, onClick = { bookmarked = !bookmarked })
                        ActionBtn(icon = Icons.Outlined.Share, count = 0, tint = TextMuted, onClick = {})
                    }
                    HorizontalDivider(modifier = Modifier.padding(top = 12.dp), color = DividerColor)
                }
            }

            // Comments header
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Replies", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    TextButton(onClick = {}) { Text("Most relevant ▾", color = TextMuted, fontSize = 13.sp) }
                }
            }

            // Comments list
            items(comments, key = { it.id }) { comment ->
                CommentItem(
                    comment = comment,
                    onReply = { replyingTo = comment },
                    onLike = {
                        val idx = comments.indexOf(comment)
                        if (idx >= 0) {
                            val updated = comment.copy(likes = if (comment.isLiked) comment.likes - 1 else comment.likes + 1, isLiked = !comment.isLiked)
                            comments[idx] = updated
                        }
                    }
                )
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun StatText(count: String, label: String) {
    Row {
        Text(count, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
        Text(" $label", color = TextMuted, fontSize = 15.sp)
    }
}

@Composable
fun CommentItem(comment: Comment, onReply: () -> Unit, onLike: () -> Unit, isReply: Boolean = false) {
    var showReplies by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().clickable {}.padding(start = if (isReply) 56.dp else 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp), verticalAlignment = Alignment.Top) {
            Box(modifier = Modifier.size(if (isReply) 32.dp else 40.dp).clip(CircleShape).background(ElectricPurple.copy(alpha = if (isReply) 0.6f else 1f)), contentAlignment = Alignment.Center) {
                Text(comment.author.firstOrNull()?.toString() ?: "?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = if (isReply) 13.sp else 16.sp)
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(comment.author, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 14.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(comment.handle, color = TextMuted, fontSize = 12.sp)
                    }
                    Text(comment.time, color = TextMuted, fontSize = 12.sp)
                }
                Spacer(Modifier.height(4.dp))
                Text(comment.content, color = TextPrimary, fontSize = 14.sp, lineHeight = 20.sp)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable(onClick = onReply)) {
                        Icon(Icons.Outlined.ChatBubbleOutline, null, tint = TextMuted, modifier = Modifier.size(16.dp))
                        if (comment.replies.isNotEmpty()) { Spacer(Modifier.width(4.dp)); Text(comment.replies.size.toString(), color = TextMuted, fontSize = 12.sp) }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable(onClick = onLike)) {
                        Icon(if (comment.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, null, tint = if (comment.isLiked) AccentRed else TextMuted, modifier = Modifier.size(16.dp))
                        if (comment.likes > 0) { Spacer(Modifier.width(4.dp)); Text(comment.likes.toString(), color = TextMuted, fontSize = 12.sp) }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {}) {
                        Icon(Icons.Outlined.Repeat, null, tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {}) {
                        Icon(Icons.Outlined.Share, null, tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
        HorizontalDivider(color = DividerColor, thickness = 0.5.dp, modifier = Modifier.padding(start = if (isReply) 56.dp else 0.dp))

        // Nested replies
        if (!isReply && comment.replies.isNotEmpty()) {
            if (showReplies) {
                comment.replies.forEach { reply ->
                    CommentItem(comment = reply, onReply = {}, onLike = {}, isReply = true)
                }
            }
            TextButton(onClick = { showReplies = !showReplies }, modifier = Modifier.padding(start = 56.dp)) {
                Text(if (showReplies) "Hide replies" else "Show ${comment.replies.size} replies", color = ElectricPurple, fontSize = 13.sp)
            }
        }
    }
}
