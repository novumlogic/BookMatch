package model

import kotlinx.serialization.Serializable

@Serializable
data class GeminiApiResponse(
    val candidates: List<Candidate>?,
    val usageMetadata: UsageMetadata,
)

@Serializable
data class Candidate(
    val content: Content?,
    val finishReason: String,
    val index: Long,
    val safetyRatings: List<SafetyRating>?,
)

@Serializable
data class SafetyRating(
    val category: String,
    val probability: String,
)

@Serializable
data class UsageMetadata(
    val promptTokenCount: Long,
    val candidatesTokenCount: Long,
    val totalTokenCount: Long,
)