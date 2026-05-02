package com.elimusocial.app.ui.screens.social

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elimusocial.app.ui.theme.*

data class Reel(
    val id: String,
    val authorName: String,
    val authorUsername: String,
    val caption: String,
    val likes: Int,
    val comments: Int,
    val shares: Int,
    val sound: String,
    val bgColors: List<Color>
)

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun ReelsScreen(onBack: () -> Unit) {
    val reels = listOf(
        Reel("1", "Mary Wanjiku", "@mary", "Study hard, dream big, achieve more! ✨ #StudyTips #ElimuSocial", 2400, 156, 320, "Original sound", listOf(Color(0xFF1A0533), Color(0xFF0D1B4D))),
        Reel("2", "Brian Otieno", "@brian_dev", "Built a full-stack app in 30 days 🚀 Here's how #WebDev #Coding", 1800, 234, 189, "Coding vibes", listOf(Color(0xFF0A2818), Color(0xFF0D1B4D))),
        Reel("3", "Teacher Alex", "@teacher_alex", "5 study techniques that actually work 🧠 Save this! #Education", 5600, 890, 1200, "Study music", listOf(Color(0xFF2D1B00), Color(0xFF1A0533))),
        Reel("4", "Joyce Maina", "@joyce_m", "Campus life moments ✨ Nothing beats this view 🌅 #CampusLife", 3200, 445, 567, "Campus sound", listOf(Color(0xFF001A2D), Color(0xFF1A0533)))
    )

    val pagerState = rememberPagerState(pageCount = { reels.size })
    var selectedTab by remember { mutableStateOf(0) } // 0=Following, 1=ForYou

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            ReelItem(reel = reels[page])
        }

        // Top bar
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
                            Box(modifier = Modifier.width(20.dp).height(2.dp).background(Color.White, RoundedCornerShape(1.dp)))
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

@Composable
fun ReelItem(reel: Reel) {
    var isLiked by remember { mutableStateOf(false) }
    var likeCount by remember { mutableIntStateOf(reel.likes) }
    var isFollowing by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(reel.bgColors))
    ) {
        // Simulated video content - gradient background with play icon
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.PlayCircle,
                null,
                tint = Color.White.copy(alpha = 0.15f),
                modifier = Modifier.size(100.dp)
            )
        }

        // Bottom overlay gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                )
        )

        // Right side actions
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Author avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ElectricPurple)
                    .border(
                        BorderStroke(2.dp, Color.White),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(reel.authorName.first().toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            // Like
            ReelAction(
                icon = if (isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                count = likeCount,
                tint = if (isLiked) AccentRed else Color.White,
                onClick = {
                    isLiked = !isLiked
                    likeCount = if (isLiked) likeCount + 1 else likeCount - 1
                }
            )

            // Comment
            ReelAction(Icons.Outlined.ChatBubbleOutline, reel.comments, Color.White, {})

            // Share
            ReelAction(Icons.Default.Send, reel.shares, Color.White, {})

            // More
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.MoreVert, null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }

            // Spinning sound disc
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(listOf(ElectricPurple, DarkBackground))
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MusicNote, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }

        // Bottom info
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, end = 80.dp, bottom = 100.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    reel.authorName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(10.dp))
                OutlinedButton(
                    onClick = { isFollowing = !isFollowing },
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Color.White),
                    modifier = Modifier.height(28.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Text(
                        if (isFollowing) "Following" else "Follow",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                reel.caption,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MusicNote, null, tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(reel.sound, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun ReelAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(30.dp))
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = if (count >= 1000) "${count / 1000}.${(count % 1000) / 100}K" else count.toString(),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
