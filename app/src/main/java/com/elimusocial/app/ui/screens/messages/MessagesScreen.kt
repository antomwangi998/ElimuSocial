package com.elimusocial.app.ui.screens.messages

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elimusocial.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class Conversation(
    val id: String,
    val name: String,
    val lastMessage: String,
    val time: String,
    val unread: Int = 0,
    val isOnline: Boolean = false,
    val isGroup: Boolean = false,
    val avatarColor: Color = ElectricPurple
)

data class ChatMessage(
    val id: String,
    val text: String,
    val isMe: Boolean,
    val time: String,
    val status: String = "sent", // sent, delivered, read
    val type: String = "text", // text, image, file, voice
    val reactions: MutableList<String> = mutableListOf()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(onBack: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedConversation by remember { mutableStateOf<Conversation?>(null) }
    var showNewMessage by remember { mutableStateOf(false) }

    val conversations = remember {
        mutableStateListOf(
            Conversation("1", "Mary Wanjiku", "Hey! Are we still meeting...", "2m", unread = 2, isOnline = true, avatarColor = Color(0xFF9C27B0)),
            Conversation("2", "John Kamau", "Thanks! I got it 🙌", "1h", isOnline = true, avatarColor = Color(0xFF2196F3)),
            Conversation("3", "Teacher Alex", "Don't forget the assignment", "3h", avatarColor = Color(0xFF4CAF50)),
            Conversation("4", "Brian Otieno", "Yeah, see you there!", "5h", avatarColor = Color(0xFFFF9800)),
            Conversation("5", "Study Group 📚", "Sarah: I'll share the notes", "6h", unread = 5, isGroup = true),
            Conversation("6", "Joyce Maina", "Great session today! 🔥", "1d", avatarColor = Color(0xFFE91E63)),
            Conversation("7", "CS Hub 💻", "New discussion posted", "2d", isGroup = true),
            Conversation("8", "Alex Otieno", "Can you review my code?", "3d", isOnline = true, avatarColor = Color(0xFF00BCD4)),
        )
    }

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
                title = { Text("Messages", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 20.sp) },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.VideoCall, null, tint = TextSecondary) }
                    IconButton(onClick = { showNewMessage = true }) {
                        Icon(Icons.Default.Edit, null, tint = ElectricPurple)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Search
            OutlinedTextField(
                value = searchQuery, onValueChange = { searchQuery = it },
                placeholder = { Text("Search messages...", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = TextMuted, modifier = Modifier.size(18.dp)) },
                trailingIcon = { if (searchQuery.isNotEmpty()) IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Close, null, tint = TextMuted, modifier = Modifier.size(16.dp)) } },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricPurple, unfocusedBorderColor = DividerColor,
                    focusedContainerColor = DarkCard, unfocusedContainerColor = DarkCard,
                    focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
                ), singleLine = true
            )

            // Active contacts row
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable {}) {
                        Box(modifier = Modifier.size(58.dp).clip(CircleShape).background(DarkCard).border(2.dp, ElectricPurple, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.NoteAlt, null, tint = ElectricPurple, modifier = Modifier.size(26.dp))
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("Your note", color = TextMuted, fontSize = 10.sp)
                    }
                }
                items(conversations.filter { it.isOnline }) { conv ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { selectedConversation = conv }) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Box(modifier = Modifier.size(58.dp).clip(CircleShape).background(conv.avatarColor), contentAlignment = Alignment.Center) {
                                Text(conv.name.first().toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                            }
                            Box(modifier = Modifier.size(14.dp).background(AccentGreen, CircleShape).border(2.dp, DarkBackground, CircleShape))
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(conv.name.split(" ").first(), color = TextMuted, fontSize = 10.sp)
                    }
                }
            }
            HorizontalDivider(color = DividerColor)

            // Conversations list
            val filtered = if (searchQuery.isBlank()) conversations else conversations.filter { it.name.contains(searchQuery, ignoreCase = true) || it.lastMessage.contains(searchQuery, ignoreCase = true) }

            if (filtered.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("💬", fontSize = 48.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("No messages yet", color = TextMuted, fontSize = 16.sp)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { showNewMessage = true }, colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple), shape = RoundedCornerShape(20.dp)) {
                            Text("New Message")
                        }
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filtered, key = { it.id }) { conv ->
                        ConversationItem(conversation = conv, onClick = { selectedConversation = conv })
                        HorizontalDivider(color = DividerColor.copy(alpha = 0.5f), modifier = Modifier.padding(start = 80.dp))
                    }
                }
            }
        }
    }

    if (showNewMessage) {
        NewMessageDialog(
            onDismiss = { showNewMessage = false },
            onSelect = { name ->
                val newConv = Conversation(System.currentTimeMillis().toString(), name, "Say hi! 👋", "Now", isOnline = true)
                conversations.add(0, newConv)
                selectedConversation = newConv
                showNewMessage = false
            }
        )
    }
}

