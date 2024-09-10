package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BackendRequest(
    @SerialName("access_token")
    val accessToken: String,
    val messages: MutableList<Message> = mutableListOf(),
)

@Serializable
data class Message(
    val role: String,
    val content: String,
)