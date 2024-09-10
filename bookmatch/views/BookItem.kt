package com.novumlogic.bookmatch.views

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import model.BookDetails


@Composable
fun BookItem(
    book: BookDetails,
    onBookSelected: () -> Unit,
    onLikeDislike: (Int, String, Boolean?) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onBookSelected,
        modifier = modifier
            .padding(8.dp)
            .width(172.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        val context = LocalContext.current
        var liked by remember(book.bookName) {
            mutableStateOf(
                book.liked
            )
        }

        ImageHolder(
            book.isbn,
            ContentScale.Crop,
            Modifier
                .size(172.dp, 164.dp)
        )

        Text(
            text = book.bookName,
            minLines = 2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, top = 8.dp, end = 12.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = book.authorName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, bottom = 8.dp, end = 12.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LikeDislikeButton(
            liked,
            onLike = {
                book.recommendedBookId?.let {
                    liked = if (liked != true) true else null
                    book.liked = liked
                    onLikeDislike(it, book.bookName, liked)
                } ?: Toast.makeText(context, "Wait the data is being processed", Toast.LENGTH_SHORT)
                    .show()
            },
            onDislike = {
                book.recommendedBookId?.let {
                    liked = if (liked != false) false else null
                    book.liked = liked
                    onLikeDislike(it, book.bookName, liked)
                } ?: Toast.makeText(context, "Wait the data is being processed", Toast.LENGTH_SHORT)
                    .show()

            },
            Modifier.height(40.dp)
        )
    }


}

