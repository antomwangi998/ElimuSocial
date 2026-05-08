package com.elimusocial.app.ui.screens.social

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.elimusocial.app.ui.theme.*
import kotlinx.coroutines.delay

data class Story(
    val id: String,
    val userId: String,
    val userName: String,
    val timeAgo: String,
    val isViewed: Boolean = false,
    val isYours: Boolean = false,
    val caption: String = "",
    val emoji: String = ""
)

@Composable
fun StoriesRow() {
    val stories = remember {
        mutableStateListOf(
            Story("0", "me", "Your story", "", isYours = true),
            Story("1", "1", "Mary W.", "2h ago", caption = "Beautiful evening at the campus 🌅", emoji = "🌅"),
            Story("2", "2", "Brian", "3h ago", caption = "Just shipped a new feature! 🚀", emoji = "🚀"),
            Story("3", "3", "Joyce", "4h ago", caption = "Study session vibes 📚", emoji = "📚"),
            Story("4", "4", "Alex", "5h ago", isViewed = true, caption = "Tech Talk Tomorrow!", emoji = "💻"),
            Story("5", "5", "Teacher Alex", "6h ago", isViewed = true, caption = "Exam tips — check pinned post 🧠", emoji = "🧠"),
        )
    }

    var viewingStory by remember { mutableStateOf<Story?>(null) }
    var showAddStory by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            stories.add(1, Story(System.currentTimeMillis().toString(), "me", "Your story", "Just now", isYours = true, caption = "New story!", emoji = "✨"))
        }
    }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(stories, key = { it.id }) { story ->
            StoryAvatar(
                story = story,
                onClick = {
                    if (story.isYours) showAddStory = true
                    else viewingStory = story
                }
            )
        }
    }
    HorizontalDivider(color = DividerColor, thickness = 0.5.dp)

    if (showAddStory) {
        AlertDialog(
            onDismissRequest = { showAddStory = false },
            containerColor = DarkCard,
            title = { Text("Add to your story", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    AddStoryOption(icon = Icons.Default.PhotoCamera, label = "Camera", color = ElectricPurple) { showAddStory = false }
                    AddStoryOption(icon = Icons.Default.Photo, label = "Gallery", color = AccentBlue) { imageLauncher.launch("image/*"); showAddStory = false }
                    AddStoryOption(icon = Icons.Default.TextFields, label = "Text story", color = AccentGreen) { showAddStory = false }
                    AddStoryOption(icon = Icons.Default.Mic, label = "Voice story", color = AccentOrange) { showAddStory = false }
                }
            },
            confirmButton = { TextButton(onClick = { showAddStory = false }) { Text("Cancel", color = TextMuted) } }
        )
    }

    viewingStory?.let { story ->
        val storyIndex = stories.indexOf(story)
        StoryViewer(
            stories = stories.filter { !it.isYours },
            initialIndex = maxOf(0, storyIndex - 1),
            onDismiss = {
                val idx = stories.indexOf(story)
                if (idx >= 0) stories[idx] = story.copy(isViewed = true)
                viewingStory = null
            }
        )
    }
}

