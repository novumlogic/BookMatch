package com.novumlogic.bookmatch.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.novumlogic.bookmatch.R
import com.novumlogic.bookmatch.ui.md_sys_light_primary_95
import com.novumlogic.bookmatch.views.CategoryChip

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategorySelectionScreen(
    categoryList: List<Pair<String, String>>,
    selectedCategories: List<String>,
    onChipSelectionChange: (String, Boolean) -> Unit,
    onContinueClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val categoryMap = categoryList.map {
        if (it.first in selectedCategories)
            Triple(it.first, it.second,true)
        else
            Triple(it.first, it.second, false)
    }

    Surface(modifier.fillMaxSize(), color = md_sys_light_primary_95) {

        BackHandler {
            onContinueClicked()
        }
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
            ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.label_select_categories),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(top = 70.dp)
            )
            Text(
                text = stringResource(R.string.label_select_least_categories),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            FlowRow(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 30.dp),
                horizontalArrangement = Arrangement.Center,
                maxItemsInEachRow = 2,
            ) {
                repeat(categoryMap.size) {
                    CategoryChip(categoryMap[it], onChipSelectionChange = onChipSelectionChange)
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onContinueClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 50.dp, end = 50.dp, bottom = 70.dp)
            ) {
                Text(text = "Continue", textAlign = TextAlign.Center)
            }


        }
    }
}
