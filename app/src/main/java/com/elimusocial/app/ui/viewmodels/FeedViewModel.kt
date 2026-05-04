package com.elimusocial.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elimusocial.app.data.repository.FirebaseRepository
import com.elimusocial.app.data.repository.FirestorePost
import com.elimusocial.app.data.repository.FirestoreUser
import com.elimusocial.app.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.pow

data class FeedUiState(
    val posts: List<FirestorePost> = emptyList(),
    val likedPostIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val isCreatingPost: Boolean = false,
    val error: String? = null
)

// ── Feed Engine Constants ──────────────────────────────────────────────────
private object FeedEngine {
    // Comment weight: comments indicate higher engagement than likes
    const val COMMENT_WEIGHT = 2.0

    // Gravity per post type — how fast posts decay in the feed
    // Lower G = posts stay visible longer (good for educational content)
    // Higher G = feed moves faster (good for general chat)
    fun gravity(postType: String): Double = when (postType) {
        "educational" -> 1.5   // Stay visible longer
        "reel"        -> 2.2   // Fast moving
        "poll"        -> 1.6   // Moderate
        else          -> 1.8   // Standard — "Industry Standard"
    }

    /**
     * Hacker News-inspired gravity formula:
     *
     *   Score = (S + C × w) / (T + 1)^G
     *
     * Where:
     *   S = likes (raw score)
     *   C = comments count
     *   w = comment weight multiplier (2.0)
     *   T = age of post in hours
     *   G = gravity constant (type-dependent)
     */
    fun score(post: FirestorePost): Double {
        val ageHours = (System.currentTimeMillis() - post.createdAt) / 3_600_000.0
        val s = post.likes.toDouble()
        val c = post.comments.toDouble()
        val w = COMMENT_WEIGHT
        val t = ageHours
        val g = gravity(post.type)

        return (s + c * w) / (t + 1.0).pow(g)
    }

    /**
     * Rank a list of posts using the gravity score.
     * New posts (< 1 hour old) get a freshness boost so they appear
     * even with zero engagement.
     */
    fun rank(posts: List<FirestorePost>): List<FirestorePost> {
        val nowMs = System.currentTimeMillis()
        return posts.sortedByDescending { post ->
            val ageHours = (nowMs - post.createdAt) / 3_600_000.0
            val base = score(post)
            // Freshness boost for posts under 1 hour — ensures new posts appear
            val freshBoost = if (ageHours < 1.0) 2.5 else 1.0
            base * freshBoost
        }
    }
}

class FeedViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        observePosts()
        loadLikedPosts()
    }

    // ── Observe real-time posts — ranked by gravity algorithm ──────────────
    private fun observePosts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getPostsFlow().collect { rawPosts ->
                val rankedPosts = FeedEngine.rank(rawPosts)
                _uiState.value = _uiState.value.copy(
                    posts = rankedPosts,
                    isLoading = false
                )
            }
        }
    }

    // ── Load which posts the current user has liked ────────────────────────
    private fun loadLikedPosts() {
        viewModelScope.launch {
            try {
                val likedIds = repository.getLikedPostIds()
                _uiState.value = _uiState.value.copy(likedPostIds = likedIds.toSet())
            } catch (e: Exception) {
                // Non-critical — just won't show liked state
            }
        }
    }

    // ── Like / Unlike ──────────────────────────────────────────────────────
    fun toggleLike(postId: String) {
        viewModelScope.launch {
            val currentLiked = _uiState.value.likedPostIds.toMutableSet()
            // Optimistic UI update
            val isNowLiked = !currentLiked.contains(postId)
            if (isNowLiked) currentLiked.add(postId) else currentLiked.remove(postId)
            _uiState.value = _uiState.value.copy(likedPostIds = currentLiked)

            // Also update post like count in the list immediately
            val updatedPosts = _uiState.value.posts.map { post ->
                if (post.id == postId) {
                    post.copy(likes = if (isNowLiked) post.likes + 1 else post.likes - 1)
                } else post
            }
            _uiState.value = _uiState.value.copy(posts = FeedEngine.rank(updatedPosts))

            when (val result = repository.toggleLike(postId)) {
                is Result.Error -> {
                    // Revert on failure
                    if (isNowLiked) currentLiked.remove(postId) else currentLiked.add(postId)
                    _uiState.value = _uiState.value.copy(likedPostIds = currentLiked, error = result.message)
                }
                else -> {}
            }
        }
    }

    fun isLiked(postId: String) = _uiState.value.likedPostIds.contains(postId)

    // ── Create post (text only) ────────────────────────────────────────────
    fun createPost(content: String, userProfile: FirestoreUser, type: String = "post") {
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
                type = type,
                createdAt = System.currentTimeMillis()
            )
            when (val result = repository.createPost(post)) {
                is Result.Success -> _uiState.value = _uiState.value.copy(isCreatingPost = false)
                is Result.Error -> _uiState.value = _uiState.value.copy(isCreatingPost = false, error = result.message)
                else -> {}
            }
        }
    }

    // ── Create post with image ─────────────────────────────────────────────
    fun createPostWithImage(content: String, imageBytes: ByteArray, userProfile: FirestoreUser) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreatingPost = true)
            val imagePath = "posts/${repository.currentUserId}/${System.currentTimeMillis()}.jpg"
            val uploadResult = repository.uploadImage(imageBytes, imagePath)
            if (uploadResult is Result.Error) {
                _uiState.value = _uiState.value.copy(isCreatingPost = false, error = uploadResult.message)
                return@launch
            }
            val imageUrl = (uploadResult as Result.Success).data
            val post = FirestorePost(
                authorId = repository.currentUserId,
                authorName = userProfile.name,
                authorUsername = userProfile.username,
                authorAvatarUrl = userProfile.avatarUrl,
                authorVerified = userProfile.isVerified,
                content = content,
                imageUrl = imageUrl,
                type = "post",
                createdAt = System.currentTimeMillis()
            )
            repository.createPost(post)
            _uiState.value = _uiState.value.copy(isCreatingPost = false)
        }
    }

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }
}
