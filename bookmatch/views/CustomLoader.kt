package com.novumlogic.bookmatch.views

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novumlogic.bookmatch.R
import kotlinx.coroutines.delay

@Composable
fun CustomLoader(modifier: Modifier = Modifier) {
    var alpha by rememberSaveable { mutableFloatStateOf(1f) }
    var size by rememberSaveable { mutableIntStateOf(100) }

    val animatedAlpha by animateFloatAsState(
        alpha, animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "alpha_transition"
    )

    val animationSize by animateIntAsState(
        size, animationSpec =
        infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "size_transition"
    )

    LaunchedEffect(Unit) {
        alpha = if (alpha == 1f) 0.4f else 1f
        size = if (size == 100) 120 else 100
        delay(1000)
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.app_logo),
            modifier = Modifier
                .alpha(animatedAlpha)
                .size(animationSize.dp),
            contentDescription = "App loading icon"
        )
        Text(
            stringResource(R.string.label_loading_books),
            modifier = Modifier.padding(8.dp),
            fontSize = 24.sp,
            style = MaterialTheme.typography.titleMedium.copy(
                brush = SolidColor(Color.White)
            ),
        )
    }
}