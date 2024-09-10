package api

import data.SupabaseProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import model.BookDetails
import model.ChatHistory
import model.Message
import model.BackendRequest
import model.BackendResponse
import model.Usage
import utils.Constants
import utils.extractBookDetails

object BackendClient {

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

    private val requestBody = BackendRequest(
        accessToken = SupabaseProvider.getAccessToken(),
    )

    private var inputTokens = 0L
    private var outputTokens = 0L


    fun addUserMessage(userMessage: String){
        requestBody.messages.add(0,Message("user",userMessage))
    }

    fun addModelMessage(modelMessage: String) {
        requestBody.messages.add(1,Message("assistant",modelMessage))
    }


    fun clearChatHistory() {
        requestBody.messages.clear()
    }

    suspend fun generateContent(text: String): Map<String, List<BookDetails>>? {

        //if requestbody has chat_history then append else put at the beginning
        if (requestBody.messages.size > 1)
            requestBody.messages.add(2, Message("user", text))
        else
            requestBody.messages.add(0,Message("user",text))

        println(" To check the messages : ${requestBody.messages.joinToString("\n")}")

        return try {
            val response: HttpResponse = client.post(Constants.BOOKMATCH_BACKEND_URL) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            if (response.status == HttpStatusCode.OK) {
                val responseBody = response.body<BackendResponse>()

                println(responseBody)
                val bookDetails = responseBody.extractBookDetails()

                trackUsage(responseBody.usage)
                bookDetails
            } else {
                println(requestBody)
                println("${response.bodyAsText()}, $response")
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


    fun changeChatHistory(list: List<ChatHistory>) {
        requestBody.messages.clear()
        list.forEach {
            requestBody.messages.add(Message("user", it.userText))
            requestBody.messages.add(Message("assistant", it.aiAnswer))
        }
    }
}