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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elimusocial.app.ui.theme.*

// Used in HomeScreen notifications tab
@Composable
fun NotificationsContent(paddingValues: PaddingValues) {
    NotificationsBody(paddingValues = paddingValues)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                title = { Text("Notifications", fontWeight = FontWeight.Bold, color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        NotificationsBody(paddingValues = padding)
    }
}

@Composable
fun NotificationsBody(paddingValues: PaddingValues) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "Mentions", "Replies", "Follows")

    data class Notif(
        val id: String, val type: String, val user: String,
        val message: String, val time: String,
        var isRead: Boolean = false, val postPreview: String = ""
    )

    val notifications = remember {
        mutableStateListOf(
            Notif("1", "like", "John Kamau", "liked your post", "2m", postPreview = "Just finished our CS project! 🔥"),
            Notif("2", "comment", "Mary Wanjiku", "commented: \"Great work team! 🔥\"", "5m", postPreview = "Teamwork makes the dream work"),
            Notif("3", "repost", "Teacher Alex", "reposted your post", "10m"),
            Notif("4", "follow", "Brian Otieno", "started following you", "15m"),
            Notif("5", "mention", "Sarah Kimani", "mentioned you: \"@antony check this!\"", "20m"),
            Notif("6", "like", "Joyce Maina", "and 12 others liked your post", "1h", isRead = true, postPreview = "Discipline today, freedom tomorrow"),
            Notif("7", "system", "Elimu Social", "Welcome to Elimu Social! 🎉", "2h", isRead = true),
            Notif("8", "comment", "Alex Otieno", "replied to your comment", "3h", isRead = true),
            Notif("9", "follow", "Code Club", "started following you", "1d", isRead = true),
            Notif("10", "mention", "Brian Otieno", "mentioned you in Study Group", "2d", isRead = true),
        )
    }

    val filtered = when (selectedTab) {
        1 -> notifications.filter { it.type == "mention" }
        2 -> notifications.filter { it.type == "comment" }
        3 -> notifications.filter { it.type == "follow" }
        else -> notifications
    }

    Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        // Mark all read button
        if (notifications.any { !it.isRead }) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { notifications.forEachIndexed { i, n -> notifications[i] = n.copy(isRead = true) } }) {
                    Icon(Icons.Default.DoneAll, null, tint = ElectricPurple, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Mark all read", color = ElectricPurple, fontSize = 13.sp)
                }
            }
        }

        TabRow(
            selectedTabIndex = selectedTab, containerColor = DarkBackground, contentColor = ElectricPurple,
            indicator = { tabPositions ->
                if (selectedTab < tabPositions.size)
                    Box(modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]).height(2.dp).background(ElectricPurple))
            }
        ) {
            tabs.forEachIndexed { i, tab ->
                Tab(selected = selectedTab == i, onClick = { selectedTab = i },
                    text = { Text(tab, color = if (selectedTab == i) ElectricPurple else TextMuted, fontSize = 14.sp) })
            }
        }
        HorizontalDivider(color = DividerColor, thickness = 0.5.dp)

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔔", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("No notifications", color = TextMuted, fontSize = 16.sp)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filtered, key = { it.id }) { notif ->
                    val idx = notifications.indexOf(notif)
                    NotifItem(notif = notif, onRead = { if (idx >= 0) notifications[idx] = notif.copy(isRead = true) })
                    HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
fun NotifItem(notif: Any, onRead: () -> Unit) {
    // Using dynamic approach to avoid data class redefinition issues
    data class N(val id: String, val type: String, val user: String, val message: String, val time: String, val isRead: Boolean, val postPreview: String)
    @Suppress("UNCHECKED_CAST")
    val n = notif as? N ?: run {
        // fallback render
        val fields = notif.javaClass.declaredFields.also { f -> f.forEach { it.isAccessible = true } }
        val get = { name: String -> fields.find { it.name == name }?.get(notif)?.toString() ?: "" }
        N(get("id"), get("type"), get("user"), get("message"), get("time"), get("isRead") == "true", get("postPreview"))
    }

    val (iconRes, iconColor) = when (n.type) {
        "like" -> Icons.Default.Favorite to AccentRed
        "comment" -> Icons.Default.ChatBubble to AccentBlue
        "follow" -> Icons.Default.PersonAdd to AccentGreen
        "mention" -> Icons.Default.AlternateEmail to ElectricPurple
        "repost" -> Icons.Default.Repeat to AccentGreen
        else -> Icons.Default.Notifications to ElectricPurple
    }

    var followedBack by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth()
            .background(if (!n.isRead) ElectricPurple.copy(alpha = 0.04f) else Color.Transparent)
            .clickable { onRead() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Avatar with badge
        Box(modifier = Modifier.size(46.dp)) {
            Box(modifier = Modifier.size(42.dp).clip(CircleShape).background(
                when (n.type) {
                    "follow" -> AccentGreen.copy(alpha = 0.2f)
                    "like" -> AccentRed.copy(alpha = 0.2f)
                    "mention" -> ElectricPurple.copy(alpha = 0.2f)
                    else -> DarkCard
                }
            ), contentAlignment = Alignment.Center) {
                Text(n.user.firstOrNull()?.toString() ?: "?", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Box(modifier = Modifier.size(18.dp).clip(CircleShape).background(iconColor).align(Alignment.BottomEnd), contentAlignment = Alignment.Center) {
                Icon(iconRes, null, tint = Color.White, modifier = Modifier.size(10.dp))
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(n.user, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 14.sp, modifier = Modifier.weight(1f))
                Text(n.time, color = TextMuted, fontSize = 12.sp)
            }
            Text(n.message, color = TextSecondary, fontSize = 13.sp)
            if (n.postPreview.isNotEmpty()) {
                Spacer(Modifier.height(3.dp))
                Text(n.postPreview, color = TextMuted, fontSize = 12.sp, maxLines = 1)
            }
            if (n.type == "follow") {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { followedBack = !followedBack },
                    colors = ButtonDefaults.buttonColors(containerColor = if (followedBack) DarkCard else ElectricPurple),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    modifier = Modifier.height(32.dp),
                    border = if (followedBack) androidx.compose.foundation.BorderStroke(1.dp, DividerColor) else null
                ) { Text(if (followedBack) "Following" else "Follow back", fontSize = 12.sp, color = TextPrimary) }
            }
        }
        if (!n.isRead) {
            Spacer(Modifier.width(8.dp))
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ElectricPurple))
        }
    }
}
