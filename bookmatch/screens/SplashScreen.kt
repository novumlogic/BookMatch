package com.novumlogic.bookmatch.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.novumlogic.bookmatch.R
import kotlinx.coroutines.delay
import com.novumlogic.bookmatch.ui.light_orange

@Composable
fun SplashScreen(onSplashShown: suspend  () -> Unit, modifier: Modifier = Modifier) {
    val currentSplashShown by rememberUpdatedState(onSplashShown)
    LaunchedEffect(Unit) {
        delay(2000)
        currentSplashShown()
    }
    Surface( color = light_orange,
        modifier = modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box( modifier = Modifier.heightIn(268.dp)){
                Image(
                    painter = painterResource(R.drawable.app_logo), contentDescription = "App Logo",
                    modifier = Modifier.size(248.dp).align(Alignment.TopCenter),
                )
                Text(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    text = stringResource(R.string.label_book_match),
                    style = MaterialTheme.typography.displayMedium.copy(color = MaterialTheme.colorScheme.primary)
                )

            }
        }
    }

}