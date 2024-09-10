package com.novumlogic.bookmatch.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.novumlogic.bookmatch.ui.md_sys_light_onSecondaryContainer_8o


@Composable
fun CategoryChip(
    categoryInfo: Triple<String,String, Boolean>,
    onChipSelectionChange: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var enabled by remember { if (categoryInfo.third) mutableStateOf(true) else mutableStateOf(false) }

    InputChip(
        onClick = {
            enabled = !enabled
            onChipSelectionChange(categoryInfo.first, enabled)
        },
        leadingIcon = {
            Text(categoryInfo.second)
        },
        label = {
            Text(
                text = categoryInfo.first,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        },
        selected = enabled,
        colors = InputChipDefaults.inputChipColors(
            leadingIconColor = MaterialTheme.colorScheme.primary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.primary,
            selectedContainerColor = md_sys_light_onSecondaryContainer_8o
        ),
        trailingIcon = {
            if (enabled)
                Icon(imageVector = Icons.Default.Close, contentDescription = null)
        },
        modifier = modifier.padding(horizontal = 5.dp),
        border = if(!enabled) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null
    )
}