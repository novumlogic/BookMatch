package api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import model.BookDetails
import model.ChatHistory
import model.Message
import model.OpenAIRequest
import model.OpenAIResponse
import model.ResponseFormat
import model.Usage
import utils.Constants
import utils.extractBookDetails

object OpenAIClient {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
    }
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            level = LogLevel.BODY
        }
    }

    private val systemInstruction = """
        You are a book recommending expert with knowledge about all books and specialization in recommending them.

        When provided with genres, you will give book recommendations for each genre separately, including:
       
        Book name
        Author name
        Genre tags
        2-line description
        no of pages
        ISBN 
        First publication date
        Good reads link for more information
        
        For example, if the user says Fiction, Non-fiction, History, provide the list of fictional books followed by non-fictional and then historical books.
        User preferences, such as liked or disliked books and personal ratings (1-5), will influence future recommendations.
        For three or fewer genres, provide at least 3 books or more per genre.
        
        JSON output format:
        {
            "fiction": [ 
               {
                    "book_name": "The Gatsby",
                    "author_name": "Scott Fitz",
                    "genre_tags": ["tag1", "tag2"],
                    "description": "A Short description.",
                    "pages": 180,
                    "isbn": "9780743273565",
                    "first_date_of_publication": "1925-04-10",
                    "reference_link": "https://www.goodreads.com/book/show/4671.The_Great_Gatsby"
                }
            ],
            "horror": [ ]
        }
        Ensure new recommendations are unique by checking previous suggestions.
    """.trimIndent()

    private val requestBody = OpenAIRequest(
        model = "gpt-3.5-turbo",
        responseFormat = ResponseFormat("json_object"),
        mutableListOf(Message("system", systemInstruction))
    )

    private var inputTokens = 0L
    private var outputTokens = 0L


    fun addUserMessage(userMessage: String){
        requestBody.messages.add(1,Message("user",userMessage))
    }

    fun addModelMessage(modelMessage: String) {
        requestBody.messages.add(Message("assistant",modelMessage))
    }


    fun clearChatHistory() {
        requestBody.messages.clear()
        requestBody.messages.add(0,Message("system", systemInstruction))
    }

    suspend fun generateContent(text: String): Map<String, List<BookDetails>>? {

        requestBody.messages.add(1, Message("user", text))

        println(" To check the messages : ${requestBody.messages.joinToString("\n")}")

        return try {
            val response: HttpResponse = client.post(Constants.OPENAI_CHAT_COMPLETION_URL) {
                header("Authorization", Constants.OPENAI_API_KEY)
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            if (response.status == HttpStatusCode.OK) {
                val responseBody = response.body<String>()

                println(responseBody)
                val rStr = json.decodeFromString<OpenAIResponse>(responseBody)
                val bookDetails = rStr.extractBookDetails()

                trackUsage(rStr.usage)
                bookDetails
            } else {
                null
            }

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun trackUsage(usage: Usage){
        inputTokens += usage.promptTokens
        outputTokens += usage.completionTokens
        println("Total tokens used : Input = $inputTokens and Output = $outputTokens")
    }

    fun optimizeChatHistory() {
        println(" Optimizing chat history: ${requestBody.messages} ")
        val size = requestBody.messages.size
        if(size > 3){
            val savedMessages = mutableListOf<Message>()
            for(i in 0..<size){
                if(i== 0 || i == size-2 || i == size-1){
                    savedMessages.add(requestBody.messages[i])
                }
            }
            requestBody.messages.clear()
            requestBody.messages.addAll(savedMessages)
            println(" Optimized chat history: ${requestBody.messages} ")
        }
    }

    fun changeChatHistory(list: List<ChatHistory>) {
        val systemMessage = requestBody.messages.first()
        requestBody.messages.clear()
        requestBody.messages.add(systemMessage)
        list.forEach {
            requestBody.messages.add(Message("user", it.userText))
            requestBody.messages.add(Message("assistant", it.aiAnswer))
        }
    }
}