@Composable
fun AddStoryOption(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(14.dp))
        Text(label, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun StoryAvatar(story: Story, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier.size(68.dp).background(
                    if (!story.isViewed && !story.isYours) Brush.linearGradient(listOf(ElectricPurple, AccentOrange))
                    else if (story.isYours) Brush.linearGradient(listOf(DarkCard, DarkCard))
                    else Brush.linearGradient(listOf(TextMuted.copy(alpha = 0.3f), TextMuted.copy(alpha = 0.3f))),
                    CircleShape
                ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(62.dp).clip(CircleShape)
                        .background(if (story.isYours) DarkCard else ElectricPurple),
                    contentAlignment = Alignment.Center
                ) {
                    if (story.isYours) {
                        Icon(Icons.Default.Add, null, tint = ElectricPurple, modifier = Modifier.size(28.dp))
                    } else {
                        Text(story.emoji.ifEmpty { story.userName.firstOrNull()?.toString() ?: "?" }, fontSize = if (story.emoji.isNotEmpty()) 24.sp else 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        Spacer(Modifier.height(5.dp))
        Text(
            if (story.isYours) "Your story" else story.userName.split(" ").first(),
            color = if (story.isViewed) TextMuted else TextPrimary,
            fontSize = 11.sp,
            maxLines = 1
        )
    }
}

@Composable
fun StoryViewer(stories: List<Story>, initialIndex: Int = 0, onDismiss: () -> Unit) {
    var currentIndex by remember { mutableIntStateOf(initialIndex) }
    var progress by remember { mutableFloatStateOf(0f) }
    var isPaused by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf("") }
    var showReactions by remember { mutableStateOf(false) }
    val currentStory = stories.getOrNull(currentIndex) ?: return

    val emojis = listOf("❤️", "🔥", "😂", "😮", "👏", "💯")

    LaunchedEffect(currentIndex, isPaused) {
        progress = 0f
        while (progress < 1f && !isPaused) {
            delay(50)
            progress += 0.02f
        }
        if (progress >= 1f) {
            if (currentIndex < stories.size - 1) { currentIndex++; progress = 0f }
            else onDismiss()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black)
                .clickable {
                    if (currentIndex < stories.size - 1) { currentIndex++; progress = 0f }
                    else onDismiss()
                }
        ) {
            // Background gradient
            Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF1A0033), DarkBackground))))

            // Story content
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(currentStory.emoji.ifEmpty { "✨" }, fontSize = 120.sp)
            }

            // Progress bars
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp).statusBarsPadding(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                stories.forEachIndexed { index, _ ->
                    Box(modifier = Modifier.weight(1f).height(2.dp).clip(RoundedCornerShape(2.dp)).background(Color.White.copy(alpha = 0.3f))) {
                        Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(
                            when { index < currentIndex -> 1f; index == currentIndex -> progress; else -> 0f }
                        ).background(Color.White))
                    }
                }
            }

            // Top bar
            Column(modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(38.dp).clip(CircleShape).background(ElectricPurple).border(2.dp, Color.White, CircleShape), contentAlignment = Alignment.Center) {
                        Text(currentStory.userName.firstOrNull()?.toString() ?: "?", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(currentStory.userName, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text(currentStory.timeAgo, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                    IconButton(onClick = { isPaused = !isPaused }) {
                        Icon(if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause, null, tint = Color.White)
                    }
                    IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, null, tint = Color.White) }
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null, tint = Color.White) }
                }
            }

            // Navigation tap zones
            Row(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable {
                    if (currentIndex > 0) { currentIndex--; progress = 0f }
                })
                Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable {
                    if (currentIndex < stories.size - 1) { currentIndex++; progress = 0f }
                    else onDismiss()
                })
            }

            // Caption + bottom bar
            Column(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
                if (currentStory.caption.isNotEmpty()) {
                    Text(currentStory.caption, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
                }

                // Reactions
                if (showReactions) {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(24.dp)).padding(8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                        emojis.forEach { emoji ->
                            Text(emoji, fontSize = 28.sp, modifier = Modifier.clickable { showReactions = false })
                        }
                    }
                }

                // Reply row
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp).navigationBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = replyText, onValueChange = { replyText = it },
                        placeholder = { Text("Reply to ${currentStory.userName}...", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp) },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White.copy(alpha = 0.5f), unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedContainerColor = Color.Black.copy(alpha = 0.3f), unfocusedContainerColor = Color.Black.copy(alpha = 0.3f),
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = Color.White
                        ), singleLine = true
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = { showReactions = !showReactions }, modifier = Modifier.size(44.dp).background(Color.Black.copy(alpha = 0.3f), CircleShape)) {
                        Text("❤️", fontSize = 20.sp)
                    }
                    IconButton(onClick = { if (replyText.isNotBlank()) replyText = "" }, modifier = Modifier.size(44.dp).background(if (replyText.isNotBlank()) ElectricPurple else Color.Black.copy(alpha = 0.3f), CircleShape)) {
                        Icon(Icons.Default.Send, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}
