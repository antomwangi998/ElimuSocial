package com.elimusocial.app.ui.screens.messages

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elimusocial.app.data.models.SampleData
import com.elimusocial.app.ui.theme.*

data class Conversation(
    val id: String,
    val name: String,
    val lastMessage: String,
    val time: String,
    val unread: Int = 0,
    val isOnline: Boolean = false,
    val isGroup: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(onBack: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedConversation by remember { mutableStateOf<Conversation?>(null) }

    val conversations = listOf(
        Conversation("1", "Mary Wanjiku", "Hey! Are we still meeting...", "2m", unread = 2, isOnline = true),
        Conversation("2", "John Kamau", "Thanks! I got it 🙌", "1h", isOnline = true),
        Conversation("3", "Teacher Alex", "Don't forget the assignment", "3h"),
        Conversation("4", "Brian Otieno", "Yeah, see you there!", "5h"),
        Conversation("5", "Study Group", "Sarah: I'll share the notes", "6h", unread = 5, isGroup = true),
        Conversation("6", "Joyce Maina", "Great session today! 🔥", "1d"),
        Conversation("7", "CS Hub", "New discussion posted", "2d", isGroup = true)
    )

    if (selectedConversation != null) {
        ChatDetailScreen(
            conversation = selectedConversation!!,
            onBack = { selectedConversation = null }
        )
        return
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) }
                },
                title = { Text("Messages", fontWeight = FontWeight.Bold, color = TextPrimary) },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Edit, null, tint = ElectricPurple) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search messages...", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = TextMuted, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricPurple,
                    unfocusedBorderColor = DividerColor,
                    focusedContainerColor = DarkCard,
                    unfocusedContainerColor = DarkCard,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )

            // Online contacts row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Your note
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier.size(56.dp).clip(CircleShape).background(DarkCard).border(2.dp, DividerColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Note, null, tint = ElectricPurple, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Your note", color = TextMuted, fontSize = 10.sp)
                    }
                }
                items(SampleData.users.take(4)) { user ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Box(
                                modifier = Modifier.size(56.dp).clip(CircleShape).background(ElectricPurple),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(user.name.first().toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                            Box(modifier = Modifier.size(14.dp).background(AccentGreen, CircleShape).border(2.dp, DarkBackground, CircleShape))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(user.name.split(" ").first(), color = TextMuted, fontSize = 10.sp)
                    }
                }
            }

            HorizontalDivider(color = DividerColor)

            // Conversations list
            val filtered = if (searchQuery.isBlank()) conversations
            else conversations.filter { it.name.contains(searchQuery, ignoreCase = true) }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filtered) { conversation ->
                    ConversationItem(conversation = conversation, onClick = { selectedConversation = conversation })
                    HorizontalDivider(color = DividerColor.copy(alpha = 0.5f), modifier = Modifier.padding(start = 80.dp))
                }
            }
        }
    }
}

@Composable
fun ConversationItem(conversation: Conversation, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier.size(52.dp).clip(CircleShape).background(
                    if (conversation.isGroup) Brush.linearGradient(listOf(ElectricPurple, AccentBlue))
                    else Brush.linearGradient(listOf(ElectricPurple, ElectricPurple))
                ),
                contentAlignment = Alignment.Center
            ) {
                Text(conversation.name.first().toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            if (conversation.isOnline) {
                Box(modifier = Modifier.size(14.dp).background(AccentGreen, CircleShape).border(2.dp, DarkBackground, CircleShape))
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    conversation.name,
                    color = TextPrimary,
                    fontWeight = if (conversation.unread > 0) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 15.sp
                )
                Text(
                    conversation.time,
                    color = if (conversation.unread > 0) ElectricPurple else TextMuted,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    conversation.lastMessage,
                    color = if (conversation.unread > 0) TextSecondary else TextMuted,
                    fontSize = 13.sp,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                if (conversation.unread > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier.size(20.dp).background(ElectricPurple, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(conversation.unread.toString(), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(conversation: Conversation, onBack: () -> Unit) {
    var message by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            Pair(false, "Hey! Are we still meeting today?"),
            Pair(true, "Yes! At 3 PM. Library room 2 🙌"),
            Pair(false, "Perfect! I'll bring my notes"),
            Pair(true, "Great! See you then 😊")
        )
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(ElectricPurple),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(conversation.name.first().toString(), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(conversation.name, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            if (conversation.isOnline) Text("Online", color = AccentGreen, fontSize = 11.sp)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Call, null, tint = TextSecondary) }
                    IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, null, tint = TextSecondary) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { (isMe, text) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .widthIn(max = 260.dp)
                                .clip(RoundedCornerShape(
                                    topStart = if (isMe) 18.dp else 4.dp,
                                    topEnd = if (isMe) 4.dp else 18.dp,
                                    bottomStart = 18.dp, bottomEnd = 18.dp
                                ))
                                .background(if (isMe) ElectricPurple else DarkCard)
                                .padding(12.dp)
                        ) {
                            Text(text, color = if (isMe) Color.White else TextPrimary, fontSize = 14.sp)
                        }
                    }
                }
            }

            // Input
            Row(
                modifier = Modifier.fillMaxWidth().background(DarkSurface).padding(horizontal = 12.dp, vertical = 10.dp).padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = {}) { Icon(Icons.Default.AttachFile, null, tint = TextMuted) }
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    placeholder = { Text("Message...", color = TextMuted, fontSize = 13.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricPurple, unfocusedBorderColor = DividerColor,
                        focusedContainerColor = DarkCard, unfocusedContainerColor = DarkCard,
                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )
                IconButton(
                    onClick = { if (message.isNotBlank()) { messages.add(Pair(true, message)); message = "" } },
                    modifier = Modifier.size(44.dp).background(ElectricPurple, CircleShape)
                ) {
                    Icon(Icons.Default.Send, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
