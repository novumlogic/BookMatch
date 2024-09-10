package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class RecommendationData(
    val data: List<Datum>
)

@Serializable
data class Datum(
    val genre: String,
    val list: List<BookDetails>
)

@Serializable
data class BookDetails(
    @SerialName("book_name")
    val bookName: String,
    @SerialName("author_name")
    val authorName: String,
    @SerialName("genre_tags")
    val genreTags: List<String>,
    val description: String,
    val pages: String,
    var isbn: String,
    @SerialName("first_date_of_publication")
    val firstDateOfPublication: String?,
    @SerialName("category_id") var categoryId: Int = 1,
    @SerialName("category_name") var categoryName: String = "",
    @SerialName("recommended_book_id") var recommendedBookId: Int? = null,
    var liked: Boolean? = null,
    var rating: Int? = null,
    var read: Boolean = false,
    @SerialName("last_updated_time") val lastUpdatedTime: Long = 0L
)

@Serializable
data class RpcParams(
    @SerialName("p_user_id")
    val userId: String,
    @SerialName("p_timestamp")
    val timestamp: Long
)

data class BookHistory(
    val genre: String,
    val list: List<BookDetails>
)
