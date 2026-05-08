package com.elimusocial.app.ui.screens.social

import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elimusocial.app.ui.theme.*
import kotlinx.coroutines.delay

data class LiveComment(val user: String, val text: String, val emoji: String = "")
data class LiveStream(val id: String, val host: String, val title: String, val viewers: Int, val isLive: Boolean = true)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveStreamScreen(onBack: () -> Unit) {
    var isWatchingLive by remember { mutableStateOf(false) }
    var isGoingLive by remember { mutableStateOf(false) }
    var selectedStream by remember { mutableStateOf<LiveStream?>(null) }

    val liveStreams = listOf(
        LiveStream("1", "John Kamau", "Building a Flutter App Live! 🔴", 243),
        LiveStream("2", "Teacher Alex", "KCSE Math Revision Session 📚", 156),
        LiveStream("3", "Code Club", "Hackathon Kickoff 🚀", 89),
        LiveStream("4", "Mary Wanjiku", "Study with me — Finals prep", 67),
    )

    if (selectedStream != null || isWatchingLive) {
        LiveWatchScreen(stream = selectedStream ?: liveStreams[0], onBack = { selectedStream = null; isWatchingLive = false })
        return
    }

    if (isGoingLive) {
        GoLiveSetupScreen(onBack = { isGoingLive = false }, onStartLive = { isWatchingLive = true })
        return
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                title = { Text("Live", fontWeight = FontWeight.Bold, color = TextPrimary) },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Search, null, tint = TextSecondary) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Go Live button
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { isGoingLive = true },
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(2.dp, Brush.linearGradient(listOf(AccentRed, ElectricPurple)))
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(AccentRed.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.RadioButtonChecked, null, tint = AccentRed, modifier = Modifier.size(28.dp))
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Go Live 🔴", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            Text("Start your live stream now", color = TextMuted, fontSize = 13.sp)
                        }
                        Icon(Icons.Default.ChevronRight, null, tint = AccentRed)
                    }
                }
            }

            // Section header
            item {
                Text("Live Now 🔴", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            // Live streams
            items(liveStreams) { stream ->
                LiveStreamCard(stream = stream, onClick = { selectedStream = stream })
            }
        }
    }
}

