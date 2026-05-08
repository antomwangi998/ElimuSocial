package com.elimusocial.app.ui.screens.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.elimusocial.app.ui.theme.*
import com.elimusocial.app.ui.viewmodels.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenFirebase(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) onLoginSuccess()
    }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    .getResult(ApiException::class.java)
                account.idToken?.let { viewModel.handleGoogleSignInResult(it) }
            } catch (_: ApiException) {}
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = DarkCard,
            title = { Text("Reset Password", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter your email to receive a reset link.", color = TextSecondary, fontSize = 13.sp)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = resetEmail, onValueChange = { resetEmail = it },
                        placeholder = { Text("Email address", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricPurple, unfocusedBorderColor = DividerColor,
                            focusedContainerColor = DarkSurface, unfocusedContainerColor = DarkSurface,
                            focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
                        ), singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.resetPassword(resetEmail); showResetDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple)) {
                    Text("Send Reset Link")
                }
            },
            dismissButton = { TextButton(onClick = { showResetDialog = false }) { Text("Cancel", color = TextMuted) } }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
        Box(modifier = Modifier.fillMaxWidth().height(300.dp)
            .background(Brush.verticalGradient(listOf(ElectricPurple.copy(alpha = 0.12f), Color.Transparent))))

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(80.dp))

            // Logo
            Box(
                modifier = Modifier.size(72.dp)
                    .background(Brush.linearGradient(listOf(ElectricPurple, AccentBlue)), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) { Text("⚡", fontSize = 34.sp) }

            Spacer(Modifier.height(20.dp))
            Text("Welcome back 👋", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("Login to continue", fontSize = 14.sp, color = TextSecondary, modifier = Modifier.padding(top = 4.dp))
            Spacer(Modifier.height(32.dp))

            // Error
            uiState.error?.let { err ->
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                    colors = CardDefaults.cardColors(containerColor = AccentRed.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Error, null, tint = AccentRed, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(err, color = AccentRed, fontSize = 13.sp, modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.clearError() }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Close, null, tint = AccentRed, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            ElimuTextField(value = email, onValueChange = { email = it },
                placeholder = "Email or username", keyboardType = KeyboardType.Email)
            Spacer(Modifier.height(14.dp))
            ElimuTextField(value = password, onValueChange = { password = it },
                placeholder = "Password", keyboardType = KeyboardType.Password,
                isPassword = true, passwordVisible = passwordVisible,
                onTogglePassword = { passwordVisible = !passwordVisible })

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(onClick = { showResetDialog = true }) {
                    Text("Forgot password?", color = ElectricPurple, fontSize = 13.sp)
                }
            }

            Button(
                onClick = { viewModel.signIn(email, password) },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) CircularProgressIndicator(Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                else Text("Login", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(22.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = DividerColor)
                Text("  or  ", color = TextMuted, fontSize = 13.sp)
                HorizontalDivider(modifier = Modifier.weight(1f), color = DividerColor)
            }
            Spacer(Modifier.height(22.dp))

            // Google Sign-In with proper logo
            OutlinedButton(
                onClick = {
                    val client = viewModel.getGoogleSignInClient(context as Activity)
                    googleLauncher.launch(client.signInIntent)
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, DividerColor),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = DarkCard)
            ) {
                // Google logo using colored text segments matching the real logo
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(color = Color(0xFF4285F4))) { append("G") }
                        withStyle(SpanStyle(color = Color(0xFFEA4335))) { append("o") }
                        withStyle(SpanStyle(color = Color(0xFFFBBC05))) { append("o") }
                        withStyle(SpanStyle(color = Color(0xFF4285F4))) { append("g") }
                        withStyle(SpanStyle(color = Color(0xFF34A853))) { append("l") }
                        withStyle(SpanStyle(color = Color(0xFFEA4335))) { append("e") }
                    },
                    fontSize = 18.sp, fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(12.dp))
                Text("Continue with Google", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(16.dp))

            // Apple Sign-In button (UI only)
            OutlinedButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, DividerColor),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = DarkCard)
            ) {
                Text("", fontSize = 20.sp)
                Spacer(Modifier.width(10.dp))
                Text("Continue with Apple", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account?", color = TextSecondary, fontSize = 14.sp)
                TextButton(onClick = onNavigateToSignUp) {
                    Text("Sign up", color = ElectricPurple, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

// Shared text field component used across auth screens
@Composable
fun ElimuTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = TextMuted) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        singleLine = true,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword && !passwordVisible)
            androidx.compose.ui.text.input.PasswordVisualTransformation()
        else androidx.compose.ui.text.input.VisualTransformation.None,
        trailingIcon = if (isPassword) ({
            IconButton(onClick = { onTogglePassword?.invoke() }) {
                Icon(
                    if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    null, tint = TextMuted
                )
            }
        }) else null,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ElectricPurple, unfocusedBorderColor = DividerColor,
            focusedContainerColor = DarkCard, unfocusedContainerColor = DarkCard,
            focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
            cursorColor = ElectricPurple
        )
    )
}
