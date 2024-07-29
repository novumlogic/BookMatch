package data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json
import model.BookDetails
import model.Books
import model.BulkInsertResult
import model.Categories
import model.ChatHistory
import model.Recommendations
import model.RecommendedBooks
import model.RpcParams
import model.Users
import utils.Constants

val supabaseClient = createSupabaseClient(
    supabaseUrl = Constants.SUPABASE_URL,
    supabaseKey = Constants.SUPABASE_KEY
) {
    install(Auth)
    install(Postgrest)
    defaultSerializer = KotlinXSerializer(Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
    })
}

object RemoteDataSource {

    suspend fun createUser(users: Users): Boolean {
        return try {
            println("The user is $users")
            supabaseClient.postgrest.from("users").insert(users)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun userExists(userId: String): Users? {
        return try {

            val result = supabaseClient.postgrest.from("users").select {
                filter { eq("user_id", userId) }
                count(Count.EXACT)
            }
            val count = result.countOrNull()!!
            println(" the count is datasource is $count")

            if(count == 0L) null else result.decodeSingle<Users>()

        } catch (e: Exception) {

            e.printStackTrace()
            null

        }
    }


    suspend fun fetchCategories(): List<Categories>? {
        return try {
            supabaseClient.postgrest.from("categories").select().decodeList<Categories>()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun addBooks(books: List<Books>): BulkInsertResult? {
        return try {
           supabaseClient.postgrest.rpc("bulk_insert_books", mapOf("books" to books))
                .decodeAs<BulkInsertResult>()

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun addRecommendation(recommendations: Recommendations): Recommendations? {
        return try {
            val result = supabaseClient.postgrest.from("recommendations").insert(recommendations)
            {
                select()
            }.decodeSingle<Recommendations>()
            println(" The result of addRecommendation() is $result")
            result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun addRecommendedBooks(recommendedBookDtos: List<RecommendedBooks>): List<RecommendedBooks>? {
        return try {

                supabaseClient.postgrest.from("recommended_books").insert(recommendedBookDtos) {
                    select()
                }.decodeList<RecommendedBooks>()

        } catch (e: Exception) {
            e.printStackTrace()
            null

        }
    }

    suspend fun fetchBooks(
        userId: String,
        lastRecommendationTime: Long
    ): Map<String, List<BookDetails>>? {
        return try {

            val result = supabaseClient.postgrest.rpc(
                "fetch_recommended_books",
                RpcParams(userId, lastRecommendationTime)
            ).decodeList<BookDetails>()

            result.groupBy { it.categoryName }

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun fetchBooks(recommendationId: Int): Map<String, List<BookDetails>>? {
        return try {
            val result = supabaseClient.postgrest.rpc(
                "fetch_recommended_books_by_id",
                mapOf("p_id" to recommendationId)
            ).decodeList<BookDetails>()

            result.groupBy { it.categoryName }

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun changeLikeDislike(
        recommendedBookId: Int,
        value: Boolean?,
        currentTimeMillis: Long
    ): Boolean {
        return try {
            supabaseClient.postgrest.from("recommended_books").update(
                {
                    set("liked", value)
                    set("last_updated_time", currentTimeMillis)
                }
            ) {
                filter { eq("id", recommendedBookId) }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateRatings(
        recommendedBookId: Int,
        rating: Int?,
        currentTimeMillis: Long
    ): Boolean {
        return try {
            supabaseClient.postgrest.from("recommended_books").update(
                {
                    set("rating", rating)
                    set("last_updated_time", currentTimeMillis)
                }
            ) {
                filter { eq("id", recommendedBookId) }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun addChatHistory(chatHistory: ChatHistory): Boolean {
        return try {
            supabaseClient.postgrest.from("chat_history").insert(chatHistory)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateReadStatus(
        recommendBookId: Int,
        status: Boolean,
        currentTimeMillis: Long
    ): Boolean {
        return try {
            supabaseClient.postgrest.from("recommended_books").update({
                set("read", status)
                set("last_updated_time", currentTimeMillis)
            }) {
                filter { eq("id", recommendBookId) }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

    suspend fun updateSelectedCategories(userId: String, categories: String): Boolean {
        return try {
            supabaseClient.postgrest.from("users").update({
                set("selected_categories", categories)
            }){
                filter { eq("user_id", userId) }
            }
            true
        }catch (e: Exception){
            e.printStackTrace()
            false
        }
    }

    suspend fun updateLastRecommendationTime(userId: String, timestamp: Long) : Boolean {
        return try {
            supabaseClient.postgrest.from("users").update({
                set("last_recommendation_time", timestamp)
            }) {
                filter { eq("user_id", userId) }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateCategoryShown(
        userId: String,
        value: Boolean
    ): Boolean{
        return try {
            supabaseClient.postgrest.from("users").update({
                set("category_shown", value)
            }) {
                filter { eq("user_id", userId) }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun fetchChatHistory(userId: String): List<ChatHistory>? {
        return try {
            val result = supabaseClient.postgrest.from("chat_history").select {
                filter { eq("user_id", userId) }
                order("timestamp", order = Order.DESCENDING)
                limit(1)
                count(Count.EXACT)
            }
            val count = result.countOrNull()!!
            val list =
                if (count == 0L) emptyList() else if (count == 1L) listOf(result.decodeSingle<ChatHistory>()) else result.decodeList()

            list
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    suspend fun fetchChatHistory(timestamp: Long): List<ChatHistory>? {
        return try {
            val result = supabaseClient.postgrest.from("chat_history").select {
                filter {
                    eq("timestamp", timestamp)
                }
                count(Count.EXACT)
            }
            val count = result.countOrNull()!!
            val list = if (count == 0L) emptyList() else if (count == 1L) listOf(result.decodeSingle<ChatHistory>()) else result.decodeList()

            list
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    suspend fun fetchRecommendationTimestamp(userId: String): List<Recommendations>? {

        return try {

            val result = supabaseClient.postgrest.from("recommendations").select {
                filter { eq("user_id", userId) }
                count(Count.EXACT)
            }
            val list = when {
                result.countOrNull()!! == 0L -> {
                    emptyList()
                }

                result.countOrNull()!! == 1L -> {
                    listOf(result.decodeSingle<Recommendations>())
                }

                else -> {
                    result.decodeList<Recommendations>()
                }
            }
            list

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }


}