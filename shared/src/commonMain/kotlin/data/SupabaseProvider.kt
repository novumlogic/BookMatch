package data

import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonPrimitive
import model.Users

object SupabaseProvider {

    suspend fun signInWithToken(token: String){
        supabaseClient.auth.signInWith(IDToken){
            idToken = token
            provider = Google
        }
    }

    suspend fun signOut() = supabaseClient.auth.signOut()

    suspend fun getCurrentUserId(): String {
        if (supabaseClient.auth.sessionStatus.value == SessionStatus.LoadingFromStorage) {
            delay(500)
        }
        return supabaseClient.auth.currentUserOrNull()?.id ?: ""
    }

    suspend fun loadSession() = supabaseClient.auth.sessionManager.loadSession()

    fun getCurrentUserOrNull() = supabaseClient.auth.currentUserOrNull()

    fun decodeUser(userInfo: UserInfo): Users{
        val metadata = Json.decodeFromJsonElement<JsonObject>(
            userInfo.userMetadata ?: JsonObject(mapOf())
        )
        val avatarUrl = metadata["avatar_url"]?.jsonPrimitive?.content ?: ""
        val displayName = metadata["name"]?.jsonPrimitive?.content ?: ""
        val email = userInfo.email ?: ""
        return Users(userInfo.id, email, displayName, avatarUrl)

    }

    fun getAccessToken() : String{
        return supabaseClient.auth.currentAccessTokenOrNull() ?: run {
            println("No access token in current session")
            ""
        }
    }
}