package api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import model.BookDetails
import model.BookHistory
import model.Content
import model.GeminiApiRequest
import model.GeminiApiResponse
import model.GenerationConfig
import model.Part
import model.ResponseSchema
import model.SchemaProperty
import model.SystemInstruction
import model.Types
import utils.Constants
import utils.extractBookDetails


object GeminiClient {
    private val content: StringBuilder = StringBuilder()

    private val systemPrompt = """
    You are a book recommending expert, who has knowledge about all books and specialization in recommending them. 
    
   You provide different lists of books based on each genres separately provided by user where each book's name, author name, genre tags applicable on the book, 2 lines of description, no of pages, isbn , first date of publication, reference link for the book (if user wants to know more) should be included. 
   
   
   For example when user says Fiction, Non-fiction, History then provide the list of fictional books followed by non-fictional followed by historical books. 
   
   Further the user will provides the name of the book he likes and dislikes, he may also provide his personal rating for particular books between 1 to 5. 
   
   So you need to provide next recommendations based on this likes, dislikes and ratings.

    Json output should be object having different genre associated with list of books, for eg. {
        "fiction": [ list of books
        ],
        "horror": [list of books
        ], ...
    }, each book object should have following keys: book_name, author_name, genre_tags (should be list of tags), description, pages, isbn, first_date_of_publication, reference_link.
     
     Remember if you are unsure about any value corresponding to the attribute then pass null, do not assume the value.
     Also provide new unique recommendation of the book by checking the previous recommended books from your conversation history.
     
    """.trimIndent()

    private val requestBody = GeminiApiRequest(
        mutableListOf(), SystemInstruction(
            mutableListOf(Part(text = systemPrompt))
        ),
        GenerationConfig(
            "application/json", responseSchema = ResponseSchema(
                type = "object", properties = mapOf(
                    "book_name" to SchemaProperty("string", "The name of the book"),
                    "author_name" to SchemaProperty("string", "The name of the author"),
                    "genre_tags" to SchemaProperty(
                        "array",
                        "List of genre tags applicable on the book",
                        items = Types("string")
                    ),
                    "description" to SchemaProperty(
                        "string",
                        "A comprehensive description of the book"
                    ),
                    "pages" to SchemaProperty("integer", "The number of pages in the book"),
                    "isbn" to SchemaProperty("string", "The ISBN of the book"),
                    "first_date_of_publication" to SchemaProperty(
                        "string",
                        "The first date of publication of the book."
                    ),
                    "reference_link" to SchemaProperty(
                        "string",
                        "A reference link to know more about book."
                    ),

                    ), required = listOf(
                    "book_name",
                    "author_name",
                    "genre_tags",
                    "description",
                    "pages",
                    "isbn",
                    "first_date_of_publication",
                    "reference_link"
                )
            )
        )
    )
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

    private var totalTokensUsed = 0L

    fun addUserMessage(userMessage: String){
        requestBody.contents.add(Content("user", listOf(Part(userMessage))))
    }

    fun addModelMessage(modelMessage: String) {
        requestBody.contents.add(Content("model", listOf(Part(modelMessage))))
    }

    fun addBookHistory(bookHistoryList: List<BookHistory>) {
        bookHistoryList.forEach {
            content.append(" Genre: ${it.genre} => Books: ${it.list.joinToString(",")} ,")
        }
    }

    fun clearChatHistory() {
        requestBody.contents.clear()
    }

    suspend fun generateContent(text: String): Result<Map<String, List<BookDetails>>> {
        requestBody.contents.add(Content("user", listOf(Part(text))))

        println(" To check the content: ${requestBody.contents.joinToString("\n")}")

        val url = "${Constants.FLASH_API_URL}${Constants.GEMINI_API_KEY}"
        return try {
            val response: HttpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            if (response.status == HttpStatusCode.OK) {
                val responseBody = response.body<String>()

                println(responseBody)
                val rStr = json.decodeFromString<GeminiApiResponse>(responseBody)
                val bookDetails = rStr.extractBookDetails()
                Result.success(bookDetails)
            } else {
                Result.failure(Exception("Status code: ${response.status} and ${response.body<String>()}"))
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }

    }
}
