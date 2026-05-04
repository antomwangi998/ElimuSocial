package com.elimusocial.app.ui.screens.social

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elimusocial.app.ui.theme.*

data class PollOption(val text: String, val votes: Int)
data class Poll(
    val id: String,
    val author: String,
    val handle: String,
    val question: String,
    val options: List<PollOption>,
    val totalVotes: Int,
    val isFinal: Boolean = false,
    val timeLeft: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollsScreen(onBack: () -> Unit) {
    val polls = listOf(
        Poll(
            "1", "Teacher Alex", "@teacher_alex",
            "Which topic should we cover next week?",
            listOf(
                PollOption("Artificial Intelligence", 102),
                PollOption("Web Development", 85),
                PollOption("Cyber Security", 32),
                PollOption("Data Science", 22)
            ),
            totalVotes = 241,
            isFinal = true
        ),
        Poll(
            "2", "Elimu Social", "@elimusocial",
            "What's your preferred study time?",
            listOf(
                PollOption("Morning (6AM - 12PM)", 0),
                PollOption("Afternoon (12PM - 6PM)", 0),
                PollOption("Evening (6PM - 10PM)", 0),
                PollOption("Night (10PM+)", 0)
            ),
            totalVotes = 0,
            timeLeft = "2 days left"
        ),
        Poll(
            "3", "Brian Otieno", "@brian_dev",
            "Best programming language to learn first?",
            listOf(
                PollOption("Python", 0),
                PollOption("JavaScript", 0),
                PollOption("Java", 0),
                PollOption("C++", 0)
            ),
            totalVotes = 0,
            timeLeft = "1 day left"
        )
    )

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                title = { Text("Polls", fontWeight = FontWeight.Bold, color = TextPrimary) },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Add, null, tint = ElectricPurple) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(polls.size) { index ->
                PollCard(poll = polls[index])
            }
        }
    }
}

@Composable
fun PollCard(poll: Poll) {
    var votedOption by remember { mutableStateOf<Int?>(null) }
    val hasVoted = votedOption != null || poll.isFinal
    val totalVotes = if (hasVoted && !poll.isFinal) (poll.totalVotes + 1) else poll.totalVotes

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Author header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(38.dp).clip(androidx.compose.foundation.shape.CircleShape).background(ElectricPurple), contentAlignment = Alignment.Center) {
                    Text(poll.author.firstOrNull()?.toString() ?: "?", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(poll.author, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 14.sp)
                    Text(poll.handle, color = TextMuted, fontSize = 12.sp)
                }
                IconButton(onClick = {}, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.MoreVert, null, tint = TextMuted, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(poll.question, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(12.dp))

            // Poll options
            poll.options.forEachIndexed { index, option ->
                val voteCount = if (votedOption == index && !poll.isFinal) option.votes + 1 else option.votes
                val percentage = if (totalVotes > 0) (voteCount.toFloat() / totalVotes * 100).toInt() else 0
                val isWinner = hasVoted && poll.options.maxByOrNull { it.votes }?.text == option.text

                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                        .background(DarkBackground)
                        .clickable(enabled = !hasVoted) { votedOption = index }
                        .padding(1.dp)
                ) {
                    // Progress background
                    if (hasVoted && percentage > 0) {
                        Box(
                            modifier = Modifier.fillMaxWidth(percentage / 100f).height(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isWinner) ElectricPurple.copy(alpha = 0.3f) else DarkCard)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!hasVoted) {
                                Box(modifier = Modifier.size(18.dp).clip(androidx.compose.foundation.shape.CircleShape).background(DarkCard), contentAlignment = Alignment.Center) {
                                    if (votedOption == index) Box(modifier = Modifier.size(10.dp).clip(androidx.compose.foundation.shape.CircleShape).background(ElectricPurple))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                            Text(option.text, color = if (isWinner && hasVoted) TextPrimary else TextSecondary, fontSize = 14.sp, fontWeight = if (isWinner && hasVoted) FontWeight.SemiBold else FontWeight.Normal)
                        }
                        if (hasVoted) {
                            Text("$percentage%", color = if (isWinner) ElectricPurple else TextMuted, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Footer
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (totalVotes > 0) "$totalVotes votes" else "Be the first to vote",
                    color = TextMuted, fontSize = 12.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (poll.isFinal) {
                        Text("Final results", color = TextMuted, fontSize = 12.sp)
                    } else {
                        Text(poll.timeLeft, color = ElectricPurple, fontSize = 12.sp)
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = DividerColor, thickness = 0.5.dp)

            // Actions
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {}) {
                    Icon(Icons.Outlined.ChatBubbleOutline, null, tint = TextMuted, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("24", color = TextMuted, fontSize = 13.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {}) {
                    Icon(Icons.Outlined.Repeat, null, tint = TextMuted, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("7", color = TextMuted, fontSize = 13.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {}) {
                    Icon(Icons.Outlined.FavoriteBorder, null, tint = TextMuted, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("32", color = TextMuted, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Outlined.Share, null, tint = TextMuted, modifier = Modifier.size(18.dp))
            }
        }
    }
}

// Create Poll Dialog
@Composable
fun CreatePollDialog(onDismiss: () -> Unit, onCreate: () -> Unit) {
    var question by remember { mutableStateOf("") }
    val options = remember { mutableStateListOf("", "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = { Text("Create Poll", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = question, onValueChange = { question = it },
                    placeholder = { Text("Ask a question...", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricPurple, unfocusedBorderColor = DividerColor, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = ElectricPurple)
                )
                Spacer(modifier = Modifier.height(12.dp))
                options.forEachIndexed { index, opt ->
                    OutlinedTextField(
                        value = opt, onValueChange = { options[index] = it },
                        placeholder = { Text("Option ${index + 1}", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricPurple, unfocusedBorderColor = DividerColor, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = ElectricPurple)
                    )
                }
                if (options.size < 4) {
                    TextButton(onClick = { options.add("") }) {
                        Icon(Icons.Default.Add, null, tint = ElectricPurple, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add option", color = ElectricPurple)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onCreate(); onDismiss() }, colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple)) {
                Text("Post Poll", color = TextPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) }
        }
    )
}
