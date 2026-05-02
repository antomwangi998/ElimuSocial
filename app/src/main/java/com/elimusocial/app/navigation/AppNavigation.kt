package com.elimusocial.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.elimusocial.app.data.models.SampleData
import com.elimusocial.app.ui.screens.auth.LoginScreenFirebase
import com.elimusocial.app.ui.screens.auth.SignUpScreen
import com.elimusocial.app.ui.screens.community.CommunitiesScreen
import com.elimusocial.app.ui.screens.community.EventsScreen
import com.elimusocial.app.ui.screens.community.GroupDetailScreen
import com.elimusocial.app.ui.screens.creator.AchievementsScreen
import com.elimusocial.app.ui.screens.creator.CreatorDashboardScreen
import com.elimusocial.app.ui.screens.creator.MonetizationScreen
import com.elimusocial.app.ui.screens.home.HomeScreen
import com.elimusocial.app.ui.screens.learning.ElimuAiScreen
import com.elimusocial.app.ui.screens.learning.StudyPlannerScreen
import com.elimusocial.app.ui.screens.messages.MessagesScreen
import com.elimusocial.app.ui.screens.onboarding.OnboardingScreen
import com.elimusocial.app.ui.screens.settings.SettingsScreen
import com.elimusocial.app.ui.screens.social.LiveStreamScreen
import com.elimusocial.app.ui.screens.social.ReelsScreenWithPlayer
import com.elimusocial.app.ui.screens.social.SpacesScreen
import com.elimusocial.app.ui.screens.splash.SplashScreen
import com.elimusocial.app.ui.viewmodels.AuthViewModel

object Routes {
    const val SPLASH        = "splash"
    const val ONBOARDING    = "onboarding"
    const val LOGIN         = "login"
    const val SIGNUP        = "signup"
    const val HOME          = "home"
    const val REELS         = "reels"
    const val LIVE_STREAM   = "live_stream"
    const val SPACES        = "spaces"
    const val COMMUNITIES   = "communities"
    const val GROUP_DETAIL  = "group_detail"
    const val EVENTS        = "events"
    const val CREATOR_DASH  = "creator_dashboard"
    const val MONETIZATION  = "monetization"
    const val ACHIEVEMENTS  = "achievements"
    const val ELIMU_AI      = "elimu_ai"
    const val STUDY_PLANNER = "study_planner"
    const val MESSAGES      = "messages"
    const val SETTINGS      = "settings"
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val startDestination = if (authState.isLoggedIn) Routes.HOME else Routes.SPLASH

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToOnboarding = { navController.navigate(Routes.ONBOARDING) { popUpTo(Routes.SPLASH) { inclusive = true } } },
                onNavigateToHome = { navController.navigate(Routes.HOME) { popUpTo(Routes.SPLASH) { inclusive = true } } }
            )
        }
        composable(Routes.ONBOARDING) {
            OnboardingScreen(onGetStarted = { navController.navigate(Routes.LOGIN) { popUpTo(Routes.ONBOARDING) { inclusive = true } } })
        }
        composable(Routes.LOGIN) {
            LoginScreenFirebase(
                viewModel = authViewModel,
                onLoginSuccess = { navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } } },
                onNavigateToSignUp = { navController.navigate(Routes.SIGNUP) }
            )
        }
        composable(Routes.SIGNUP) {
            SignUpScreen(
                onSignUpSuccess = { navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } } },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                onLogout = { authViewModel.signOut(); navController.navigate(Routes.LOGIN) { popUpTo(Routes.HOME) { inclusive = true } } },
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        composable(Routes.REELS) {
            @OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
            ReelsScreenWithPlayer(onBack = { navController.popBackStack() })
        }
        composable(Routes.LIVE_STREAM) { LiveStreamScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.SPACES) { SpacesScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.COMMUNITIES) { CommunitiesScreen(onCommunityClick = { navController.navigate(Routes.GROUP_DETAIL) }) }
        composable(Routes.GROUP_DETAIL) { GroupDetailScreen(community = SampleData.communities[0], onBack = { navController.popBackStack() }) }
        composable(Routes.EVENTS) { EventsScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.CREATOR_DASH) { CreatorDashboardScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.MONETIZATION) { MonetizationScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.ACHIEVEMENTS) { AchievementsScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.ELIMU_AI) { ElimuAiScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.STUDY_PLANNER) { StudyPlannerScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.MESSAGES) { MessagesScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onLogout = { authViewModel.signOut(); navController.navigate(Routes.LOGIN) { popUpTo(Routes.HOME) { inclusive = true } } }
            )
        }
    }
}
