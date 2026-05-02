package com.elimusocial.app.ui.screens.social

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.elimusocial.app.data.repository.FirestorePost
import com.elimusocial.app.ui.theme.*

// ── Sample reel data (replace with Firestore reels from FeedViewModel) ────
val sampleReels = listOf(
    FirestorePost(
        id = "r1", authorName = "Mary Wanjiku", authorUsername = "@mary",
        content = "Study hard, dream big, achieve more! ✨ #StudyTips #ElimuSocial",
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        likes = 2400, comments = 156, type = "reel"
    ),
    FirestorePost(
        id = "r2", authorName = "Brian Otieno", authorUsername = "@brian_dev",
        content = "Built a full-stack app in 30 days 🚀 #WebDev #Coding",
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
        likes = 1800, comments = 234, type = "reel"
    ),
    FirestorePost(
        id = "r3", authorName = "Teacher Alex", authorUsername = "@teacher_alex",
        content = "5 study techniques that ACTUALLY work 🧠 Save this! #Education",
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
        likes = 5600, comments = 890, type = "reel"
    )
)

@OptIn(UnstableApi::class)
@androidx.compose.foundation.ExperimentalFoundationApi
@Composable
fun ReelsScreenWithPlayer(onBack: () -> Unit) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { sampleReels.size })
    var selectedTab by remember { mutableStateOf(1) }  // 0=Following, 1=ForYou

    // Create one ExoPlayer per reel page, release when done
    val players = remember {
        sampleReels.map { reel ->
            ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.fromUri(reel.videoUrl)
                setMediaItem(mediaItem)
                repeatMode = Player.REPEAT_MODE_ONE
                volume = 1f
                prepare()
            }
        }
    }

    // Auto-play current page, pause others
    LaunchedEffect(pagerState.currentPage) {
        players.forEachIndexed { index, player ->
            if (index == pagerState.currentPage) {
                player.play()
            } else {
                player.pause()
                player.seekTo(0)
            }
        }
    }

    // Release all players on dispose
    DisposableEffect(Unit) {
        onDispose {
            players.forEach { it.release() }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            ReelPageWithPlayer(
                reel = sampleReels[page],
                player = players[page],
                isActive = page == pagerState.currentPage
            )
        }

        // Top bar overlay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, null, tint = Color.White)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                listOf("Following", "For You").forEachIndexed { index, label ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { selectedTab = index }
                    ) {
                        Text(
                            label,
                            color = if (selectedTab == index) Color.White else Color.White.copy(alpha = 0.5f),
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 15.sp
                        )
                        if (selectedTab == index) {
                            Spacer(modifier = Modifier.height(3.dp))
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(2.dp)
                                    .background(Color.White, RoundedCornerShape(1.dp))
                            )
                        }
                    }
                }
            }

            IconButton(onClick = {}) {
                Icon(Icons.Default.CameraAlt, null, tint = Color.White)
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun ReelPageWithPlayer(
    reel: FirestorePost,
    player: ExoPlayer,
    isActive: Boolean
) {
    var isLiked by remember { mutableStateOf(false) }
    var likeCount by remember { mutableIntStateOf(reel.likes) }
    var isMuted by remember { mutableStateOf(false) }

    LaunchedEffect(isMuted) {
        player.volume = if (isMuted) 0f else 1f
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ── ExoPlayer Video Surface ────────────────────────────────────────
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PlayerView(ctx).apply {
                    this.player = player
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            update = { playerView ->
                playerView.player = player
            }
        )

        // ── Bottom gradient overlay ────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                )
        )

        // ── Right side action buttons ──────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 14.dp, bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(ElectricPurple)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    reel.authorName.firstOrNull()?.toString() ?: "?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            // Like
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable {
                isLiked = !isLiked
                likeCount = if (isLiked) likeCount + 1 else likeCount - 1
            }) {
                Icon(
                    if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    null, tint = if (isLiked) AccentRed else Color.White,
                    modifier = Modifier.size(30.dp)
                )
                Text(formatCount(likeCount), color = Color.White, fontSize = 12.sp)
            }

            // Comment
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.ChatBubbleOutline, null, tint = Color.White, modifier = Modifier.size(28.dp))
                Text(formatCount(reel.comments), color = Color.White, fontSize = 12.sp)
            }

            // Share
            Icon(Icons.Default.Send, null, tint = Color.White, modifier = Modifier.size(28.dp).clickable {})

            // Mute toggle
            IconButton(
                onClick = { isMuted = !isMuted },
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.Black.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(
                    if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                    null, tint = Color.White, modifier = Modifier.size(22.dp)
                )
            }

            // Spinning music disc
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(ElectricPurple, DarkBackground)))
                    .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MusicNote, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }

        // ── Bottom info ───────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 14.dp, end = 80.dp, bottom = 100.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(reel.authorName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.width(10.dp))
                OutlinedButton(
                    onClick = {},
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Color.White),
                    modifier = Modifier.height(28.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Text("Follow", color = Color.White, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                reel.content,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MusicNote, null, tint = Color.White, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(5.dp))
                Text("Original sound", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }
    }
}

private fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> "${count / 1_000_000}M"
        count >= 1_000 -> "${count / 1_000}.${(count % 1_000) / 100}K"
        else -> count.toString()
    }
}
