package com.elimusocial.app.ui.screens.social

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elimusocial.app.ui.theme.*

data class FollowUser(
    val id: String,
    val name: String,
    val handle: String,
    val bio: String = "",
    val isFollowingBack: Boolean = false,
    val isVerified: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowingFollowersScreen(
    initialTab: Int = 0,
    userName: String = "Antony Mwangi",
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    val tabs = listOf("Following", "Followers")

    val following = listOf(
        FollowUser("1", "John Kamau", "@johnkamau", "Software Engineer | Tech enthusiast"),
        FollowUser("2", "Mary Wanjiku", "@mary", "Student | Learning everyday 📚", isFollowingBack = true),
        FollowUser("3", "Brian Otieno", "@brian_dev", "Full Stack Developer | Open source", isVerified = true),
        FollowUser("4", "Joyce Maina", "@joyce_m", "UI/UX Designer | Creator"),
        FollowUser("5", "Alex Otieno", "@alex_tech", "CTO @ElimuSocial | Builder", isVerified = true, isFollowingBack = true),
    )

    val followers = listOf(
        FollowUser("1", "Mary Wanjiku", "@mary", "Student | Learning everyday 📚", isFollowingBack = true),
        FollowUser("2", "John Kamau", "@johnkamau", "Software Engineer | Tech enthusiast", isFollowingBack = true),
        FollowUser("3", "Sarah Kimani", "@sarah", "Teacher | Education advocate"),
        FollowUser("4", "Brian Otieno", "@brian_dev", "Full Stack Developer", isVerified = true, isFollowingBack = true),
        FollowUser("5", "Code Club", "@code_club", "Building the next generation of coders"),
        FollowUser("6", "Joyce Maina", "@joyce_m", "UI/UX Designer"),
        FollowUser("7", "Teacher Alex", "@teacher_alex", "CS Teacher | Mentor", isVerified = true),
    )

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) }
                },
                title = { Text(userName, fontWeight = FontWeight.Bold, color = TextPrimary) },
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

            val list = if (selectedTab == 0) following else followers
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                items(list, key = { it.id }) { user ->
                    FollowUserItem(user = user, isFollowingTab = selectedTab == 0)
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
fun FollowUserItem(user: FollowUser, isFollowingTab: Boolean) {
    var following by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier.fillMaxWidth().clickable {}.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(ElectricPurple), contentAlignment = Alignment.Center) {
            Text(user.name.firstOrNull()?.toString() ?: "?", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(user.name, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 15.sp)
                if (user.isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.Verified, null, tint = ElectricPurple, modifier = Modifier.size(14.dp))
                }
            }
            Text(user.handle, color = TextMuted, fontSize = 13.sp)
            if (user.bio.isNotEmpty()) {
                Text(user.bio, color = TextSecondary, fontSize = 12.sp, maxLines = 1)
            }
        }
        if (isFollowingTab) {
            OutlinedButton(
                onClick = { following = !following },
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.height(32.dp)
            ) { Text(if (following) "Following" else "Follow", fontSize = 12.sp, color = TextPrimary) }
        } else {
            Button(
                onClick = { following = !following },
                colors = ButtonDefaults.buttonColors(containerColor = if (user.isFollowingBack) DarkCard else ElectricPurple),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.height(32.dp)
            ) { Text(if (user.isFollowingBack) "Following" else "Follow back", fontSize = 12.sp, color = TextPrimary) }
        }
    }
}
