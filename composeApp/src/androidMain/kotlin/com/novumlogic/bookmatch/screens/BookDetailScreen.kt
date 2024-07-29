package com.novumlogic.bookmatch.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.novumlogic.bookmatch.R
import com.novumlogic.bookmatch.views.BookTitleAndAuthor
import com.novumlogic.bookmatch.views.GenreTagContainer
import com.novumlogic.bookmatch.views.ImageHolder
import com.novumlogic.bookmatch.views.RatingBarContainer
import com.novumlogic.bookmatch.views.ReadStatusButton
import model.BookDetails

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    book: BookDetails,
    bookList: List<BookDetails>,
    onBookSelected: (BookDetails) -> Unit,
    onRatingChange: (Int, String, Int?) -> Unit,
    onRead: (Int, Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var rating by remember { mutableStateOf(book.rating) }
    var read by remember { mutableStateOf(book.read) }

    val remainingList: List<BookDetails> = mutableListOf<BookDetails>().apply {
        addAll(bookList)
        remove(book)
    }

    Surface(modifier = modifier)
    {
        BackHandler {
            onBack()
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(
                    rememberScrollState()
                )
        ) {
            TopAppBar(
                title = {
                    Text(book.bookName, maxLines = 2, overflow = TextOverflow.Ellipsis)
                },
                actions = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.Clear, null)
                    }
                }
            )

            ImageHolder(
                book.isbn,
                ContentScale.Fit,
                Modifier.requiredSize(200.dp)
            )

            BookTitleAndAuthor(
                book.bookName,
                book.authorName,
                Modifier.padding(top = 22.dp, bottom = 7.dp, start = 16.dp, end = 16.dp)
            )

            ReadStatusButton(read, onChoice = { choice ->

                book.recommendedBookId?.let {
                    read = if (read != choice) choice else false
                    book.read = read
                    onRead(it, read)

                    if (!read) {
                        rating = null
                        book.rating = null
                        onRatingChange(it, book.bookName, null)
                    }
                } ?: Toast.makeText(context, "Wait data is being processed", Toast.LENGTH_SHORT)
                    .show()

            }, Modifier.size(214.dp, 48.dp))

            RatingBarContainer(
                rating, onRatingChange = { ratingValue ->

                    if (!read) {
                        Toast.makeText(
                            context,
                            "Please change your status to Read in order to rate",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        book.recommendedBookId?.let { recommendedBookId ->
                            rating = ratingValue
                            book.rating = rating
                            onRatingChange(recommendedBookId, book.bookName, rating)

                        } ?: Toast.makeText(
                            context,
                            "Wait data is being processed",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                },
                Modifier.padding(vertical = 22.dp)
            )

            GenreTagContainer(book.genreTags)

            Text(
                text = book.description,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 35.dp)
            )

            if (remainingList.isNotEmpty()) {

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = stringResource(R.string.label_recommendation_on_your_books),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(vertical = 25.dp)
                )


                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 30.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    items(remainingList) { book ->
                        HorizontalBookItem(book, { onBookSelected(book) })
                    }
                }

            }


        }
    }

}


@Composable
fun HorizontalBookItem(
    book: BookDetails,
    onBookSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.size(265.dp, 90.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        onClick = onBookSelected,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    book.bookName,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    book.authorName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            ImageHolder(book.isbn, contentScale = ContentScale.Fit)

        }
    }

}