@Composable
fun NewMessageDialog(onDismiss: () -> Unit, onSelect: (String) -> Unit) {
    val contacts = listOf("Mary Wanjiku", "John Kamau", "Teacher Alex", "Brian Otieno", "Joyce Maina", "Alex Otieno", "Sarah Kimani")
    var search by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss, containerColor = DarkCard,
        title = { Text("New Message", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(value = search, onValueChange = { search = it },
                    placeholder = { Text("Search people...", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricPurple, unfocusedBorderColor = DividerColor, focusedContainerColor = DarkBackground, unfocusedContainerColor = DarkBackground, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = ElectricPurple),
                    singleLine = true)
                Spacer(Modifier.height(12.dp))
                contacts.filter { it.contains(search, ignoreCase = true) }.forEach { name ->
                    Row(modifier = Modifier.fillMaxWidth().clickable { onSelect(name) }.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(ElectricPurple), contentAlignment = Alignment.Center) {
                            Text(name.first().toString(), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(name, color = TextPrimary, fontSize = 15.sp)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) } }
    )
}

@Composable
fun ConversationItem(conversation: Conversation, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(modifier = Modifier.size(54.dp).clip(CircleShape).background(if (conversation.isGroup) Brush.linearGradient(listOf(ElectricPurple, AccentBlue)) else Brush.linearGradient(listOf(conversation.avatarColor, conversation.avatarColor))), contentAlignment = Alignment.Center) {
                if (conversation.isGroup) Icon(Icons.Default.Group, null, tint = Color.White, modifier = Modifier.size(26.dp))
                else Text(conversation.name.first().toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            if (conversation.isOnline) Box(modifier = Modifier.size(14.dp).background(AccentGreen, CircleShape).border(2.dp, DarkBackground, CircleShape))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(conversation.name, color = TextPrimary, fontWeight = if (conversation.unread > 0) FontWeight.Bold else FontWeight.Normal, fontSize = 15.sp)
                Text(conversation.time, color = if (conversation.unread > 0) ElectricPurple else TextMuted, fontSize = 12.sp)
            }
            Spacer(Modifier.height(3.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(conversation.lastMessage, color = if (conversation.unread > 0) TextSecondary else TextMuted, fontSize = 13.sp, maxLines = 1, modifier = Modifier.weight(1f))
                if (conversation.unread > 0) {
                    Spacer(Modifier.width(8.dp))
                    Box(modifier = Modifier.size(20.dp).background(ElectricPurple, CircleShape), contentAlignment = Alignment.Center) {
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
    var messageText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    var showAttachMenu by remember { mutableStateOf(false) }
    var showEmojiPicker by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val messages = remember {
        mutableStateListOf(
            ChatMessage("1", "Hey! Are we still meeting today?", false, "10:30 AM"),
            ChatMessage("2", "Yes! At 3 PM. Library room 2 🙌", true, "10:31 AM", "read"),
            ChatMessage("3", "Perfect! I'll bring my notes 📚", false, "10:33 AM"),
            ChatMessage("4", "Great! Should I bring snacks? 😄", true, "10:35 AM", "read"),
            ChatMessage("5", "Definitely! And coffee ☕", false, "10:36 AM"),
            ChatMessage("6", "See you at 3! 🎉", true, "10:40 AM", "delivered"),
        )
    }

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { messages.add(ChatMessage(System.currentTimeMillis().toString(), "📷 Photo", true, "Now", type = "image")) }
    }

    val fileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { messages.add(ChatMessage(System.currentTimeMillis().toString(), "📎 File attachment", true, "Now", type = "file")) }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    // Simulate other person typing
    LaunchedEffect(messageText) {
        if (messageText.isNotEmpty()) {
            delay(1500)
            isTyping = true
            delay(2000)
            isTyping = false
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {}) {
                        Box(modifier = Modifier.size(38.dp).clip(CircleShape).background(conversation.avatarColor), contentAlignment = Alignment.Center) {
                            if (conversation.isGroup) Icon(Icons.Default.Group, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            else Text(conversation.name.first().toString(), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(conversation.name, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            if (isTyping) Text("typing...", color = AccentGreen, fontSize = 11.sp)
                            else if (conversation.isOnline) Text("Online", color = AccentGreen, fontSize = 11.sp)
                            else if (conversation.isGroup) Text("${(3..15).random()} members", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Call, null, tint = TextSecondary) }
                    IconButton(onClick = {}) { Icon(Icons.Default.VideoCall, null, tint = TextSecondary) }
                    IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, null, tint = TextSecondary) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(state = listState, modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {

                // Date separator
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Box(modifier = Modifier.background(DarkCard, RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 4.dp)) {
                            Text("Today", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }

                items(messages, key = { it.id }) { msg ->
                    ChatBubble(message = msg, onReaction = { emoji ->
                        val idx = messages.indexOf(msg)
                        if (idx >= 0) messages[idx] = msg.copy(reactions = (msg.reactions + emoji).toMutableList())
                    })
                }

                if (isTyping) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(30.dp).clip(CircleShape).background(conversation.avatarColor), contentAlignment = Alignment.Center) {
                                Text(conversation.name.first().toString(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(8.dp))
                            Box(modifier = Modifier.background(DarkCard, RoundedCornerShape(18.dp)).padding(horizontal = 14.dp, vertical = 10.dp)) {
                                Text("• • •", color = TextMuted, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }

            // Attach menu
            if (showAttachMenu) {
                Row(modifier = Modifier.fillMaxWidth().background(DarkSurface).padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    AttachOption("📷", "Photo") { imageLauncher.launch("image/*"); showAttachMenu = false }
                    AttachOption("🎥", "Video") { showAttachMenu = false }
                    AttachOption("📎", "File") { fileLauncher.launch("*/*"); showAttachMenu = false }
                    AttachOption("📍", "Location") { showAttachMenu = false }
                    AttachOption("🎵", "Audio") { showAttachMenu = false }
                    AttachOption("📊", "Poll") { showAttachMenu = false }
                }
            }

            // Input row
            Row(modifier = Modifier.fillMaxWidth().background(DarkSurface).padding(horizontal = 10.dp, vertical = 8.dp).imePadding(),
                verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = { showAttachMenu = !showAttachMenu }) {
                    Icon(if (showAttachMenu) Icons.Default.Close else Icons.Default.Add, null, tint = TextMuted)
                }
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Message...", color = TextMuted, fontSize = 13.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricPurple, unfocusedBorderColor = DividerColor,
                        focusedContainerColor = DarkCard, unfocusedContainerColor = DarkCard,
                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = ElectricPurple
                    ),
                    maxLines = 4
                )
                if (messageText.isBlank()) {
                    IconButton(onClick = {}, modifier = Modifier.size(44.dp).background(DarkCard, CircleShape)) {
                        Icon(Icons.Default.Mic, null, tint = ElectricPurple)
                    }
                } else {
                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                messages.add(ChatMessage(System.currentTimeMillis().toString(), messageText, true, "Now", "sent"))
                                messageText = ""
                                scope.launch { delay(100); listState.animateScrollToItem(messages.size - 1) }
                            }
                        },
                        modifier = Modifier.size(44.dp).background(ElectricPurple, CircleShape)
                    ) { Icon(Icons.Default.Send, null, tint = Color.White, modifier = Modifier.size(20.dp)) }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage, onReaction: (String) -> Unit) {
    var showReactions by remember { mutableStateOf(false) }
    val emojis = listOf("❤️", "😂", "😮", "😢", "👍", "🔥")

    Column(modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isMe) Alignment.End else Alignment.Start) {
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = if (message.isMe) Arrangement.End else Arrangement.Start) {
            if (!message.isMe) {
                Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(ElectricPurple), contentAlignment = Alignment.Center) {
                    Text("U", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(6.dp))
            }
            Column(horizontalAlignment = if (message.isMe) Alignment.End else Alignment.Start) {
                Box(modifier = Modifier.widthIn(max = 260.dp)
                    .clip(RoundedCornerShape(topStart = if (message.isMe) 18.dp else 4.dp, topEnd = if (message.isMe) 4.dp else 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp))
                    .background(if (message.isMe) ElectricPurple else DarkCard)
                    .combinedClickable(onClick = {}, onLongClick = { showReactions = !showReactions })
                    .padding(horizontal = 14.dp, vertical = 10.dp)) {
                    Text(message.text, color = if (message.isMe) Color.White else TextPrimary, fontSize = 14.sp, lineHeight = 20.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(message.time, color = TextMuted, fontSize = 10.sp)
                    if (message.isMe) {
                        Icon(when (message.status) {
                            "read" -> Icons.Default.DoneAll
                            "delivered" -> Icons.Default.DoneAll
                            else -> Icons.Default.Done
                        }, null, tint = if (message.status == "read") AccentBlue else TextMuted, modifier = Modifier.size(12.dp))
                    }
                }
                // Reactions
                if (message.reactions.isNotEmpty()) {
                    Box(modifier = Modifier.background(DarkCard, RoundedCornerShape(12.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                        Text(message.reactions.distinct().joinToString(" "), fontSize = 14.sp)
                    }
                }
            }
        }
        // Reaction picker
        if (showReactions) {
            Row(modifier = Modifier.background(DarkCard, RoundedCornerShape(20.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                emojis.forEach { emoji ->
                    Text(emoji, fontSize = 22.sp, modifier = Modifier.clickable { onReaction(emoji); showReactions = false }.padding(4.dp))
                }
            }
        }
    }
}

@Composable
fun AttachOption(emoji: String, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Box(modifier = Modifier.size(46.dp).clip(CircleShape).background(DarkCard), contentAlignment = Alignment.Center) {
            Text(emoji, fontSize = 22.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text(label, color = TextMuted, fontSize = 10.sp)
    }
}
