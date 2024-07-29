package com.novumlogic.bookmatch.screens

import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.novumlogic.bookmatch.R
import com.novumlogic.bookmatch.screens.viewmodel.ContentViewModel
import com.novumlogic.bookmatch.utils.Utils
import com.novumlogic.bookmatch.views.BookMatchBottomNavigation
import com.novumlogic.bookmatch.views.BookMatchRoute
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.BookDetails
import model.Recommendations


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent(
    categoryList: List<Pair<String, String>>,
    selectedCategories: List<String>,
    bookMap: Map<String, List<BookDetails>>,
    fetchRecommendationTimestamp: suspend () -> List<Recommendations>?,
    currentRecommendationTimestamp: Long,
    fetchBooksFromId: (Int, Long) -> Unit,
    filterEnabled: () -> Boolean,
    onReloadCategoriesAndHistory: (Boolean) -> Unit,
    recommendationStatus: RecommendationStatus,
    onCategoryChipChange: (String, Boolean) -> Unit,
    onCategoryScreenContinue: () -> Boolean,
    onRecommendMore: (likedBookList: List<String>, dislikedBookList: List<String>, ratings: Map<String, Int>) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ContentViewModel = viewModel()
) {
    //perform fetching booklist logic from selected categories and pass it in homescreen
    var selectedDestination by rememberSaveable { mutableStateOf(BookMatchRoute.HOME) }
    var showBookDetails by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var shouldReloadCategoryAndHistory by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val timestamp = remember { mutableMapOf<Long, Int>() }
    val currentTimestamp by rememberUpdatedState(currentRecommendationTimestamp)


    //Map<RecommendedBookId, BookName> or Map<RecommendedBookId, Pair<BookName, Rating>
    val likedBookMap = rememberSaveable { mutableMapOf<Int, String>() }
    val disLikedBookMap = rememberSaveable { mutableMapOf<Int, String>() }
    val ratingMap = rememberSaveable { mutableMapOf<Int, Pair<String, Int>>() }

    val showSnackbar = {
        scope.launch {
            if (snackbarHostState.currentSnackbarData == null) {

                val result = snackbarHostState.showSnackbar(
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
                        //action for snackbar dismissed
                    }
                }
            }
        }
    }

    LaunchedEffect(bookMap.hashCode()) {
        timestamp.clear()
        withContext(dispatcherIO) {
            val recommendations = fetchRecommendationTimestamp()
            val idTimestampMap = recommendations?.associate { it.timestamp to it.id }
            idTimestampMap?.let {
                timestamp.putAll(it)
            }
        }
    }


    Scaffold(modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = (selectedDestination == BookMatchRoute.HOME) && !showBookDetails
            ) {
                FloatingActionButton(
                    onClick = {
                        if (recommendationStatus == RecommendationStatus.LOADING) {
                            Toast.makeText(
                                context,
                                "Can't recommend more while loading",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            if (Utils.isNetworkAvailable(context)) {
                                onReloadCategoriesAndHistory(shouldReloadCategoryAndHistory)
                                onRecommendMore(
                                    likedBookMap.values.toList(),
                                    disLikedBookMap.values.toList(),
                                    ratingMap.values.toMap()
                                )
                                shouldReloadCategoryAndHistory = false
                            } else {
                                shouldReloadCategoryAndHistory = true
                                showSnackbar()
                            }
                        }

                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(bottom = 10.dp, end = 20.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_recommend),
                        null,
                        tint = Color.Unspecified
                    )

                }
            }
        },
        topBar = {
            AnimatedVisibility(
                visible = (selectedDestination == BookMatchRoute.HOME) && !showBookDetails,
                content = {
                    CenterAlignedTopAppBar(title = {
                        Text(
                            stringResource(R.string.label_match_book),
                        )
                    },
                        navigationIcon = {
                            Icon(
                                modifier = Modifier.size(50.dp),
                                tint = Color.Unspecified,
                                painter = painterResource(R.drawable.app_logo),
                                contentDescription = null
                            )
                        },
                        actions = {
                            var expanded by rememberSaveable { mutableStateOf(false) }

                            IconButton(onClick = {
                                expanded = true
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_profile),
                                    contentDescription = null
                                )
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.label_log_out)) },
                                    onClick = {
                                        expanded = false
                                        onLogout()
                                    },
                                    trailingIcon = {
                                        Icon(
                                            Icons.AutoMirrored.Outlined.ExitToApp,
                                            contentDescription = null
                                        )
                                    }
                                )
                            }

                        }
                    )
                }
            )

        },
        bottomBar = {
            AnimatedVisibility(
//                visible = (selectedDestination == BookMatchRoute.HOME || selectedDestination == BookMatchRoute.MY_BOOKS) && !showBookDetails
                visible = selectedDestination == BookMatchRoute.HOME && !showBookDetails
            ) {
                BookMatchBottomNavigation(
                    recommendationStatus,
                    selectedDestination = selectedDestination,
                    onDestinationChange = {
                        selectedDestination = it
                    }
                )

            }
        }) { paddingValues ->

        if (selectedDestination == BookMatchRoute.HOME) {

            var bookToShow by remember { mutableStateOf<BookDetails?>(null) }
            val booksList = remember { mutableListOf<BookDetails>() }
            if (!showBookDetails) {

                HomeScreen(
                    bookMap,
                    timestamp,
                    currentTimestamp,
                    fetchBooksFromId,
                    filterEnabled,
                    onBookSelected = { book, bList ->
                        bookToShow = book
                        booksList.clear()
                        booksList.addAll(bList)
                        showBookDetails = true

                    },
                    onLikeDislike = { recommendedBookId, bookName, value ->
                        // dislike == 0, like == 1
                        if (value == false) {
                            disLikedBookMap[recommendedBookId] = bookName
                            if (recommendedBookId in likedBookMap.keys) likedBookMap.remove(
                                recommendedBookId
                            )
                        } else if (value == true) {
                            likedBookMap[recommendedBookId] = bookName
                            if (recommendedBookId in disLikedBookMap.keys) disLikedBookMap.remove(
                                recommendedBookId
                            )
                        } else {
                            if (recommendedBookId in likedBookMap.keys) likedBookMap.remove(
                                recommendedBookId
                            )
                            if (recommendedBookId in disLikedBookMap.keys) disLikedBookMap.remove(
                                recommendedBookId
                            )
                        }
                        viewModel.changeLikeDislike(
                            recommendedBookId,
                            value,
                            System.currentTimeMillis()
                        )
                    },
                    showSnackbar = {
                        showSnackbar()
                    },
                    modifier = Modifier.padding(paddingValues)
                )

            } else {

                BookDetailScreen(
                    bookToShow!!,
                    booksList,
                    onBookSelected = { book ->
                        bookToShow = book
                        showBookDetails = true
                    },
                    onRatingChange = { recommendedBookId, bookName, rating ->
                        if (rating == null) {
                            if (recommendedBookId in ratingMap) ratingMap.remove(recommendedBookId)
                        } else {
                            ratingMap[recommendedBookId] = bookName to rating
                        }

                        viewModel.updateRatings(
                            recommendedBookId,
                            rating,
                            System.currentTimeMillis()
                        )

                    },
                    onRead = { recommendBookId, readStatus ->
                        viewModel.updateReadStatus(
                            recommendBookId,
                            readStatus,
                            System.currentTimeMillis()
                        )
                    },
                    onBack = {
                        showBookDetails = false
                    }
                )
            }

        } else if (selectedDestination == BookMatchRoute.EDIT) {

            CategorySelectionScreen(
                categoryList,
                selectedCategories,
                onChipSelectionChange = onCategoryChipChange,
                onContinueClicked = {
                    if (onCategoryScreenContinue()) selectedDestination = BookMatchRoute.HOME
                },
                modifier = Modifier.padding(paddingValues)
            )

        } else {
//            selectedDestination = BookMatchRoute.MY_BOOKS
            UnderConstruction(onBack = { selectedDestination = BookMatchRoute.HOME })
        }
    }

}
