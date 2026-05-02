package com.elimusocial.app.ui.screens.learning

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elimusocial.app.ui.theme.*

data class StudyTask(
    val id: String,
    val subject: String,
    val task: String,
    val timeStart: String,
    val timeEnd: String,
    val color: Color,
    var isDone: Boolean = false
)

data class CalendarDay(val day: Int, val label: String, val isToday: Boolean = false, val hasEvent: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyPlannerScreen(onBack: () -> Unit) {
    var selectedDay by remember { mutableStateOf(19) }

    val calendarDays = listOf(
        CalendarDay(13, "Mon"), CalendarDay(14, "Tue"), CalendarDay(15, "Wed"),
        CalendarDay(16, "Thu"), CalendarDay(17, "Fri"), CalendarDay(18, "Sat"),
        CalendarDay(19, "Sun", isToday = true, hasEvent = true),
        CalendarDay(20, "Mon", hasEvent = true), CalendarDay(21, "Tue"),
        CalendarDay(22, "Wed", hasEvent = true), CalendarDay(23, "Thu"),
        CalendarDay(24, "Fri", hasEvent = true), CalendarDay(25, "Sat"),
        CalendarDay(26, "Sun")
    )

    val todayTasks = remember {
        mutableStateListOf(
            StudyTask("1", "Math", "Math Revision", "11:00 AM", "11:30 AM", ElectricPurple),
            StudyTask("2", "Physics", "Physics Homework", "11:45 AM", "12:00 PM", AccentBlue),
            StudyTask("3", "English", "Chemistry Reading", "2:00 PM", "3:00 PM", AccentGreen, isDone = true)
        )
    }

    val weeklyProgress = listOf(
        Triple("Math", 0.75f, ElectricPurple),
        Triple("Science", 0.50f, AccentBlue),
        Triple("English", 0.90f, AccentGreen),
        Triple("History", 0.30f, AccentOrange)
    )

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) }
                },
                title = { Text("Study Planner", fontWeight = FontWeight.Bold, color = TextPrimary) },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Add, null, tint = ElectricPurple) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Month header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("May 2024", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Icon(Icons.Default.ArrowDropDown, null, tint = TextSecondary)
                    }
                    Row {
                        IconButton(onClick = {}) { Icon(Icons.Default.ChevronLeft, null, tint = TextSecondary) }
                        IconButton(onClick = {}) { Icon(Icons.Default.ChevronRight, null, tint = TextSecondary) }
                    }
                }
            }

            // Calendar row
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { d ->
                        Text(d, color = TextMuted, fontSize = 10.sp, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                // Mini grid - show 2 weeks
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(calendarDays.take(7), calendarDays.drop(7)).forEach { week ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            week.forEach { day ->
                                CalendarDayCell(day = day, isSelected = selectedDay == day.day, onClick = { selectedDay = day.day }, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // Today's plan
            item {
                Text("Today's Plan", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(10.dp))

                if (todayTasks.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().background(DarkCard, RoundedCornerShape(14.dp)).padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No tasks for today. Tap + to add one!", color = TextMuted, fontSize = 13.sp)
                    }
                }
            }

            items(todayTasks) { task ->
                StudyTaskCard(task = task, onToggle = { task.isDone = !task.isDone })
            }

            // Weekly progress
            item {
                Text("Weekly Progress", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier.fillMaxWidth().background(DarkCard, RoundedCornerShape(16.dp)).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    weeklyProgress.forEach { (subject, progress, color) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(subject, color = TextSecondary, fontSize = 13.sp, modifier = Modifier.width(64.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = color,
                                trackColor = DividerColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${(progress * 100).toInt()}%", color = color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(34.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarDayCell(day: CalendarDay, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                when {
                    isSelected -> ElectricPurple
                    day.isToday -> ElectricPurple.copy(alpha = 0.2f)
                    else -> Color.Transparent
                }
            )
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            day.day.toString(),
            color = when {
                isSelected -> Color.White
                day.isToday -> ElectricPurple
                else -> TextSecondary
            },
            fontWeight = if (isSelected || day.isToday) FontWeight.Bold else FontWeight.Normal,
            fontSize = 13.sp
        )
        if (day.hasEvent) {
            Spacer(modifier = Modifier.height(3.dp))
            Box(modifier = Modifier.size(4.dp).background(if (isSelected) Color.White else ElectricPurple, CircleShape))
        }
    }
}

@Composable
fun StudyTaskCard(task: StudyTask, onToggle: () -> Unit) {
    var isDone by remember { mutableStateOf(task.isDone) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkCard, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Color bar
        Box(modifier = Modifier.width(4.dp).height(44.dp).background(task.color, RoundedCornerShape(2.dp)))
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                task.task,
                color = if (isDone) TextMuted else TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
            )
            Spacer(modifier = Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.AccessTime, null, tint = TextMuted, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("${task.timeStart} – ${task.timeEnd}", color = TextMuted, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.background(task.color.copy(alpha = 0.15f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text(task.subject, color = task.color, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Checkbox(
            checked = isDone,
            onCheckedChange = { isDone = it; onToggle() },
            colors = CheckboxDefaults.colors(checkedColor = task.color, uncheckedColor = TextMuted)
        )
    }
}
