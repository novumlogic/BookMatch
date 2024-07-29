package com.novumlogic.bookmatch.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.novumlogic.bookmatch.R
import com.novumlogic.bookmatch.views.CustomLoader
import com.novumlogic.bookmatch.screens.viewmodel.MainViewModel
import com.novumlogic.bookmatch.utils.ConnectionState
import com.novumlogic.bookmatch.utils.PreferenceKeys
import com.novumlogic.bookmatch.utils.connectivityState
import com.novumlogic.bookmatch.utils.currentConnectivityState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = PreferenceKeys.PREF_NAME)

enum class RecommendationStatus { LOADING, LOADED, FAILED }

enum class FetchSource { SUPABASE, AI }

enum class FailedReason { NO_INTERNET, AI_ERROR, NONE }

val dispatcherIO = Dispatchers.IO
val dispatcherMain = Dispatchers.Main

@Composable
fun App(modifier: Modifier = Modifier, viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val categoriesShown by viewModel.isCategoryShown(context).collectAsStateWithLifecycle(
        false,
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )
    val userLogged by viewModel.isUserLogged(context).collectAsStateWithLifecycle(
        false,
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )
    val selectedCategories by viewModel.getSelectedCategoryList(context)
        .collectAsStateWithLifecycle(
            emptyList(),
            lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
        )

    var failedReason by rememberSaveable { mutableStateOf(FailedReason.NONE) }

    val bookMap by viewModel.bookRecommendations.collectAsStateWithLifecycle(lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current)

    val lastRecommendationTime by viewModel.getLastRecommendationTime(context)
        .collectAsStateWithLifecycle(
            0,
            lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
        )

    if (viewModel.splashShown) {

        if (userLogged) {

            val selectedCategoryList = rememberSaveable {
                mutableListOf<String>().apply {
                    clear()
                    addAll(selectedCategories)
                }
            }

            if (categoriesShown) {

                var reloadCategoriesAndHistory by rememberSaveable { mutableStateOf(false) }
                var reloadRecommendations by rememberSaveable { mutableStateOf(false) }
                var recommendationStatus by rememberSaveable { mutableStateOf(RecommendationStatus.LOADING) }

                suspend fun getRecommendationStatus(): RecommendationStatus {
                    val currentTime = System.currentTimeMillis()
                    val status: RecommendationStatus

                    //To fetch the data from db when opened everytime after killing
                    if (viewModel.fetchSource == FetchSource.SUPABASE && lastRecommendationTime != 0L) {

                        status = viewModel.fetchBooksFromDb(lastRecommendationTime)
                        failedReason = FailedReason.NO_INTERNET

                    } else {

                        status = viewModel.getBookRecommendation(
                            selectedCategories,
                            currentTime
                        ) {
                            viewModel.setLastRecommendationTime(context, currentTime)
                        }
                        failedReason = FailedReason.AI_ERROR

                    }
                    viewModel.fetchSource = FetchSource.AI

                    return status
                }

                LaunchedEffect(reloadCategoriesAndHistory) {
                    if (reloadCategoriesAndHistory) {
                        viewModel.reloadInit()
                        reloadCategoriesAndHistory = false
                    }
                }

                LaunchedEffect(reloadRecommendations) {

                    if (reloadRecommendations) {
                        recommendationStatus = RecommendationStatus.LOADING
                        recommendationStatus = withContext(dispatcherIO) {
                            getRecommendationStatus()
                        }
                        reloadRecommendations = false

                    }
                }

                LaunchedEffect(selectedCategories) {
                    recommendationStatus = RecommendationStatus.LOADING
                    recommendationStatus = withContext(dispatcherIO) {
                        getRecommendationStatus()
                    }
                }

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

                    var currentRecommendationTimestamp by rememberSaveable(lastRecommendationTime) {
                        mutableLongStateOf(
                            lastRecommendationTime
                        )
                    }

                    AppContent(
                        viewModel.categoryList,
                        selectedCategories,
                        bookMap,
                        fetchRecommendationTimestamp = {
                            viewModel.fetchRecommendationTimestamp()
                        },
                        currentRecommendationTimestamp = currentRecommendationTimestamp,
                        fetchBooksFromId =
                        { id, timestamp ->
                            recommendationStatus = RecommendationStatus.LOADING
                            scope.launch(dispatcherIO) {
                                recommendationStatus = viewModel.fetchBooksFromRecommendationId(id,timestamp)
                                currentRecommendationTimestamp = timestamp
                            }
                        },
                        filterEnabled = {
                            recommendationStatus != RecommendationStatus.LOADING
                        },
                        onReloadCategoriesAndHistory = {
                            reloadCategoriesAndHistory = it
                        },
                        recommendationStatus,
                        onCategoryChipChange = { category, selected ->
                            if (selected) {
                                selectedCategoryList.add(category)
                            } else {
                                selectedCategoryList.remove(category)
                            }
                        },
                        onCategoryScreenContinue = {
                            if (selectedCategoryList.size < 2 || selectedCategoryList.size > 5) {
                                Toast.makeText(
                                    context,
                                    "Please select 2 to 5 categories",
                                    Toast.LENGTH_SHORT
                                ).show()
                                false
                            } else {
                                scope.launch(dispatcherIO) {
                                    viewModel.setCategoryShown(context, true)
                                    viewModel.setSelectedCategoryList(context, selectedCategoryList)
                                }
                                true
                            }
                        },
                        onLogout = {
                            scope.launch {
                                viewModel.signOut(context)
                            }
                        },
                        onRecommendMore = { liked, disliked, rating ->
                            recommendationStatus = RecommendationStatus.LOADING
                            scope.launch(dispatcherIO) {
                                val totalBooks = bookMap.flatMap { it.value.map { it.bookName } }
                                val currentTime = System.currentTimeMillis()
                                recommendationStatus =
                                    viewModel.recommendMore(liked, disliked, rating, totalBooks , selectedCategories ,currentTime) {
                                        viewModel.setLastRecommendationTime(context, currentTime)
                                    }
                                failedReason = FailedReason.AI_ERROR
                            }
                        },
                    )

                    if (recommendationStatus == RecommendationStatus.LOADING) {

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.8f))
                                .clickable(
                                    enabled = false, onClick = {}
                                )) {

                            CustomLoader()

                        }

                    }
                    else if (recommendationStatus == RecommendationStatus.FAILED) {

                        when(failedReason){
                            FailedReason.AI_ERROR -> {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                ){
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(stringResource(R.string.label_loading_failed), textAlign = TextAlign.Center)
                                        IconButton(onClick = {
                                            if (context.currentConnectivityState === ConnectionState.Available) {
                                                reloadRecommendations = true
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "No Internet",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                            }
                                        }) {
                                            Icon(Icons.Outlined.Refresh, null)
                                        }
                                    }

                                }


                            }
                            FailedReason.NO_INTERNET -> {
                                val connectionState by connectivityState()
                                val isConnected = connectionState === ConnectionState.Available

                                if (isConnected) {
                                    Toast.makeText(
                                        context,
                                        "Back online",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(context, "No internet", Toast.LENGTH_SHORT).show()
                                }

                                if (!isConnected) {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.padding(24.dp)
                                        ) {

                                            Text(
                                                stringResource(R.string.label_connect_internet),
                                                style = MaterialTheme.typography.headlineSmall,
                                                modifier = Modifier,
                                                lineHeight = 12.sp
                                            )
                                            ElevatedButton(onClick = {
                                                context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))

                                            }) {
                                                Text(stringResource(R.string.go_to_settings))
                                            }

                                        }
                                    }

                                } else {

                                    reloadCategoriesAndHistory = true
                                    viewModel.fetchSource = FetchSource.SUPABASE
                                    reloadRecommendations = true


                                }
                            }
                            FailedReason.NONE -> {
                                Unit
                            }
                            }

                    }


                }

            } else {

                CategorySelectionScreen(
                    viewModel.categoryList,
                    selectedCategories,
                    onChipSelectionChange = { category, selected ->
                        if (selected) {
                            selectedCategoryList.add(category)
                        } else {
                            selectedCategoryList.remove(category)
                        }
                    },
                    onContinueClicked = {
                        if (selectedCategoryList.size < 2 || selectedCategoryList.size > 5) {
                            Toast.makeText(
                                context,
                                "Please select between 2 to 5 categories",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {
                            scope.launch {
                                viewModel.setCategoryShown(context, true)
                                viewModel.setSelectedCategoryList(context, selectedCategoryList)
                            }
                        }
                    }
                )
            }

        } else {

            LoginScreen(signInHelper = viewModel.signInHelper, onLogin = {
                scope.launch(dispatcherIO) {
                    val saved = async {
                        viewModel.saveCurrentUser(context)
                    }.await()

                    if (saved) viewModel.setUserLogged(context, true)
                    withContext(dispatcherMain) {
                        if (saved)
                            Toast.makeText(
                                context,
                                "User logged in successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        else
                            Toast.makeText(
                                context,
                                "Something went wrong while saving",
                                Toast.LENGTH_SHORT
                            ).show()
                    }
                }
            }
            )
        }

    } else {

        SplashScreen(onSplashShown = {
            viewModel.isSessionValid(context)
            viewModel.splashShown = true
        })

    }
}



@Composable
fun UnderConstruction(onBack: () -> Unit) {
    BackHandler {
        onBack()
    }
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Text(text = "Screen coming soon", style = MaterialTheme.typography.titleMedium)

    }
}
