package com.novumlogic.bookmatch.screens.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.OpenAIClient
import com.novumlogic.bookmatch.screens.FetchSource
import com.novumlogic.bookmatch.screens.RecommendationStatus
import com.novumlogic.bookmatch.screens.datastore
import com.novumlogic.bookmatch.screens.dispatcherIO
import com.novumlogic.bookmatch.utils.GoogleSignInHelper
import com.novumlogic.bookmatch.utils.PreferenceKeys
import data.RemoteDataSource
import data.SupabaseProvider
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.BookDetails
import model.BookHistory
import model.Books
import model.ChatHistory
import model.Recommendations
import model.RecommendedBooks

class MainViewModel(
    val signInHelper: GoogleSignInHelper,
) : ViewModel() {

    private val TAG = javaClass.simpleName
    var splashShown by mutableStateOf(false)
    val categoryList = mutableStateListOf<Pair<String, String>>()

    private val _bookRecommendations: MutableStateFlow<Map<String, List<BookDetails>>> =
        MutableStateFlow(
            emptyMap()
        )
    val bookRecommendations: StateFlow<Map<String, List<BookDetails>>> = _bookRecommendations

    private lateinit var categoryMap: Map<String, Int>
    var fetchSource = FetchSource.SUPABASE

    init {
        viewModelScope.launch {
            fetchChatHistory()
            fetchCategories()
        }
    }

    private suspend fun fetchChatHistory() {
            val userId = getCurrentUserId()
            if (userId.isEmpty()) return
            val result = RemoteDataSource.fetchChatHistory(userId)
            result?.let { chatHistoryList ->
                OpenAIClient.clearChatHistory()
                chatHistoryList.forEach {
                    OpenAIClient.addUserMessage(it.userText)
                }
            }?: run {
                Log.d(TAG, "fetchChatHistory: fetch failed")
            }
    }


    private suspend fun fetchCategories() {

            val result = RemoteDataSource.fetchCategories()

            result?.let { data ->
                categoryList.addAll(data.map {
                    it.categoryName to it.categoryEmoji
                })
                Log.d(TAG, "fetchCategories called, its size = ${categoryList.size}")
                categoryMap = data.associate { it.categoryName.lowercase() to it.categoryId }

            } ?: run {
                categoryList.clear()
            }

    }

    fun isCategoryShown(context: Context): Flow<Boolean> {
        return context.datastore.data.map { pref ->
            pref[PreferenceKeys.CATEGORY_SHOWN] ?: false
        }
    }

    suspend fun setCategoryShown(context: Context, value: Boolean, updateRemote: Boolean = true) {
            val userId = getCurrentUserId()
            if(updateRemote) RemoteDataSource.updateCategoryShown(userId,value)
            context.datastore.edit { pref ->
                pref[PreferenceKeys.CATEGORY_SHOWN] = value
            }
    }

    fun isUserLogged(context: Context): Flow<Boolean> {
        return context.datastore.data.map { pref ->
            pref[PreferenceKeys.USER_LOGGED] ?: false
        }
    }

    fun setUserLogged(context: Context, value: Boolean) {
        viewModelScope.launch {
            context.datastore.edit {
                it[PreferenceKeys.USER_LOGGED] = value
            }
        }
    }

    fun getSelectedCategoryList(context: Context): Flow<List<String>> {
        return context.datastore.data.map { pref ->
            val encodedStr = pref[PreferenceKeys.CATEGORY_LIST] ?: ""
            if (encodedStr.isEmpty()) emptyList() else encodedStr.split(",")
        }
    }

    fun setSelectedCategoryList(context: Context, list: List<String>) {
        viewModelScope.launch {
            val userId = getCurrentUserId()
            val encodedStr = list.joinToString(",")
            RemoteDataSource.updateSelectedCategories(userId,encodedStr)
            context.datastore.edit {
                it[PreferenceKeys.CATEGORY_LIST] = encodedStr
            }
        }
    }

    private suspend fun setSelectedCategoryList(context: Context, encodedList: String, updateRemote: Boolean = true) {
            val userId = getCurrentUserId()
            if (updateRemote) RemoteDataSource.updateSelectedCategories(userId,encodedList)
            context.datastore.edit {
                it[PreferenceKeys.CATEGORY_LIST] = encodedList
            }
    }

    fun getLastRecommendationTime(context: Context): Flow<Long> {
        return context.datastore.data.map { it[PreferenceKeys.LAST_RECOMMENDATION_TIME] ?: 0 }
    }

    suspend fun setLastRecommendationTime(context: Context, timestamp: Long, updateRemote: Boolean = true) {
            val userId = getCurrentUserId()
            if(updateRemote) RemoteDataSource.updateLastRecommendationTime(userId, timestamp)
            context.datastore.edit {
                it[PreferenceKeys.LAST_RECOMMENDATION_TIME] = timestamp
            }
    }

    suspend fun saveCurrentUser(context: Context): Boolean {
        return SupabaseProvider.getCurrentUserOrNull()?.let { userInfo: UserInfo ->

            val user = RemoteDataSource.userExists(userInfo.id)
            Log.d(TAG, "saveCurrentUser: The user is $user")
            if (user != null) {
                user.categoryShown?.let { setCategoryShown(context,it,false) }
                user.lastRecommendationTime?.let { setLastRecommendationTime(context,it,false) }
                user.selectedCategories?.let {
                    setSelectedCategoryList(context,it,false)
                }
                true
            } else {
                val newUser = SupabaseProvider.decodeUser(userInfo)
                RemoteDataSource.createUser(newUser)
            }

        } ?: run {
            false
        }
    }


    private fun clearTokens(context: Context) {
        viewModelScope.launch {
            context.datastore.edit {
                it.remove(PreferenceKeys.REFRESH_TOKEN)
                it.remove(PreferenceKeys.ACCESS_TOKEN)
            }
        }
    }

    suspend fun signOut(context: Context) {
        fetchSource = FetchSource.AI
        setUserLogged(context, false)
        clearUserPreferences(context)
        clearTokens(context)
        SupabaseProvider.signOut()
        signInHelper.signOut()

    }

    private suspend fun clearUserPreferences(context: Context){
            context.datastore.edit {
                it.remove(PreferenceKeys.CATEGORY_SHOWN)
                it.remove(PreferenceKeys.CATEGORY_LIST)
                it.remove(PreferenceKeys.LAST_RECOMMENDATION_TIME)
        }
    }

    suspend fun getBookRecommendation(

        selectedCategories: List<String>,
        lastRecommendationTime: Long,
        setLastRecommendationTime: suspend (Long) -> Unit

    ): RecommendationStatus {

        OpenAIClient.clearChatHistory()

        val categoriesStr = selectedCategories.joinToString(",")
        val result = OpenAIClient.generateContent(categoriesStr)

        var status = RecommendationStatus.FAILED
        result?.let {

            _bookRecommendations.value = it
            val bookHistoryList = it.entries.map { BookHistory(it.key, it.value) }

            insertBooks(bookHistoryList, lastRecommendationTime)

            val userId = getCurrentUserId()
            if (userId.isEmpty()) {
                Log.d(
                    TAG,
                    "getBookRecommendation: userid is $userId so chat history cannot be added as well as model message in gemini api failed"
                )
                return RecommendationStatus.FAILED
            }

            val modelMessage = getModelMessage(bookHistoryList)
            val chatHistory = ChatHistory(userId, categoriesStr, modelMessage, lastRecommendationTime)
            RemoteDataSource.addChatHistory(chatHistory)
            setLastRecommendationTime(lastRecommendationTime)

            status = RecommendationStatus.LOADED

        }?: run {
            _bookRecommendations.value = emptyMap()
            status = RecommendationStatus.FAILED
        }
        return status
    }

    private fun getModelMessage(bookHistoryList: List<BookHistory>) =
        bookHistoryList.joinToString(", ") {
            " ${it.genre} : [ ${it.list.joinToString(",") { bookDetails -> bookDetails.bookName }} ] "
        }

    private suspend fun getCurrentUserId(): String {
        return SupabaseProvider.getCurrentUserId()
    }

    private fun insertBooks(bookHistoryList: List<BookHistory>, lastRecommendationTime: Long) {

        viewModelScope.launch(dispatcherIO) {
            val bookList = mutableListOf<Books>()
            bookHistoryList.forEach {
                val categoryId = categoryMap[it.genre.lowercase()] ?: 1
                bookList.addAll(it.list.map { bookDetails ->
                    Books(
                        bookDetails.bookName,
                        bookDetails.authorName,
                        bookDetails.genreTags,
                        categoryId,
                        bookDetails.description,
                        bookDetails.pages,
                        bookDetails.isbn,
                        bookDetails.firstDateOfPublication,
                        bookDetails.referenceLink
                    )
                })
            }


            val insertResult = RemoteDataSource.addBooks(bookList.toList())

            insertResult?.let { bulkInsertionResult ->
                Log.d(
                    TAG,
                    " The inserted rows [ ${bulkInsertionResult.insertedBooksList.joinToString(",")} ]: ${bulkInsertionResult.insertedBooksList.size}\n duplicated rows [ ${
                        bulkInsertionResult.duplicateBookList.joinToString(
                            ","
                        )
                    } ]: ${bulkInsertionResult.duplicateBookList.size}"
                )

                val userId = getCurrentUserId()
                if (userId.isEmpty()) {
                    Log.d(
                        TAG,
                        "insertBooks: userId is  $userId hence cannot add data in recommendations table which needs it "
                    )
                    return@launch
                }
                val recommendationsInsert = RemoteDataSource.addRecommendation(
                    Recommendations(
                        userId,
                        lastRecommendationTime
                    )
                )


                recommendationsInsert?.let { recommendation ->
                    val recommendedBookList =
                        bulkInsertionResult.insertedBooksList.plus(bulkInsertionResult.duplicateBookList)
                            .map {
                                RecommendedBooks(
                                    recommendation.id,
                                    it.bookId
                                )
                            }


                    val recommendedBookResult =
                        RemoteDataSource.addRecommendedBooks(recommendedBookList)

                    recommendedBookResult?.let { recommendedBookListResult ->

                        val rMap = recommendedBookListResult.associateBy { it.bookId }
                        val bMap =
                            bulkInsertionResult.insertedBooksList.plus(bulkInsertionResult.duplicateBookList)
                                .associateBy { it.bookName }

                        _bookRecommendations.value.values.flatten().forEach { bookDetails ->

                            bMap[bookDetails.bookName]?.bookId?.let { bookId ->
                                rMap[bookId]?.id?.let { recommendedBookId ->
                                    bookDetails.recommendedBookId = recommendedBookId
                                }

                            }
                        }

                        Log.d(TAG, "The recommended book list is $recommendedBookListResult")

                    }?: run {

                        Log.d(TAG, "The insertion operation of recommended book list failed  ")

                    }

                }?: run {

                    Log.d(TAG, "The recommendation insertion operation failed")

                }

            }?: run {

                Log.d(TAG, "The bulk insertion operation failed ")
            }
        }
    }

    suspend fun recommendMore(
        liked: List<String>,
        disliked: List<String>,
        rating: Map<String, Int>,
        totalBooks: List<String>,
        selectedCategories: List<String>,
        lastRecommendationTime: Long,
        setLastRecommendationTime: suspend (Long) -> Unit
    ): RecommendationStatus {
        val userText = mutableListOf<String>()
        val text = if (liked.isEmpty() && disliked.isEmpty() && rating.isEmpty()) {
            """Suggest me more books in genres: ${
                selectedCategories.joinToString(
                    ",",
                    "[",
                    "]"
                )
            }, do not repeat these books: ${totalBooks.joinToString(",", "[", "]")} """

        } else {
            val remainingBooks = mutableListOf<String>().apply {
                addAll(totalBooks)
            }

            userText.add("Suggest me more books in these ${selectedCategories.joinToString(",","[","]")} ")
            if(rating.isNotEmpty()) {
                userText.add(" My rating $rating ")
                remainingBooks.forEach {
                    if(it in rating.keys) remainingBooks.remove(it)
                }
            }
            if(disliked.isNotEmpty()) {
                userText.add("I dislike ${disliked.joinToString(",","[","]")} ")
                remainingBooks.forEach {
                    if(it in disliked) remainingBooks.remove(it)
                }
            }
            if(liked.isNotEmpty()){
                userText.add("I like ${liked.joinToString(",","[","]")}")
                remainingBooks.forEach {
                    if(it in liked) remainingBooks.remove(it)
                }
            }
            if(remainingBooks.isNotEmpty()){
                userText.add(userText.size-1,"Do not repeat the mentioned books and ${remainingBooks.joinToString(",","[","]")}")
            }else{
                userText.add(userText.size-1,"Do not repeat the mentioned books.")
            }

            userText.joinToString(". ")
        }

        val result = OpenAIClient.generateContent(text)

        var status = RecommendationStatus.FAILED
        result?.let {

            _bookRecommendations.value = it
            val bookHistoryList = it.entries.map { BookHistory(it.key, it.value) }

            val modelMessage = getModelMessage(bookHistoryList)
            insertBooks(bookHistoryList, lastRecommendationTime)


            val userId = getCurrentUserId()
            if (userId.isEmpty()) {
                Log.d(TAG,"getBookRecommendation: userid is $userId so chat history cannot be added as well as model message in gemini api failed")
                return RecommendationStatus.FAILED
            }

            val chatHistory = ChatHistory(userId, text, modelMessage, lastRecommendationTime)
            RemoteDataSource.addChatHistory(chatHistory)
            setLastRecommendationTime(lastRecommendationTime)

            status = RecommendationStatus.LOADED

        }?: run {
            Log.d(TAG, "recommendMore: operation failed")
            _bookRecommendations.value = emptyMap()
            status = RecommendationStatus.FAILED

        }
        return status

    }


    suspend fun isSessionValid(context: Context) {
        SupabaseProvider.loadSession() ?: signOut(context)
    }

    suspend fun fetchBooksFromDb(
        lastRecommendationTime: Long,
    ): RecommendationStatus {
        var status = RecommendationStatus.FAILED

        val userId = getCurrentUserId()
        Log.d(
            TAG,
            "fetchBooksFromDb: Last recommendation time = $lastRecommendationTime and userId = $userId"
        )

        RemoteDataSource.fetchBooks(userId,lastRecommendationTime)?.let {
            _bookRecommendations.value = it
            status = RecommendationStatus.LOADED
        } ?: run {
            status = RecommendationStatus.FAILED
        }

        return status

    }

    suspend fun fetchRecommendationTimestamp(): List<Recommendations>? {
        val userId = getCurrentUserId()
        val result = RemoteDataSource.fetchRecommendationTimestamp(userId)
        var list: List<Recommendations>? = null
        result?.let {
            list = it
        }?: run {
            Log.d(TAG, "Error while fetching recommendation timestamps")
        }
        return list
    }

    suspend fun fetchBooksFromRecommendationId(id: Int, timestamp: Long): RecommendationStatus {
        var status = RecommendationStatus.FAILED
        withContext(dispatcherIO) {
            val chatHistoryResult = RemoteDataSource.fetchChatHistory(timestamp)
            val result = RemoteDataSource.fetchBooks(id)
            result?.let {
                chatHistoryResult?.let { list ->
                    OpenAIClient.changeChatHistory(list)
                }?: run {
                    Log.d(TAG, "fetchBooksFromId failed to change chat history")
                }
                _bookRecommendations.value = it
                status = RecommendationStatus.LOADED
            }?: run {
                status = RecommendationStatus.FAILED
            }
        }
        return status

    }

    fun reloadInit() {
        viewModelScope.launch {
            fetchCategories()
            fetchChatHistory()
        }
    }


}
