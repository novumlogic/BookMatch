package utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import model.BookDetails
import model.GeminiApiResponse
import model.OpenAIResponse

fun GeminiApiResponse.extractBookDetails(): Map<String, List<BookDetails>> {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
    }

    val output = candidates?.first()?.content?.parts?.first()?.text

    val jsonObject = json.decodeFromString<JsonObject>(output ?: "")

    return jsonObject.mapValues { (_, jsonElement) ->
        json.decodeFromJsonElement<List<BookDetails>>(jsonElement)
    }
}

fun OpenAIResponse.extractBookDetails(): Map<String, List<BookDetails>> {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
    }

    val output = choices.first().message.content

    val jsonObject = json.decodeFromString<JsonObject>(output)

    return jsonObject.mapValues { (_, jsonElement) ->
        json.decodeFromJsonElement<List<BookDetails>>(jsonElement)
    }.mapKeys { (genre, _) ->
        genre.replace("_", " ").replace("-"," ")
    }
}