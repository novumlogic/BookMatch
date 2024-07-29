package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Users(
    @SerialName("user_id") val userId: String,
    val email: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("avatar_url") val avatarUrl: String,
    @SerialName("category_shown") val categoryShown: Boolean? = null,
    @SerialName("selected_categories") val selectedCategories: String? = null,
    @SerialName("last_recommendation_time") val lastRecommendationTime: Long? = null,
    @SerialName("created_at") val createdAt: String = ""

)

@Serializable
data class Categories(
    @SerialName("category_id") val categoryId: Int,
    @SerialName("category_name") val categoryName: String,
    @SerialName("category_emoji") val categoryEmoji : String
)

@Serializable
data class Recommendations(
    @SerialName("user_id") val userId: String,
    val timestamp: Long,
    @SerialName("recommendation_id") val id: Int = 0

)

@Serializable
data class RecommendedBooks(
    @SerialName("recommendation_id") val recommendationId: Int,
    @SerialName("book_id") val bookId: Int,
    val id: Int = 0
)

@Serializable
data class UserInteraction(
    @SerialName("recommendation_book_id") val recommendationBookId: Int,
    var liked: Boolean,
    var rating: Int,
    var read: Boolean,
    @SerialName("last_updated_time") val lastUpdatedTime: Int,
    val id: Int = 0
)


@Serializable
data class Books(
    @SerialName("book_name") val bookName: String,
    @SerialName("author_name") val authorName: String,
    @SerialName("genre_tags") val genreTags: List<String>,
    @SerialName("category_id") val categoryId: Int,
    val description: String,
    val pages: String,
    val isbn: String,
    @SerialName("first_date_of_publication") val firstDateOfPublication: String?,
    @SerialName("reference_link") val referenceLink: String?,
    val id: Int = 0
)


@Serializable
data class BulkInsertResult(
    @SerialName("inserted_books") val insertedBooksList: List<BooksInfo>,
    @SerialName("duplicate_books") val duplicateBookList: List<BooksInfo>,
)

@Serializable
data class BooksInfo(
    @SerialName("book_id") val bookId: Int,
    @SerialName("book_name") val bookName: String
)

@Serializable
data class ChatHistory(
    @SerialName("user_id") val userId: String,
    @SerialName("user_text") val userText: String,
    @SerialName("ai_answer") val aiAnswer: String,
    val timestamp: Long ,
    val id: Int = 0,
)

@Serializable
data class OldUserData(
    val books: List<BookDetails>,
    val timestamp: Long
)

