package com.elimusocial.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

// ── Result wrapper ─────────────────────────────────────────────────────────
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val exception: Exception? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

// ── Firestore document models ──────────────────────────────────────────────
data class FirestoreUser(
    val uid: String = "",
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val bio: String = "",
    val location: String = "",
    val avatarUrl: String = "",
    val coverUrl: String = "",
    val followers: Int = 0,
    val following: Int = 0,
    val posts: Int = 0,
    val isVerified: Boolean = false,
    val role: String = "student",  // student | teacher | admin
    val fcmToken: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class FirestorePost(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorUsername: String = "",
    val authorAvatarUrl: String = "",
    val authorVerified: Boolean = false,
    val content: String = "",
    val imageUrl: String = "",
    val videoUrl: String = "",
    val likes: Int = 0,
    val comments: Int = 0,
    val reposts: Int = 0,
    val tags: List<String> = emptyList(),
    val type: String = "post",  // post | reel | poll | quote
    val createdAt: Long = System.currentTimeMillis()
)

data class FirestoreComment(
    val id: String = "",
    val postId: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorAvatarUrl: String = "",
    val content: String = "",
    val likes: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

// ── Repository ────────────────────────────────────────────────────────────
class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // Collections
    private val usersCol = db.collection("users")
    private val postsCol = db.collection("posts")
    private val commentsCol = db.collection("comments")
    private val likesCol = db.collection("likes")
    private val followsCol = db.collection("follows")
    private val notificationsCol = db.collection("notifications")

    val currentUser: FirebaseUser? get() = auth.currentUser
    val currentUserId: String get() = auth.currentUser?.uid ?: ""

    // ── Auth ───────────────────────────────────────────────────────────────

    suspend fun signUp(name: String, email: String, password: String, role: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user!!
            // Create user profile in Firestore
            val username = "@${name.lowercase().replace(" ", "_")}"
            val firestoreUser = FirestoreUser(
                uid = user.uid,
                name = name,
                username = username,
                email = email,
                role = role
            )
            usersCol.document(user.uid).set(firestoreUser).await()
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Sign up failed", e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.Success(result.user!!)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Login failed", e)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user!!
            // Create profile if new user
            if (result.additionalUserInfo?.isNewUser == true) {
                val firestoreUser = FirestoreUser(
                    uid = user.uid,
                    name = user.displayName ?: "",
                    username = "@${user.displayName?.lowercase()?.replace(" ", "_") ?: "user"}",
                    email = user.email ?: "",
                    avatarUrl = user.photoUrl?.toString() ?: ""
                )
                usersCol.document(user.uid).set(firestoreUser).await()
            }
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Google sign-in failed", e)
        }
    }

    fun signOut() = auth.signOut()

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to send reset email", e)
        }
    }

    // ── User Profile ───────────────────────────────────────────────────────

    suspend fun getUserProfile(uid: String): Result<FirestoreUser> {
        return try {
            val doc = usersCol.document(uid).get().await()
            val user = doc.toObject(FirestoreUser::class.java)
            if (user != null) Result.Success(user)
            else Result.Error("User not found")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load profile", e)
        }
    }

    fun getUserProfileFlow(uid: String): Flow<FirestoreUser?> = callbackFlow {
        val listener = usersCol.document(uid).addSnapshotListener { snap, _ ->
            trySend(snap?.toObject(FirestoreUser::class.java))
        }
        awaitClose { listener.remove() }
    }

    suspend fun updateUserProfile(uid: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            usersCol.document(uid).update(updates).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update profile", e)
        }
    }

    suspend fun updateFcmToken(token: String) {
        val uid = currentUserId
        if (uid.isNotEmpty()) {
            usersCol.document(uid).update("fcmToken", token).await()
        }
    }

    // ── Posts ──────────────────────────────────────────────────────────────

    fun getPostsFlow(): Flow<List<FirestorePost>> = callbackFlow {
        val listener = postsCol
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snap, _ ->
                val posts = snap?.documents?.mapNotNull { it.toObject(FirestorePost::class.java) }
                trySend(posts ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    fun getReelsFlow(): Flow<List<FirestorePost>> = callbackFlow {
        val listener = postsCol
            .whereEqualTo("type", "reel")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(20)
            .addSnapshotListener { snap, _ ->
                val reels = snap?.documents?.mapNotNull { it.toObject(FirestorePost::class.java) }
                trySend(reels ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    suspend fun createPost(post: FirestorePost): Result<String> {
        return try {
            val docRef = postsCol.document()
            val postWithId = post.copy(id = docRef.id, authorId = currentUserId)
            docRef.set(postWithId).await()
            // Increment user post count
            usersCol.document(currentUserId)
                .update("posts", com.google.firebase.firestore.FieldValue.increment(1))
                .await()
            Result.Success(docRef.id)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to create post", e)
        }
    }

    suspend fun deletePost(postId: String): Result<Unit> {
        return try {
            postsCol.document(postId).delete().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete post", e)
        }
    }

    // ── Likes ──────────────────────────────────────────────────────────────

    suspend fun toggleLike(postId: String): Result<Boolean> {
        return try {
            val likeDocId = "${currentUserId}_$postId"
            val likeDoc = likesCol.document(likeDocId).get().await()
            val postRef = postsCol.document(postId)

            if (likeDoc.exists()) {
                // Unlike
                likesCol.document(likeDocId).delete().await()
                postRef.update("likes", com.google.firebase.firestore.FieldValue.increment(-1)).await()
                Result.Success(false)
            } else {
                // Like
                likesCol.document(likeDocId).set(
                    mapOf("userId" to currentUserId, "postId" to postId, "createdAt" to System.currentTimeMillis())
                ).await()
                postRef.update("likes", com.google.firebase.firestore.FieldValue.increment(1)).await()
                Result.Success(true)
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to toggle like", e)
        }
    }

    suspend fun isPostLiked(postId: String): Boolean {
        return try {
            val likeDocId = "${currentUserId}_$postId"
            likesCol.document(likeDocId).get().await().exists()
        } catch (e: Exception) { false }
    }

    // ── Comments ───────────────────────────────────────────────────────────

    fun getCommentsFlow(postId: String): Flow<List<FirestoreComment>> = callbackFlow {
        val listener = commentsCol
            .whereEqualTo("postId", postId)
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snap, _ ->
                val comments = snap?.documents?.mapNotNull { it.toObject(FirestoreComment::class.java) }
                trySend(comments ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    suspend fun addComment(postId: String, content: String): Result<String> {
        return try {
            val user = getUserProfile(currentUserId)
            if (user is Result.Error) return Result.Error("Failed to get user")
            val userData = (user as Result.Success).data
            val docRef = commentsCol.document()
            val comment = FirestoreComment(
                id = docRef.id,
                postId = postId,
                authorId = currentUserId,
                authorName = userData.name,
                authorAvatarUrl = userData.avatarUrl,
                content = content
            )
            docRef.set(comment).await()
            postsCol.document(postId)
                .update("comments", com.google.firebase.firestore.FieldValue.increment(1))
                .await()
            Result.Success(docRef.id)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to add comment", e)
        }
    }

    // ── Follow / Unfollow ──────────────────────────────────────────────────

    suspend fun toggleFollow(targetUid: String): Result<Boolean> {
        return try {
            val followDocId = "${currentUserId}_$targetUid"
            val followDoc = followsCol.document(followDocId).get().await()

            if (followDoc.exists()) {
                followsCol.document(followDocId).delete().await()
                usersCol.document(currentUserId).update("following", com.google.firebase.firestore.FieldValue.increment(-1)).await()
                usersCol.document(targetUid).update("followers", com.google.firebase.firestore.FieldValue.increment(-1)).await()
                Result.Success(false)
            } else {
                followsCol.document(followDocId).set(
                    mapOf("followerId" to currentUserId, "followingId" to targetUid, "createdAt" to System.currentTimeMillis())
                ).await()
                usersCol.document(currentUserId).update("following", com.google.firebase.firestore.FieldValue.increment(1)).await()
                usersCol.document(targetUid).update("followers", com.google.firebase.firestore.FieldValue.increment(1)).await()
                Result.Success(true)
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to toggle follow", e)
        }
    }

    suspend fun isFollowing(targetUid: String): Boolean {
        return try {
            followsCol.document("${currentUserId}_$targetUid").get().await().exists()
        } catch (e: Exception) { false }
    }

    // ── Storage (upload images/videos) ────────────────────────────────────

    suspend fun uploadImage(byteArray: ByteArray, path: String): Result<String> {
        return try {
            val ref = storage.reference.child(path)
            ref.putBytes(byteArray).await()
            val url = ref.downloadUrl.await().toString()
            Result.Success(url)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Upload failed", e)
        }
    }

    // ── Search ─────────────────────────────────────────────────────────────

    suspend fun searchUsers(query: String): Result<List<FirestoreUser>> {
        return try {
            val result = usersCol
                .orderBy("name")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .limit(20)
                .get()
                .await()
            val users = result.documents.mapNotNull { it.toObject(FirestoreUser::class.java) }
            Result.Success(users)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Search failed", e)
        }
    }
}
