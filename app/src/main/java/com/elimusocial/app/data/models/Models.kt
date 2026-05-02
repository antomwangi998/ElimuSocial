package com.elimusocial.app.data.models

// ===== USER MODEL =====
data class User(
    val id: String,
    val name: String,
    val username: String,
    val avatarUrl: String = "",
    val bio: String = "",
    val location: String = "",
    val followers: Int = 0,
    val following: Int = 0,
    val posts: Int = 0,
    val isVerified: Boolean = false,
    val isFollowing: Boolean = false
)

// ===== POST MODEL =====
data class Post(
    val id: String,
    val author: User,
    val content: String,
    val imageUrl: String? = null,
    val likes: Int = 0,
    val comments: Int = 0,
    val reposts: Int = 0,
    val timestamp: String = "",
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val tags: List<String> = emptyList()
)

// ===== NOTIFICATION MODEL =====
data class Notification(
    val id: String,
    val type: NotificationType,
    val actor: User,
    val message: String,
    val timestamp: String,
    val isRead: Boolean = false
)

enum class NotificationType {
    LIKE, COMMENT, FOLLOW, REPOST, MENTION, SYSTEM
}

// ===== MESSAGE MODEL =====
data class Message(
    val id: String,
    val sender: User,
    val content: String,
    val timestamp: String,
    val isRead: Boolean = false
)

// ===== COMMUNITY MODEL =====
data class Community(
    val id: String,
    val name: String,
    val description: String,
    val members: Int,
    val newPosts: Int,
    val iconUrl: String = "",
    val isJoined: Boolean = false
)

// ===== SAMPLE DATA =====
object SampleData {
    val users = listOf(
        User("1", "Antony Mwangi", "@antony", bio = "Building solutions for education", location = "Nairobi, Kenya", followers = 1200, following = 320, posts = 128, isVerified = true),
        User("2", "Mary Wanjiku", "@mary", bio = "Lover of learning", location = "Nairobi", followers = 890, following = 210, posts = 64),
        User("3", "Brian Otieno", "@brian_dev", bio = "Full-stack developer", location = "Mombasa", followers = 450, following = 180, posts = 32),
        User("4", "Joyce Maina", "@joyce_m", bio = "Teacher & mentor", location = "Kisumu", followers = 2100, following = 98, posts = 210, isVerified = true),
        User("5", "Teacher Alex", "@teacher_alex", bio = "CS educator", location = "Nakuru", followers = 3400, following = 120, posts = 340, isVerified = true)
    )

    val posts = listOf(
        Post(
            id = "1",
            author = users[0],
            content = "Just finished our computer science project! 🔥💻 Teamwork makes the dream work! 💪",
            imageUrl = "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=800",
            likes = 128, comments = 32, reposts = 18,
            timestamp = "2h ago",
            tags = listOf("#CS", "#Teamwork")
        ),
        Post(
            id = "2",
            author = users[1],
            content = "Beautiful evening at the campus 🌅 #Blessed",
            imageUrl = "https://images.unsplash.com/photo-1523050854058-8df90110c9f1?w=800",
            likes = 64, comments = 12, reposts = 4,
            timestamp = "4h ago"
        ),
        Post(
            id = "3",
            author = users[4],
            content = "Discipline today, freedom tomorrow. 💪\n\nConsistency is the key to success. Keep pushing, great things take time.",
            likes = 245, comments = 45, reposts = 67,
            timestamp = "5h ago",
            isLiked = true
        ),
        Post(
            id = "4",
            author = users[2],
            content = "Just built a To-Do app with React! 🎉 Excited to share my learning journey. Who else is learning web dev?",
            likes = 87, comments = 23, reposts = 11,
            timestamp = "1d ago",
            tags = listOf("#WebDev", "#React", "#Learning")
        ),
        Post(
            id = "5",
            author = users[3],
            content = "Reminder: Midterm exams start on 20th May 2024. Prepare early! 📚 Here are 5 study tips that actually work 🧠✨",
            likes = 312, comments = 56, reposts = 98,
            timestamp = "1d ago",
            isLiked = true
        )
    )

    val communities = listOf(
        Community("1", "Computer Science Hub", "Love coding, tech and building solutions", members = 1200, newPosts = 25, isJoined = true),
        Community("2", "Elimu Announcements", "Official updates from Elimu", members = 3400, newPosts = 8, isJoined = true),
        Community("3", "Entrepreneurship Club", "Grow ideas, build solutions", members = 980, newPosts = 12, isJoined = true),
        Community("4", "Study Together", "Let's learn and help each other", members = 2100, newPosts = 30, isJoined = true)
    )

    val notifications = listOf(
        Notification("1", NotificationType.LIKE, users[1], "liked your post", "2m"),
        Notification("2", NotificationType.COMMENT, users[1], "commented on your post: \"Great work team! 🔥\"", "5m"),
        Notification("3", NotificationType.FOLLOW, users[2], "started following you", "10m"),
        Notification("4", NotificationType.LIKE, users[2], "liked your post", "15m"),
        Notification("5", NotificationType.MENTION, users[3], "mentioned you in a post", "20m"),
        Notification("6", NotificationType.SYSTEM, users[0], "Welcome to Elimu Social! 🎉", "1h", isRead = true)
    )
}
