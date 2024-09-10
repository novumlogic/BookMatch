package com.novumlogic.bookmatch.utils

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import utils.Constants

class GoogleSignInHelper(private val context: Context) {

    private val oneTapClient by lazy {
        Identity.getSignInClient(context)
    }

    private val signInRequest: BeginSignInRequest by lazy {
        BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(Constants.OAUTH_WEB_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
    }

    fun signIn(activity: ComponentActivity, signInResultLauncher: ActivityResultLauncher<IntentSenderRequest>) {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(activity) { result ->
                val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                signInResultLauncher.launch(intentSenderRequest)
            }
            .addOnFailureListener(activity) { e ->
                Log.e("GoogleSignInHelper", "Error: ${e.localizedMessage}")
            }
    }

    fun signOut() {
        oneTapClient.signOut()
    }

}
