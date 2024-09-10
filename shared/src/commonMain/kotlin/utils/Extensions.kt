package utils

import kotlinx.serialization.json.Json
import model.BackendResponse
import model.BookDetails
import model.RecommendationData

fun BackendResponse.extractBookDetails(): Map<String, List<BookDetails>> {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
    }

    val output = choices.first().message.content

    val recommendationData = json.decodeFromString<RecommendationData>(output)

    return recommendationData.data.associate { it.genre to it.list }

    }
