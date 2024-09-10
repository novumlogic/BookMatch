package com.novumlogic.bookmatch.views

import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadStatusButton(
    readStatus: Boolean,
    onChoice: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("Read", "Unread")
    SingleChoiceSegmentedButtonRow(modifier = modifier.selectableGroup()) {
        SegmentedButton(
            selected = readStatus,
            onClick = {
                onChoice(true)
            },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = options.size)
        ) {
            Text(options[0])
        }
        SegmentedButton(
            selected = !readStatus,
            onClick = {
                onChoice(false)
            },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = options.size)
        ) {
            Text(options[1])
        }

    }
}