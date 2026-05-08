package com.elimusocial.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elimusocial.app.navigation.AppNavigation
import com.elimusocial.app.service.ElimuFCMService
import com.elimusocial.app.ui.theme.ElimuSocialTheme
import com.elimusocial.app.ui.viewmodels.AuthViewModel
import com.elimusocial.app.ui.viewmodels.FeedViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ElimuFCMService.createNotificationChannels(this)
        setContent {
            ElimuSocialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val authViewModel: AuthViewModel = viewModel()
                    val feedViewModel: FeedViewModel = viewModel()
                    AppNavigation(
                        authViewModel = authViewModel,
                        feedViewModel = feedViewModel
                    )
                }
            }
        }
    }
}