@Composable
fun LiveStreamCard(stream: LiveStream, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box {
            // Preview placeholder
            Box(modifier = Modifier.fillMaxWidth().height(180.dp).background(Brush.linearGradient(listOf(Color(0xFF1A0033), Color(0xFF0D0D1A)))), contentAlignment = Alignment.Center) {
                Text("📹", fontSize = 48.sp)
            }
            // Live badge
            Box(modifier = Modifier.align(Alignment.TopStart).padding(10.dp).background(AccentRed, RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                Text("LIVE", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            // Viewers
            Box(modifier = Modifier.align(Alignment.TopEnd).padding(10.dp).background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Visibility, null, tint = Color.White, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${stream.viewers}", color = Color.White, fontSize = 11.sp)
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(38.dp).clip(CircleShape).background(ElectricPurple), contentAlignment = Alignment.Center) {
                Text(stream.host.firstOrNull()?.toString() ?: "?", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(stream.title, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1)
                Text(stream.host, color = TextMuted, fontSize = 12.sp)
            }
            Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = AccentRed), shape = RoundedCornerShape(20.dp), contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp), modifier = Modifier.height(32.dp)) {
                Text("Watch", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveWatchScreen(stream: LiveStream, onBack: () -> Unit) {
    var commentText by remember { mutableStateOf("") }
    var viewerCount by remember { mutableIntStateOf(stream.viewers) }
    val listState = rememberLazyListState()
    val comments = remember {
        mutableStateListOf(
            LiveComment("Mary W.", "This is so insightful! 🔥"),
            LiveComment("Brian", "Great session! 🙌"),
            LiveComment("Joyce", "Thank you! 💯"),
            LiveComment("Teacher Alex", "Great explanation Brian! 🎉"),
        )
    }
    val emojis = listOf("❤️", "🔥", "👏", "😮", "💯", "🎉")
    var floatingEmoji by remember { mutableStateOf("") }

    // Simulate live viewers changing
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            viewerCount += (-5..10).random()
        }
    }

    // Auto scroll comments
    LaunchedEffect(comments.size) {
        if (comments.isNotEmpty()) listState.animateScrollToItem(comments.size - 1)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Video background
        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.65f).background(Brush.verticalGradient(listOf(Color(0xFF1A0033), Color(0xFF0A0A1A)))), contentAlignment = Alignment.Center) {
            Text("📹", fontSize = 80.sp)
            Text("LIVE", color = AccentRed, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.align(Alignment.TopStart).padding(16.dp).background(AccentRed.copy(alpha = 0.8f), RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 4.dp))
        }

        // Top bar
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp).statusBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), CircleShape)) {
                Icon(Icons.Default.ArrowBack, null, tint = Color.White)
            }
            Spacer(Modifier.width(8.dp))
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(ElectricPurple), contentAlignment = Alignment.Center) {
                Text(stream.host.firstOrNull()?.toString() ?: "?", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(stream.host, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(stream.title, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, maxLines = 1)
            }
            Row(modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(12.dp)).padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Visibility, null, tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("$viewerCount", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = {}, modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), CircleShape)) {
                Icon(Icons.Default.MoreVert, null, tint = Color.White)
            }
        }

        // Bottom section — comments + input
        Column(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))))) {
            // Comments
            LazyColumn(state = listState, modifier = Modifier.fillMaxWidth().height(200.dp).padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(6.dp), contentPadding = PaddingValues(vertical = 8.dp)) {
                items(comments) { comment ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(26.dp).clip(CircleShape).background(ElectricPurple), contentAlignment = Alignment.Center) {
                            Text(comment.user.firstOrNull()?.toString() ?: "?", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(6.dp))
                        Box(modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp)).padding(horizontal = 10.dp, vertical = 6.dp)) {
                            Row {
                                Text("${comment.user}: ", color = ElectricPurple, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text(comment.text, color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Emoji reactions row
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                emojis.forEach { emoji ->
                    Text(emoji, fontSize = 24.sp, modifier = Modifier.clickable {
                        comments.add(LiveComment("You", emoji))
                    })
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = {}, modifier = Modifier.size(36.dp).background(Color.Black.copy(alpha = 0.4f), CircleShape)) {
                    Icon(Icons.Default.Share, null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }

            // Comment input
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp).navigationBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = commentText, onValueChange = { commentText = it },
                    placeholder = { Text("Add a comment...", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp) },
                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricPurple.copy(alpha = 0.7f), unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedContainerColor = Color.Black.copy(alpha = 0.4f), unfocusedContainerColor = Color.Black.copy(alpha = 0.4f),
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = Color.White
                    ), singleLine = true
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            comments.add(LiveComment("You", commentText))
                            commentText = ""
                        }
                    },
                    modifier = Modifier.size(42.dp).background(ElectricPurple, CircleShape)
                ) { Icon(Icons.Default.Send, null, tint = Color.White, modifier = Modifier.size(20.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoLiveSetupScreen(onBack: () -> Unit, onStartLive: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Education") }
    val categories = listOf("Education", "Tech", "Study", "Discussion", "Entertainment")

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.Close, null, tint = TextPrimary) } },
                title = { Text("Go Live", fontWeight = FontWeight.Bold, color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Camera preview
            Box(modifier = Modifier.fillMaxWidth().height(240.dp).clip(RoundedCornerShape(20.dp)).background(Brush.linearGradient(listOf(Color(0xFF1A0033), DarkCard))), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.VideoCall, null, tint = TextMuted, modifier = Modifier.size(60.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Camera preview", color = TextMuted)
                }
                Row(modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    IconButton(onClick = {}, modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)) {
                        Icon(Icons.Default.FlipCameraAndroid, null, tint = Color.White)
                    }
                    IconButton(onClick = {}, modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)) {
                        Icon(Icons.Default.Mic, null, tint = Color.White)
                    }
                    IconButton(onClick = {}, modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)) {
                        Icon(Icons.Default.Mood, null, tint = Color.White)
                    }
                }
            }

            OutlinedTextField(
                value = title, onValueChange = { title = it },
                placeholder = { Text("Stream title...", color = TextMuted) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricPurple, unfocusedBorderColor = DividerColor, focusedContainerColor = DarkCard, unfocusedContainerColor = DarkCard, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = ElectricPurple)
            )

            Text("Category", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat, onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricPurple, selectedLabelColor = Color.White, containerColor = DarkCard, labelColor = TextMuted)
                    )
                }
            }

            Spacer(Modifier.weight(1f))
            Button(
                onClick = onStartLive, modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                shape = RoundedCornerShape(16.dp), enabled = title.isNotBlank()
            ) {
                Icon(Icons.Default.RadioButtonChecked, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Start Live Stream", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
