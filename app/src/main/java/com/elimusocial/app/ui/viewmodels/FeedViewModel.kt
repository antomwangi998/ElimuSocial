package com.elimusocial.app.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elimusocial.app.data.repository.FirebaseRepository
import com.elimusocial.app.data.repository.FirestorePost
import com.elimusocial.app.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FeedUiState(
    val posts: List<FirestorePost> = emptyList(),
    val likedPostIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val isCreatingPost: Boolean = false,
    val error: String? = null
)

class FeedViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        observePosts()
    }

    // ── Observe real-time posts from Firestore ─────────────────────────────
    private fun observePosts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getPostsFlow().collect { posts ->
                _uiState.value = _uiState.value.copy(
                    posts = posts,
                    isLoading = false
                )
            }
        }
    }

    // ── Like / Unlike ──────────────────────────────────────────────────────
    fun toggleLike(postId: String) {
        viewModelScope.launch {
            val currentLiked = _uiState.value.likedPostIds.toMutableSet()
            // Optimistic UI update
            if (currentLiked.contains(postId)) currentLiked.remove(postId)
            else currentLiked.add(postId)
            _uiState.value = _uiState.value.copy(likedPostIds = currentLiked)

            when (val result = repository.toggleLike(postId)) {
                is Result.Error -> {
                    // Revert on failure
                    if (currentLiked.contains(postId)) currentLiked.remove(postId)
                    else currentLiked.add(postId)
                    _uiState.value = _uiState.value.copy(likedPostIds = currentLiked, error = result.message)
                }
                else -> {}
            }
        }
    }

    fun isLiked(postId: String) = _uiState.value.likedPostIds.contains(postId)

    // ── Create post (text only) ────────────────────────────────────────────
    fun createPost(content: String, userProfile: com.elimusocial.app.data.repository.FirestoreUser) {
        if (content.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreatingPost = true)
            val post = FirestorePost(
                authorId = repository.currentUserId,
                authorName = userProfile.name,
                authorUsername = userProfile.username,
                authorAvatarUrl = userProfile.avatarUrl,
                authorVerified = userProfile.isVerified,
                content = content,
                type = "post"
            )
            when (val result = repository.createPost(post)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(isCreatingPost = false)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(isCreatingPost = false, error = result.message)
                }
                else -> {}
            }
        }
    }

    // ── Create post with image ─────────────────────────────────────────────
    fun createPostWithImage(
        content: String,
        imageBytes: ByteArray,
        userProfile: com.elimusocial.app.data.repository.FirestoreUser
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreatingPost = true)
            // 1. Upload image to Firebase Storage
            val imagePath = "posts/${repository.currentUserId}/${System.currentTimeMillis()}.jpg"
            val uploadResult = repository.uploadImage(imageBytes, imagePath)
            if (uploadResult is Result.Error) {
                _uiState.value = _uiState.value.copy(isCreatingPost = false, error = uploadResult.message)
                return@launch
            }
            val imageUrl = (uploadResult as Result.Success).data
            // 2. Create post with image URL
            val post = FirestorePost(
                authorId = repository.currentUserId,
                authorName = userProfile.name,
                authorUsername = userProfile.username,
                authorAvatarUrl = userProfile.avatarUrl,
                authorVerified = userProfile.isVerified,
                content = content,
                imageUrl = imageUrl,
                type = "post"
            )
            repository.createPost(post)
            _uiState.value = _uiState.value.copy(isCreatingPost = false)
        }
    }

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }
}
