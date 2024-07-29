package com.novumlogic.bookmatch.views

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.novumlogic.bookmatch.R
import utils.Constants


@Composable
fun ImageHolder(isbn: String, contentScale: ContentScale, modifier: Modifier = Modifier) {
    val url = "${Constants.OPEN_LIBRARY_API_URL}/$isbn-L.jpg"

    SubcomposeAsyncImage(
        model = url,
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale,
    ) {
        val state = painter.state
        if (state is AsyncImagePainter.State.Loading) {
            CircularProgressIndicator(modifier = Modifier.requiredSize(40.dp))
        } else if (state is AsyncImagePainter.State.Error) {
            Icon(
                Icons.Outlined.Warning,
                null,
                modifier = Modifier.requiredSize(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        } else {
            val drawable = (state as AsyncImagePainter.State.Success).result.drawable
            if (drawable.intrinsicWidth > 1 && drawable.intrinsicHeight > 1) {
                SubcomposeAsyncImageContent()
            } else {
                SubcomposeAsyncImageContent(painter = painterResource(R.drawable.no_image), modifier = Modifier
                    .requiredSize(50.dp)
                    .padding(end = 4.dp))
            }
        }
    }

}
