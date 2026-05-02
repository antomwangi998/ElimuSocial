package com.elimusocial.app.ui.screens.social

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

data class LiveComment(val author: String, val text: String, val emoji: String = "")

@Composable
fun LiveStreamScreen(onBack: () -> Unit) {
    var comment by remember { mutableStateOf("") }
    var viewerCount by remember { mutableIntStateOf(243) }
    val listState = rememberLazyListState()

    val comments = remember {
        mutableStateListOf(
            LiveComment("Joyce", "This is so insightful 🔥"),
            LiveComment("Mary", "Great session! 🙌"),
            LiveComment("Brian", "Thank you! 🎉"),
            LiveComment("Alex", "Amazing content!"),
            LiveComment("Sarah", "Loving this 💜")
        )
    }

    // Simulate new viewers joining
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            viewerCount += (1..5).random()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Stream background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0A1628), Color(0xFF1A0533), Color.Black)
                    )
                )
        ) {
            // Simulated presenter
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(ElectricPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("JK", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 40.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("John Kamau", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Guest Speaker", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                }
            }
        }

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LIVE badge + viewers
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(AccentRed, RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("LIVE", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                }
                Row(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Visibility, null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(viewerCount.toString(), color = Color.White, fontSize = 13.sp)
                }
            }

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }

        // Comments + Input at bottom
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            // Comments list (last 5 visible)
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 12.dp),
                reverseLayout = true,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(comments.reversed()) { liveComment ->
                    LiveCommentBubble(liveComment)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Input row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    placeholder = { Text("Add a comment...", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricPurple,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedContainerColor = Color.Black.copy(alpha = 0.5f),
                        unfocusedContainerColor = Color.Black.copy(alpha = 0.5f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true,
                    maxLines = 1
                )

                // Heart button
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Favorite, null, tint = AccentRed, modifier = Modifier.size(22.dp))
                }

                // Send
                IconButton(
                    onClick = {
                        if (comment.isNotBlank()) {
                            comments.add(LiveComment("You", comment))
                            comment = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(ElectricPurple, CircleShape)
                ) {
                    Icon(Icons.Default.Send, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun LiveCommentBubble(comment: LiveComment) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(ElectricPurple),
            contentAlignment = Alignment.Center
        ) {
            Text(comment.author.first().toString(), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(comment.author, color = LightPurple, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(comment.text, color = Color.White, fontSize = 12.sp)
    }
}
