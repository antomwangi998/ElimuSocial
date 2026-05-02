package com.elimusocial.app.ui.screens.creator

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elimusocial.app.ui.theme.*

data class PricingPlan(
    val name: String,
    val price: String,
    val period: String,
    val features: List<String>,
    val isPopular: Boolean = false,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonetizationScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("For You", "For Creators")

    val personalPlans = listOf(
        PricingPlan(
            name = "Basic",
            price = "KSh 199",
            period = "/month",
            features = listOf("Premium badge", "Custom profile theme", "Bookmark collections"),
            color = AccentBlue
        ),
        PricingPlan(
            name = "Pro",
            price = "KSh 499",
            period = "/month",
            features = listOf("AI Basic features", "Post analytics", "Advanced filters", "AI credits (100/month)"),
            isPopular = true,
            color = ElectricPurple
        ),
        PricingPlan(
            name = "Institution",
            price = "Custom pricing",
            period = "",
            features = listOf("School dashboard", "Advanced analytics", "Priority support"),
            color = AccentGreen
        )
    )

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) }
                },
                title = { Text("Go Premium", fontWeight = FontWeight.Bold, color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.linearGradient(listOf(ElectricPurple, AccentBlue)))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⚡", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Unlock exclusive features\nand support community growth.",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )
                    }
                }
            }

            // Tabs
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkCard, RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    tabs.forEachIndexed { i, tab ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedTab == i) ElectricPurple else Color.Transparent)
                                .clickable { selectedTab = i }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                tab,
                                color = if (selectedTab == i) Color.White else TextMuted,
                                fontWeight = if (selectedTab == i) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Plans
            items(personalPlans.size) { index ->
                val plan = personalPlans[index]
                PricingCard(plan = plan)
            }

            // Contact Sales for Institution
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkCard, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text("Need a custom plan?", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("For schools and institutions with specific needs.", color = TextMuted, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = {},
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, ElectricPurple)
                        ) {
                            Text("Contact Sales", color = ElectricPurple, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PricingCard(plan: PricingPlan) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkCard)
            .then(
                if (plan.isPopular) Modifier.border(
                    1.5.dp,
                    Brush.linearGradient(listOf(ElectricPurple, AccentBlue)),
                    RoundedCornerShape(16.dp)
                ) else Modifier.border(1.dp, DividerColor, RoundedCornerShape(16.dp))
            )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(plan.name, color = plan.color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                if (plan.isPopular) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.linearGradient(listOf(ElectricPurple, AccentBlue)),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Most Popular", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(plan.price, color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                if (plan.period.isNotEmpty()) {
                    Text(plan.period, color = TextMuted, fontSize = 13.sp, modifier = Modifier.padding(bottom = 3.dp))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            plan.features.forEach { feature ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, null, tint = plan.color, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(feature, color = TextSecondary, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (plan.isPopular) ElectricPurple else plan.color.copy(alpha = 0.15f)
                )
            ) {
                Text(
                    if (plan.name == "Institution") "Contact Sales" else "Get ${plan.name}",
                    color = if (plan.isPopular) Color.White else plan.color,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
