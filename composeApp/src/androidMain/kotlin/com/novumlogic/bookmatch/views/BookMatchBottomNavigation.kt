package com.novumlogic.bookmatch.views

import androidx.annotation.StringRes
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.novumlogic.bookmatch.R
import com.novumlogic.bookmatch.screens.RecommendationStatus
import com.novumlogic.bookmatch.ui.md_sys_light_secondary_fixed_dim

@Composable
fun BookMatchBottomNavigation(recommendationStatus: RecommendationStatus, selectedDestination: String = BookMatchRoute.HOME, onDestinationChange: (String) -> Unit, modifier: Modifier = Modifier) {

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier
    ) {
        TOP_LEVEL_DESTINATIONS.forEach { destination ->
            NavigationBarItem(
                enabled = recommendationStatus != RecommendationStatus.LOADING,
                selected = selectedDestination == destination.route,
                icon = { Icon(painter = painterResource(destination.iconResId), null) },
                onClick = {
                    onDestinationChange(destination.route)
                },
                colors = NavigationBarItemDefaults.colors(indicatorColor = md_sys_light_secondary_fixed_dim)
            )
        }

    }
}

object BookMatchRoute {
    const val EDIT = "Edit"
    const val HOME = "Home"
}

data class BookMatchTopLevelDestination(
    val route: String,
    val iconResId: Int,
    @StringRes val iconTextId: Int
)

val TOP_LEVEL_DESTINATIONS = listOf(
    BookMatchTopLevelDestination(
        BookMatchRoute.EDIT,
        R.drawable.ic_edit,
        R.string.label_edit_books_choice
    ),
    BookMatchTopLevelDestination(
        BookMatchRoute.HOME,
        R.drawable.ic_home,
        R.string.label_home
    ),
)