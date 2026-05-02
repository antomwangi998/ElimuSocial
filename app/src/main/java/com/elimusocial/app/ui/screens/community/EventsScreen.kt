package com.elimusocial.app.ui.screens.community

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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

data class Event(
    val id: String,
    val title: String,
    val date: String,
    val month: String,
    val day: Int,
    val time: String,
    val location: String,
    val category: String,
    val color: Color,
    val isGoing: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Upcoming", "Past", "Your Events")

    val events = listOf(
        Event("1", "Tech Hackathon 2024", "MAY", "MAY", 24, "9:00 AM – 6:00 PM", "KICC, Nairobi", "Tech", ElectricPurple, isGoing = true),
        Event("2", "Inter-School Debate Championship", "MAY", "MAY", 31, "10:00 AM – 2:00 PM", "Alliance High School", "Education", AccentBlue),
        Event("3", "Career Day", "JUN", "JUN", 7, "9:00 AM – 4:00 PM", "Strathmore University", "Career", AccentGreen),
        Event("4", "AI & Machine Learning Workshop", "JUN", "JUN", 14, "10:00 AM – 5:00 PM", "iHub, Nairobi", "Tech", Color(0xFFEC4899)),
        Event("5", "Entrepreneurship Summit", "JUN", "JUN", 21, "8:00 AM – 6:00 PM", "KICC, Nairobi", "Business", AccentOrange)
    )

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) }
                },
                title = { Text("Events", fontWeight = FontWeight.Bold, color = TextPrimary) },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Search, null, tint = TextSecondary) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {},
                containerColor = ElectricPurple,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, null, tint = Color.White) },
                text = { Text("Create Event", color = Color.White, fontWeight = FontWeight.SemiBold) }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Tabs
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
                tabs.forEachIndexed { i, tab ->
                    Tab(
                        selected = selectedTab == i,
                        onClick = { selectedTab = i },
                        text = { Text(tab, color = if (selectedTab == i) ElectricPurple else TextMuted, fontSize = 13.sp) }
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(events) { event ->
                    EventCard(event = event)
                }
                item { Spacer(modifier = Modifier.height(60.dp)) }
            }
        }
    }
}

@Composable
fun EventCard(event: Event) {
    var isGoing by remember { mutableStateOf(event.isGoing) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkCard, RoundedCornerShape(16.dp))
            .clickable {}
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Date badge
        Box(
            modifier = Modifier
                .width(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(event.color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 10.dp)
            ) {
                Text(event.month, color = event.color, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text(event.day.toString(), color = event.color, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Category chip
            Box(
                modifier = Modifier
                    .background(event.color.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(event.category, color = event.color, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(event.title, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, lineHeight = 20.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.AccessTime, null, tint = TextMuted, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(event.time, color = TextMuted, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.LocationOn, null, tint = TextMuted, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(event.location, color = TextMuted, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { isGoing = !isGoing },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(34.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isGoing) AccentGreen else ElectricPurple
                )
            ) {
                Icon(
                    if (isGoing) Icons.Default.CheckCircle else Icons.Default.AddCircleOutline,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(if (isGoing) "Going" else "Attend", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
