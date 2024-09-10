package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BackendResponse(
    val id: String,
    @SerialName("object")
    val objectField: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage,
    @SerialName("system_fingerprint")
    val systemFingerprint: String?,
)

@Serializable
data class Choice(
    val index: Long,
    val message: Message,
    val logprobs: Int?,
    @SerialName("finish_reason")
    val finishReason: String,
)

@Serializable
data class Usage(
    @SerialName("prompt_tokens")
    val promptTokens: Long,
    @SerialName("completion_tokens")
    val completionTokens: Long,
    @SerialName("total_tokens")
    val totalTokens: Long,
)