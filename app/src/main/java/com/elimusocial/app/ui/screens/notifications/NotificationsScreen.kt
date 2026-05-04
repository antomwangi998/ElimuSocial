package com.elimusocial.app.ui.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elimusocial.app.ui.theme.*

data class AppNotification(
    val id: String,
    val type: String, // like, comment, follow, mention, repost, system
    val userName: String,
    val message: String,
    val time: String,
    val isRead: Boolean = false,
    val postPreview: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "Mentions", "Replies", "Follows")

    val allNotifications = listOf(
        AppNotification("1", "like", "John Kamau", "liked your post", "2m", postPreview = "Just finished our computer science project! 🔥"),
        AppNotification("2", "comment", "Mary Wanjiku", "commented on your post: \"Great work team! 🔥🔥\"", "5m", postPreview = "Teamwork makes the dream work"),
        AppNotification("3", "repost", "Teacher Alex", "reposted your post", "10m"),
        AppNotification("4", "follow", "Brian Otieno", "started following you", "15m"),
        AppNotification("5", "mention", "Sarah Kimani", "mentioned you in a post: \"@antony check this out!\"", "20m"),
        AppNotification("6", "system", "Elimu Social", "Welcome to Elimu Social! 🎉 Start connecting with students.", "1h"),
        AppNotification("7", "like", "Joyce Maina", "liked your reply", "2h", isRead = true),
        AppNotification("8", "comment", "Alex Otieno", "replied to your comment", "3h", isRead = true),
        AppNotification("9", "follow", "Tech Hub", "started following you", "1d", isRead = true),
        AppNotification("10", "mention", "Brian Otieno", "mentioned you in a study group post", "2d", isRead = true),
    )

    val filtered = when (selectedTab) {
        1 -> allNotifications.filter { it.type == "mention" }
        2 -> allNotifications.filter { it.type == "comment" }
        3 -> allNotifications.filter { it.type == "follow" }
        else -> allNotifications
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = TextPrimary)
                    }
                },
                title = { Text("Notifications", fontWeight = FontWeight.Bold, color = TextPrimary) },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.DoneAll, null, tint = ElectricPurple)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkBackground,
                contentColor = ElectricPurple,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        Box(modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]).height(2.dp).background(ElectricPurple))
                    }
                }
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(tab, color = if (selectedTab == index) ElectricPurple else TextMuted, fontSize = 14.sp) }
                    )
                }
            }
            HorizontalDivider(color = DividerColor, thickness = 0.5.dp)

            if (filtered.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔔", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No notifications yet", color = TextMuted, fontSize = 16.sp)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filtered, key = { it.id }) { notification ->
                        NotificationItem(notification = notification)
                        HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: AppNotification) {
    val bgColor = if (!notification.isRead) ElectricPurple.copy(alpha = 0.05f) else Color.Transparent
    val (icon, iconColor) = when (notification.type) {
        "like" -> Icons.Default.Favorite to AccentRed
        "comment" -> Icons.Default.ChatBubble to AccentBlue
        "follow" -> Icons.Default.PersonAdd to AccentGreen
        "mention" -> Icons.Default.AlternateEmail to ElectricPurple
        "repost" -> Icons.Default.Repeat to AccentGreen
        else -> Icons.Default.Notifications to ElectricPurple
    }

    Row(
        modifier = Modifier.fillMaxWidth().background(bgColor).clickable {}.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Avatar with icon badge
        Box(modifier = Modifier.size(46.dp)) {
            Box(
                modifier = Modifier.size(42.dp).clip(CircleShape).background(DarkCard),
                contentAlignment = Alignment.Center
            ) {
                Text(notification.userName.firstOrNull()?.toString() ?: "?", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Box(
                modifier = Modifier.size(18.dp).clip(CircleShape).background(iconColor).align(Alignment.BottomEnd),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(11.dp))
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row {
                Text(notification.userName, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 14.sp)
                Text(" ${notification.message}", color = TextSecondary, fontSize = 14.sp)
            }
            if (notification.postPreview.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(notification.postPreview, color = TextMuted, fontSize = 12.sp, maxLines = 1)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(notification.time, color = TextMuted, fontSize = 12.sp)
        }
        if (!notification.isRead) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ElectricPurple))
        }
        if (notification.type == "follow") {
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                modifier = Modifier.height(32.dp)
            ) { Text("Follow", fontSize = 12.sp, color = TextPrimary) }
        }
    }
}
