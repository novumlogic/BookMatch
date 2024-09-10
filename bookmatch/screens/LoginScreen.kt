package com.novumlogic.bookmatch.screens

import android.app.Activity
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.novumlogic.bookmatch.R
import com.novumlogic.bookmatch.ui.light_orange
import com.novumlogic.bookmatch.utils.GoogleSignInHelper
import com.novumlogic.bookmatch.utils.Utils
import data.SupabaseProvider
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(signInHelper: GoogleSignInHelper, onLogin: () -> Unit, modifier: Modifier = Modifier) {
    var showProgress by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    val signInResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val credentials = Identity.getSignInClient(context).getSignInCredentialFromIntent(result.data)
                val googleIdToken = credentials.googleIdToken

                if (googleIdToken != null) {
                    scope.launch {
                        try {
                            SupabaseProvider.signInWithToken(googleIdToken)
                            // Handle successful sign-in
                            onLogin()
                        } catch (e: RestException) {
                            // Handle RestException thrown by Supabase
                            Toast.makeText(context, "SignIn failed $e", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            // Handle unknown exceptions
                            Toast.makeText(context, "SignIn failed $e", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("GoogleSignInButton", "No ID token!")
                    Toast.makeText(context, "No ID token", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Log.e("GoogleSignInButton", "SignIn failed: ${e.statusCode}")
                Toast.makeText(context, "SignIn failed ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        } else{
            Toast.makeText(context, "Login cancelled", Toast.LENGTH_SHORT).show()
        }
        showProgress = false
    }

    Scaffold (modifier = modifier
        .fillMaxSize()
        .background(light_orange)
    , snackbarHost = { SnackbarHost(snackBarHostState) }) { padding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            Image(
                painter = painterResource(R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(248.dp)
                    .weight(0.45f),
                alignment = Alignment.BottomCenter
            )
            Text(
                text = stringResource(R.string.label_login_screen),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(0.10f)
            )

            Box(
                modifier = Modifier.weight(0.45f),
                contentAlignment = Alignment.Center
            ) {
                
                OutlinedButton(
                    modifier = Modifier
                        .widthIn(280.dp)
                        .heightIn(40.dp),
                    onClick = {
                        if (Utils.isNetworkAvailable(context)) {
                            showProgress = true
                            signInHelper.signIn( context as ComponentActivity,signInResultLauncher)
                        } else {
                            scope.launch {
                                if (snackBarHostState.currentSnackbarData == null) {

                                val result = snackBarHostState.showSnackbar(
                                    message = "Please connect to Internet to proceed",
                                    actionLabel = "Settings",
                                    withDismissAction = true,
                                    duration = SnackbarDuration.Indefinite
                                )

                                when (result) {
                                    SnackbarResult.ActionPerformed -> {
                                        val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                                        context.startActivity(intent)
                                    }

                                    SnackbarResult.Dismissed -> {
                                        //action for snackbar being dismissed
                                    }
                                }
                                }
                            }
                        }
                    },
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_google),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = stringResource(R.string.label_login_with_google),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                if (showProgress) {
                    CircularProgressIndicator(modifier = Modifier
                        .padding(bottom = 50.dp)
                        .align(Alignment.BottomCenter))
                }
            }

        }


    }

}