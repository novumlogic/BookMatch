package utils

object Constants {
    const val SUPABASE_KEY = "your_supabase_anon_key"
    const val SUPABASE_URL = "your_supabase_url"
    const val OAUTH_WEB_CLIENT_ID = "your_oauth_web_client_id"
    const val OPEN_LIBRARY_API_URL = "https://covers.openlibrary.org/b/isbn"
    //change the url with your hosted link or if running locally then http://IP_ADDRESS:8080/generate-recommendations
    //also remove usesCleartextTraffic from AndroidManifest file in Android module when not using local api
    //and set Allow Arbitrary Loads to No for App Transport Security Settings in Info.plist for iOS
    const val BOOKMATCH_BACKEND_URL = "http://localhost:8080/generate-recommendations"
}
