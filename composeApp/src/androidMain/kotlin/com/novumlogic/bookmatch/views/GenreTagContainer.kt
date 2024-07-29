package com.novumlogic.bookmatch.views

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GenreTagContainer(tags: List<String>, modifier: Modifier = Modifier) {
    FlowRow(modifier = modifier.padding(horizontal = 4.dp)) {
        tags.forEach {
            SuggestionChip(
                colors = SuggestionChipDefaults.suggestionChipColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.padding(horizontal = 4.dp),
                onClick = {},
                label = {
                    Text(
                        it,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            )
        }
    }

}

