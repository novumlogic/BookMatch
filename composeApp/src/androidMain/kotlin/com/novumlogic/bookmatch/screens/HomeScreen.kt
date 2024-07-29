package com.novumlogic.bookmatch.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novumlogic.bookmatch.R
import com.novumlogic.bookmatch.utils.Utils
import com.novumlogic.bookmatch.views.BookItem
import com.novumlogic.bookmatch.views.HourSelectionDialog
import model.BookDetails
import java.time.Instant
import java.time.ZoneId


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    books: Map<String, List<BookDetails>>,
    timestamp: Map<Long, Int>,
    currentRecommendationTimestamp: Long,
    fetchBooksFromId: (Int, Long) -> Unit,
    filterEnabled: () -> Boolean,
    onBookSelected: (BookDetails, List<BookDetails>) -> Unit,
    onLikeDislike: (Int, String, Boolean?) -> Unit,
    showSnackbar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var openFilterDialog by remember { mutableStateOf(false) }
    var openHourDialog by remember { mutableStateOf(false) }
    val hoursForSelectedDate = remember { mutableMapOf<Long, Int>() }
    val datePickerState =
        rememberDatePickerState(
            selectableDates =
            object : SelectableDates {
                val dayInMillis = 86400000L
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    var isSelectable = false

                    timestamp.keys.forEach { timestamp ->
                        if (timestamp in utcTimeMillis..<(utcTimeMillis + dayInMillis)) {
                            isSelectable = true
                        }
                    }
                    return isSelectable
                }

                override fun isSelectableYear(year: Int): Boolean {
                    val _year = if (timestamp.keys.isEmpty()) Instant.now()
                        .atZone(ZoneId.systemDefault()).year else Instant.ofEpochMilli(
                        timestamp.keys.first()
                    )
                        .atZone(ZoneId.systemDefault()).year

                    return year == _year

                }
            }
        )

    val confirmEnabled by remember {
        derivedStateOf { datePickerState.selectedDateMillis != null }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 10.dp)
                .heightIn(max = 24.dp), horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = {
                    if (Utils.isNetworkAvailable(context)) {
                        openFilterDialog = true
                    } else {
                        showSnackbar()
                    }
                },
                enabled = filterEnabled()
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_filter_alt_24),
                    contentDescription = "Filter the list"
                )
            }
        }

        if (openFilterDialog) {

            DatePickerDialog(
                onDismissRequest = {
                    // Dismiss the dialog when the user clicks outside the dialog or on the back
                    // button. If you want to disable that functionality, simply use an empty
                    // onDismissRequest.
                    openFilterDialog = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            openFilterDialog = false
                            hoursForSelectedDate.clear()
                            datePickerState.selectedDateMillis?.let { selectedDateMillis ->
                                hoursForSelectedDate.putAll(
                                    timestamp.filter {
                                        it.key in selectedDateMillis..<(selectedDateMillis + 86400000L)
                                    }
                                )
                                openHourDialog = true
                            }
                        },
                        enabled = confirmEnabled
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { openFilterDialog = false }) { Text("Cancel") }
                },
            ) {
                DatePicker(state = datePickerState, showModeToggle = false, title = {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Choose from previous recommendations", modifier = Modifier.padding(top = 16.dp), fontSize = 18.sp)
                    }
                })
            }
        }

        if (openHourDialog) {

            HourSelectionDialog(currentRecommendationTimestamp, hoursForSelectedDate,
                onHourSelected = { recommendationId, timestamp ->
                    fetchBooksFromId(recommendationId, timestamp)
                    openHourDialog = false
                },
                onDismissRequest = { openHourDialog = false }
            )

        }


        //handle empty map case here
        books.keys.sorted().forEachIndexed { index, genre ->
            val details = books[genre] ?: emptyList()
            HomeSection(genre) {
                LazyRow(
                    contentPadding = PaddingValues(15.dp),
                    modifier = Modifier.padding(bottom = if(index == books.keys.size - 1 && (books[genre]?.size
                            ?: 0) > 1
                    ) 72.dp else 0.dp)
                ) {
                    items(details) { item ->
                        BookItem(
                            item,
                            onBookSelected = { onBookSelected(item, details) },
                            onLikeDislike = onLikeDislike,
                        )
                    }
                }
            }
        }
    }

}


@Composable
fun HomeSection(title: String, content: @Composable () -> Unit) {
    Text(
        text = title.replaceFirstChar { it.uppercaseChar() }.replace("_", " ").replace("-"," "),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleLarge
    )
    content()
}