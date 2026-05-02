package com.elimusocial.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elimusocial.app.navigation.AppNavigation
import com.elimusocial.app.service.ElimuFCMService
import com.elimusocial.app.ui.theme.ElimuSocialTheme
import com.elimusocial.app.ui.viewmodels.AuthViewModel
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ElimuFCMService.createNotificationChannels(this)
        FirebaseMessaging.getInstance().token.addOnSuccessListener { _ -> }
        enableEdgeToEdge()
        setContent {
            ElimuSocialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val authViewModel: AuthViewModel = viewModel()
                    AppNavigation(authViewModel = authViewModel)
                }
            }
        }
    }
}
