package com.elimusocial.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.elimusocial.app.data.models.SampleData
import com.elimusocial.app.ui.screens.auth.LoginScreenFirebase
import com.elimusocial.app.ui.screens.auth.SignUpScreen
import com.elimusocial.app.ui.screens.bookmarks.BookmarksScreen
import com.elimusocial.app.ui.screens.community.CommunitiesScreen
import com.elimusocial.app.ui.screens.community.GroupDetailScreen
import com.elimusocial.app.ui.screens.community.EventsScreen
import com.elimusocial.app.ui.screens.creator.AchievementsScreen
import com.elimusocial.app.ui.screens.creator.AnalyticsScreen
import com.elimusocial.app.ui.screens.creator.CreatorDashboardScreen
import com.elimusocial.app.ui.screens.creator.MonetizationScreen
import com.elimusocial.app.ui.screens.home.HomeScreen
import com.elimusocial.app.ui.screens.learning.ElimuAiScreen
import com.elimusocial.app.ui.screens.learning.StudyPlannerScreen
import com.elimusocial.app.ui.screens.messages.MessagesScreen
import com.elimusocial.app.ui.screens.notifications.NotificationsScreen
import com.elimusocial.app.ui.screens.onboarding.ChooseGoalsScreen
import com.elimusocial.app.ui.screens.onboarding.FollowPeopleScreen
import com.elimusocial.app.ui.screens.onboarding.OnboardingScreen
import com.elimusocial.app.ui.screens.postdetail.PostDetailScreen
import com.elimusocial.app.ui.screens.profile.ProfileScreen
import com.elimusocial.app.ui.screens.search.SearchScreen
import com.elimusocial.app.ui.screens.settings.SettingsScreen
import com.elimusocial.app.ui.screens.social.FollowingFollowersScreen
import com.elimusocial.app.ui.screens.social.LiveStreamScreen
import com.elimusocial.app.ui.screens.social.PollsScreen
import com.elimusocial.app.ui.screens.social.ReelsScreenWithPlayer
import com.elimusocial.app.ui.screens.social.SpacesScreen
import com.elimusocial.app.ui.screens.splash.SplashScreen
import com.elimusocial.app.ui.viewmodels.AuthViewModel
import com.elimusocial.app.ui.viewmodels.FeedViewModel

object Routes {
    const val SPLASH          = "splash"
    const val ONBOARDING      = "onboarding"
    const val FOLLOW_PEOPLE   = "follow_people"
    const val CHOOSE_GOALS    = "choose_goals"
    const val LOGIN           = "login"
    const val SIGNUP          = "signup"
    const val HOME            = "home"
    const val REELS           = "reels"
    const val LIVE_STREAM     = "live_stream"
    const val SPACES          = "spaces"
    const val COMMUNITIES     = "communities"
    const val GROUP_DETAIL    = "group_detail"
    const val EVENTS          = "events"
    const val POLLS           = "polls"
    const val CREATOR_DASH    = "creator_dashboard"
    const val MONETIZATION    = "monetization"
    const val ACHIEVEMENTS    = "achievements"
    const val ANALYTICS       = "analytics"
    const val ELIMU_AI        = "elimu_ai"
    const val STUDY_PLANNER   = "study_planner"
    const val MESSAGES        = "messages"
    const val SETTINGS        = "settings"
    const val NOTIFICATIONS   = "notifications"
    const val SEARCH          = "search"
    const val POST_DETAIL     = "post_detail"
    const val BOOKMARKS       = "bookmarks"
    const val FOLLOWING       = "following"
    const val FOLLOWERS       = "followers"
    const val PROFILE         = "profile"
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel,
    feedViewModel: FeedViewModel = viewModel()
) {
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val startDestination = if (authState.isLoggedIn) Routes.HOME else Routes.SPLASH

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Routes.ONBOARDING) { popUpTo(Routes.SPLASH) { inclusive = true } }
                },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) { popUpTo(Routes.SPLASH) { inclusive = true } }
                }
            )
        }

        composable(Routes.ONBOARDING) {
            OnboardingScreen(onGetStarted = { navController.navigate(Routes.FOLLOW_PEOPLE) })
        }

        composable(Routes.FOLLOW_PEOPLE) {
            FollowPeopleScreen(
                onContinue = { navController.navigate(Routes.CHOOSE_GOALS) },
                onSkip = { navController.navigate(Routes.CHOOSE_GOALS) }
            )
        }

        composable(Routes.CHOOSE_GOALS) {
            ChooseGoalsScreen(
                onContinue = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreenFirebase(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                },
                onNavigateToSignUp = { navController.navigate(Routes.SIGNUP) }
            )
        }

        composable(Routes.SIGNUP) {
            SignUpScreen(
                viewModel = authViewModel,
                onSignUpSuccess = {
                    navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Routes.HOME) {
            val feedState by feedViewModel.uiState.collectAsStateWithLifecycle()
            HomeScreen(
                authViewModel = authViewModel,
                feedViewModel = feedViewModel,
                onLogout = {
                    authViewModel.signOut()
                    navController.navigate(Routes.LOGIN) { popUpTo(Routes.HOME) { inclusive = true } }
                },
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(Routes.REELS) {
            @OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
            ReelsScreenWithPlayer(onBack = { navController.popBackStack() })
        }

        composable(Routes.LIVE_STREAM) {
            LiveStreamScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.SPACES) {
            SpacesScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.COMMUNITIES) {
            CommunitiesScreen(
                onCommunityClick = { community ->
                    navController.navigate(Routes.GROUP_DETAIL)
                }
            )
        }

        composable(Routes.GROUP_DETAIL) {
            GroupDetailScreen(
                community = com.elimusocial.app.ui.screens.community.Community(
                    "1", "Computer Science Hub",
                    "Students who love coding, tech & solutions",
                    1200, 25, "Tech", "💻", isJoined = true
                ),
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.EVENTS) {
            EventsScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.POLLS) {
            PollsScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.CREATOR_DASH) {
            CreatorDashboardScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.MONETIZATION) {
            MonetizationScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.ACHIEVEMENTS) {
            AchievementsScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.ANALYTICS) {
            AnalyticsScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.ELIMU_AI) {
            ElimuAiScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.STUDY_PLANNER) {
            StudyPlannerScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.MESSAGES) {
            MessagesScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.signOut()
                    navController.navigate(Routes.LOGIN) { popUpTo(Routes.HOME) { inclusive = true } }
                }
            )
        }

        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.SEARCH) {
            SearchScreen(
                onBack = { navController.popBackStack() },
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(Routes.POST_DETAIL) {
            val feedState by feedViewModel.uiState.collectAsStateWithLifecycle()
            PostDetailScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.BOOKMARKS) {
            BookmarksScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.FOLLOWING) {
            FollowingFollowersScreen(
                initialTab = 0,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.FOLLOWERS) {
            FollowingFollowersScreen(
                initialTab = 1,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PROFILE) {
            val authState2 by authViewModel.uiState.collectAsStateWithLifecycle()
            val feedState by feedViewModel.uiState.collectAsStateWithLifecycle()
            ProfileScreen(
                userProfile = authState2.userProfile,
                posts = feedState.posts.filter { it.authorId == authState2.currentUser?.uid },
                isOwnProfile = true,
                onNavigate = { route -> navController.navigate(route) },
                onBack = { navController.popBackStack() },
                onAvatarUpload = { bytes -> authViewModel.uploadAvatar(bytes) },
                onCoverUpload = { bytes -> authViewModel.uploadCover(bytes) },
                onSaveProfile = { name, bio, location -> authViewModel.updateProfile(name, bio, location) }
            )
        }
    }
}
