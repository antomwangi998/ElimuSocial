package com.elimusocial.app.ui.viewmodels

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elimusocial.app.data.repository.FirebaseRepository
import com.elimusocial.app.data.repository.FirestoreUser
import com.elimusocial.app.data.repository.Result
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val currentUser: FirebaseUser? = null,
    val userProfile: FirestoreUser? = null,
    val error: String? = null
)

class AuthViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Web Client ID from google-services.json oauth_client (client_type 3)
    private val WEB_CLIENT_ID = "your-web-client-id-here"

    init {
        val user = repository.currentUser
        if (user != null) {
            _uiState.value = _uiState.value.copy(isLoggedIn = true, currentUser = user)
            loadUserProfile(user.uid)
        }
    }

    fun signUp(name: String, email: String, password: String, role: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please fill in all fields"); return
        }
        if (password.length < 6) {
            _uiState.value = _uiState.value.copy(error = "Password must be at least 6 characters"); return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = repository.signUp(name, email, password, role)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true, currentUser = result.data)
                    loadUserProfile(result.data.uid)
                }
                is Result.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                else -> {}
            }
        }
    }

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please enter email and password"); return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = repository.signIn(email, password)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true, currentUser = result.data)
                    loadUserProfile(result.data.uid)
                }
                is Result.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                else -> {}
            }
        }
    }

    fun getGoogleSignInClient(activity: Activity): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(activity, gso)
    }

    fun handleGoogleSignInResult(idToken: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = repository.signInWithGoogle(idToken)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true, currentUser = result.data)
                    loadUserProfile(result.data.uid)
                }
                is Result.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                else -> {}
            }
        }
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) { _uiState.value = _uiState.value.copy(error = "Please enter your email"); return }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (repository.resetPassword(email)) {
                is Result.Success -> _uiState.value = _uiState.value.copy(isLoading = false, error = "✅ Reset email sent! Check your inbox.")
                is Result.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to send reset email")
                else -> {}
            }
        }
    }

    fun signOut() {
        repository.signOut()
        _uiState.value = AuthUiState()
    }

    fun uploadAvatar(imageBytes: ByteArray) {
        val uid = repository.currentUserId
        if (uid.isEmpty()) return
        viewModelScope.launch {
            val path = "avatars/$uid/avatar.jpg"
            when (val result = repository.uploadImage(imageBytes, path)) {
                is Result.Success -> {
                    repository.updateUserProfile(uid, mapOf("avatarUrl" to result.data))
                    loadUserProfile(uid)
                }
                else -> {}
            }
        }
    }

    fun uploadCover(imageBytes: ByteArray) {
        val uid = repository.currentUserId
        if (uid.isEmpty()) return
        viewModelScope.launch {
            val path = "covers/$uid/cover.jpg"
            when (val result = repository.uploadImage(imageBytes, path)) {
                is Result.Success -> {
                    repository.updateUserProfile(uid, mapOf("coverUrl" to result.data))
                    loadUserProfile(uid)
                }
                else -> {}
            }
        }
    }

    fun updateProfile(name: String, bio: String, location: String) {
        val uid = repository.currentUserId
        if (uid.isEmpty()) return
        viewModelScope.launch {
            repository.updateUserProfile(uid, mapOf("name" to name, "bio" to bio, "location" to location))
            loadUserProfile(uid)
        }
    }

    private fun loadUserProfile(uid: String) {
        viewModelScope.launch {
            when (val result = repository.getUserProfile(uid)) {
                is Result.Success -> _uiState.value = _uiState.value.copy(userProfile = result.data)
                else -> {}
            }
        }
    }

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }
}
