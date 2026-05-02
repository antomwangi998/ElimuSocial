package com.elimusocial.app.ui.screens.learning

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
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String,
    val content: String,
    val isUser: Boolean,
    val timestamp: String = ""
)

data class AiSuggestion(val icon: String, val title: String, val subtitle: String, val color: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElimuAiScreen(onBack: () -> Unit) {
    var inputText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val messages = remember {
        mutableStateListOf(
            ChatMessage("0", "Hello Antony! 👋\nHow can I help you today?", isUser = false, timestamp = "")
        )
    }

    val suggestions = listOf(
        AiSuggestion("📝", "Summarize this topic", "Get quick summaries of any text or PDF", ElectricPurple),
        AiSuggestion("💡", "Explain a concept", "Get easy explanations for difficult topics", AccentBlue),
        AiSuggestion("✍️", "Generate content", "Create posts, notes, captions & more", AccentOrange),
        AiSuggestion("📅", "Study planner", "Plan your study & achieve your goals", AccentGreen)
    )

    val quickReplies = listOf("Summarize a topic", "Explain photosynthesis", "Help me study", "Create study notes")

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        messages.add(ChatMessage(messages.size.toString(), text, isUser = true))
        inputText = ""
        isTyping = true

        scope.launch {
            listState.animateScrollToItem(messages.size - 1)
            delay(1500)
            isTyping = false
            val response = when {
                text.contains("photosynthesis", ignoreCase = true) ->
                    "Photosynthesis is the process by which green plants convert sunlight into food. 🌱\n\n**Formula:** 6CO₂ + 6H₂O + light → C₆H₁₂O₆ + 6O₂\n\nPlants absorb sunlight through chlorophyll, use CO₂ from air and water from roots to produce glucose for energy."
                text.contains("summarize", ignoreCase = true) ->
                    "Sure! Please share the text, PDF, or topic you'd like me to summarize. I can handle:\n• Study notes\n• Articles\n• Textbook chapters\n• Research papers 📄"
                text.contains("study", ignoreCase = true) ->
                    "I'd love to help you study! 📚 Here's what I can do:\n\n1. **Create flashcards** from your notes\n2. **Quiz you** on any topic\n3. **Build a study schedule**\n4. **Explain difficult concepts**\n\nWhat subject are you studying?"
                else ->
                    "Great question! I'm Elimu AI, your smart study buddy. 🤖✨\n\nI can help you with summaries, explanations, study planning, content creation, and much more. What would you like to explore today?"
            }
            messages.add(ChatMessage(messages.size.toString(), response, isUser = false))
            listState.animateScrollToItem(messages.size - 1)
        }
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
                            modifier = Modifier
                                .size(36.dp)
                                .background(Brush.linearGradient(listOf(ElectricPurple, AccentBlue)), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("AI", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Elimu AI", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Your smart study buddy", color = AccentGreen, fontSize = 11.sp)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Tune, null, tint = TextSecondary) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Messages list
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Suggestion chips (shown before first user message)
                if (messages.size == 1) {
                    items(suggestions) { suggestion ->
                        AiSuggestionCard(suggestion = suggestion, onClick = { sendMessage(suggestion.title) })
                    }
                }

                items(messages) { message ->
                    AiChatBubble(message = message)
                }

                if (isTyping) {
                    item { TypingIndicator() }
                }
            }

            // Quick reply chips
            if (messages.size <= 3) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickReplies.forEach { reply ->
                        SuggestionChip(
                            onClick = { sendMessage(reply) },
                            label = { Text(reply, fontSize = 12.sp, color = LightPurple) },
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                enabled = true,
                                borderColor = ElectricPurple.copy(alpha = 0.5f)
                            ),
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = ElectricPurple.copy(alpha = 0.1f))
                        )
                    }
                }
            }

            // Input bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask Elimu AI anything...", color = TextMuted, fontSize = 13.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricPurple,
                        unfocusedBorderColor = DividerColor,
                        focusedContainerColor = DarkCard,
                        unfocusedContainerColor = DarkCard,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 3,
                    trailingIcon = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Mic, null, tint = TextMuted, modifier = Modifier.size(20.dp))
                        }
                    }
                )

                IconButton(
                    onClick = { sendMessage(inputText) },
                    enabled = inputText.isNotBlank(),
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            if (inputText.isNotBlank()) ElectricPurple else DarkCard,
                            CircleShape
                        )
                ) {
                    Icon(Icons.Default.Send, null, tint = if (inputText.isNotBlank()) Color.White else TextMuted, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun AiSuggestionCard(suggestion: AiSuggestion, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkCard, RoundedCornerShape(14.dp))
            .border(1.dp, suggestion.color.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(suggestion.color.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(suggestion.icon, fontSize = 22.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(suggestion.title, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(suggestion.subtitle, color = TextMuted, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, null, tint = TextMuted, modifier = Modifier.size(18.dp))
    }
}

@Composable
fun AiChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Brush.linearGradient(listOf(ElectricPurple, AccentBlue)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("AI", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = if (message.isUser) 18.dp else 4.dp,
                        topEnd = if (message.isUser) 4.dp else 18.dp,
                        bottomStart = 18.dp,
                        bottomEnd = 18.dp
                    )
                )
                .background(
                    if (message.isUser)
                        Brush.linearGradient(listOf(ElectricPurple, AccentBlue))
                    else
                        Brush.linearGradient(listOf(DarkCard, DarkCard))
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.content,
                color = if (message.isUser) Color.White else TextPrimary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Brush.linearGradient(listOf(ElectricPurple, AccentBlue)), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("AI", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .background(DarkCard, RoundedCornerShape(18.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(3) { index ->
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            tween(400, delayMillis = index * 150),
                            RepeatMode.Reverse
                        ),
                        label = "dot$index"
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(ElectricPurple.copy(alpha = alpha), CircleShape)
                    )
                }
            }
        }
    }
}
