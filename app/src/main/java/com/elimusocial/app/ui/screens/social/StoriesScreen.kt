package com.elimusocial.app.ui.screens.social

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.elimusocial.app.data.models.SampleData
import com.elimusocial.app.ui.theme.*

data class Story(
    val userId: String,
    val userName: String,
    val timeAgo: String,
    val isViewed: Boolean = false,
    val isYours: Boolean = false,
    val caption: String = ""
)

@Composable
fun StoriesRow() {
    val stories = listOf(
        Story("0", "Your story", "", isYours = true),
        Story("1", "Mary W.", "2h ago", caption = "Beautiful evening at the campus 🌅"),
        Story("2", "Brian", "3h ago", caption = "Just shipped a new feature! 🚀"),
        Story("3", "Joyce", "4h ago", caption = "Study session vibes 📚"),
        Story("4", "Alex", "5h ago", isViewed = true, caption = "Tech Talk Tomorrow!"),
        Story("5", "Teacher Alex", "6h ago", isViewed = true, caption = "Exam tips 🧠")
    )

    var viewingStory by remember { mutableStateOf<Story?>(null) }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(stories) { story ->
            StoryAvatar(story = story, onClick = { viewingStory = story })
        }
    }

    HorizontalDivider(color = DividerColor, thickness = 0.5.dp)

    // Full screen story viewer
    viewingStory?.let { story ->
        StoryViewer(story = story, onDismiss = { viewingStory = null })
    }
}

@Composable
fun StoryAvatar(story: Story, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            // Ring border
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .background(
                        if (!story.isViewed && !story.isYours)
                            Brush.linearGradient(listOf(ElectricPurple, AccentBlue))
                        else
                            Brush.linearGradient(listOf(DividerColor, DividerColor)),
                        CircleShape
                    )
                    .padding(3.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkBackground, CircleShape)
                        .padding(2.dp)
                        .background(ElectricPurple, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (story.isYours) "+" else story.userName.first().toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }
            }

            if (story.isYours) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(ElectricPurple, CircleShape)
                        .border(2.dp, DarkBackground, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (story.isYours) "Your story" else story.userName,
            fontSize = 11.sp,
            color = if (story.isViewed) TextMuted else TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(68.dp)
        )
    }
}

@Composable
fun StoryViewer(story: Story, onDismiss: () -> Unit) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(story) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 5000, easing = LinearEasing)
        )
        onDismiss()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable(onClick = onDismiss)
        ) {
            // Story background gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1A0533), DarkBackground, Color(0xFF0A1628))
                        )
                    )
            )

            Column(modifier = Modifier.fillMaxSize()) {
                // Progress bar
                Spacer(modifier = Modifier.height(48.dp))
                LinearProgressIndicator(
                    progress = { progress.value },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .height(2.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ElectricPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(story.userName.first().toString(), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(story.userName, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text(story.timeAgo, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, null, tint = Color.White)
                    }
                }

                // Caption
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = story.caption.ifEmpty { "📸 ${story.userName}'s Story" },
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(24.dp)
                    )
                }

                // Reply bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(22.dp))
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text("Reply to ${story.userName}...", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.Favorite, null, tint = Color.White, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(Icons.Default.Send, null, tint = Color.White, modifier = Modifier.size(28.dp))
                }
            }
        }
    }
}
