package com.elimusocial.app.ui.screens.auth

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.elimusocial.app.ui.theme.*
import com.elimusocial.app.ui.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    viewModel: AuthViewModel,
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("Student") }
    var roleExpanded by remember { mutableStateOf(false) }
    val roles = listOf("Student", "Teacher", "Admin")

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) onSignUpSuccess()
    }

    Box(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
        Box(
            modifier = Modifier.fillMaxWidth().height(280.dp)
                .background(Brush.verticalGradient(listOf(ElectricPurple.copy(alpha = 0.08f), Color.Transparent)))
        )
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(70.dp))

            Box(
                modifier = Modifier.size(64.dp)
                    .background(Brush.linearGradient(listOf(ElectricPurple, AccentBlue)), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) { Text("⚡", fontSize = 30.sp) }

            Spacer(modifier = Modifier.height(20.dp))
            Text("Create Account", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("Join Elimu Social today", fontSize = 14.sp, color = TextSecondary, modifier = Modifier.padding(top = 4.dp))
            Spacer(modifier = Modifier.height(28.dp))

            // Error
            uiState.error?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = AccentRed.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Error, null, tint = AccentRed, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(error, color = AccentRed, fontSize = 13.sp, modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.clearError() }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Close, null, tint = AccentRed, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            ElimuTextField(value = name, onValueChange = { name = it }, placeholder = "Full name")
            Spacer(modifier = Modifier.height(14.dp))
            ElimuTextField(value = email, onValueChange = { email = it }, placeholder = "Email address", keyboardType = KeyboardType.Email)
            Spacer(modifier = Modifier.height(14.dp))
            ElimuTextField(
                value = password, onValueChange = { password = it }, placeholder = "Password",
                keyboardType = KeyboardType.Password, isPassword = true,
                passwordVisible = passwordVisible, onTogglePassword = { passwordVisible = !passwordVisible }
            )
            Spacer(modifier = Modifier.height(14.dp))

            ExposedDropdownMenuBox(expanded = roleExpanded, onExpandedChange = { roleExpanded = !roleExpanded }, modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedRole, onValueChange = {}, readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricPurple, unfocusedBorderColor = DividerColor,
                        focusedContainerColor = DarkCard, unfocusedContainerColor = DarkCard,
                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
                    )
                )
                ExposedDropdownMenu(expanded = roleExpanded, onDismissRequest = { roleExpanded = false }, modifier = Modifier.background(DarkCard)) {
                    roles.forEach { role ->
                        DropdownMenuItem(text = { Text(role, color = TextPrimary) }, onClick = { selectedRole = role; roleExpanded = false })
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = { viewModel.signUp(name, email, password, selectedRole) },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Sign Up", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = DividerColor)
                Text("  or  ", color = TextMuted, fontSize = 13.sp)
                HorizontalDivider(modifier = Modifier.weight(1f), color = DividerColor)
            }
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, DividerColor),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = DarkCard)
            ) {
                Text("G", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AccentRed)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Continue with Google", color = TextPrimary, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already have an account?", color = TextSecondary, fontSize = 14.sp)
                TextButton(onClick = onNavigateToLogin) {
                    Text("Login", color = ElectricPurple, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
