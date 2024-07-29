package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIRequest(
    val model: String,
    @SerialName("response_format")
    val responseFormat: ResponseFormat,
    val messages: MutableList<Message>,
)

@Serializable
data class ResponseFormat(
    val type: String,
)

@Serializable
data class Message(
    val role: String,
    val content: String,
)