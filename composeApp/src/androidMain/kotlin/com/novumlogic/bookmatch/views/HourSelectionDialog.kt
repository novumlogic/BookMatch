package com.novumlogic.bookmatch.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.novumlogic.bookmatch.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HourSelectionDialog(
    currentRecommendationTimestamp: Long,
    idTimestampMap: Map<Long, Int>,
    onHourSelected: (Int, Long) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.large)
            .heightIn(min = 300.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            Text(
                stringResource(R.string.label_choose_time),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(idTimestampMap.keys.toList()) { hour ->
                    val time =
                        Instant.ofEpochMilli(hour).atZone(ZoneId.systemDefault()).toLocalTime()
                    val formattedTime = time.format(DateTimeFormatter.ofPattern("hh:mm:ss a"))

                    if(hour == currentRecommendationTimestamp){
                        Button(
                            onClick = { onHourSelected(idTimestampMap[hour]!!, hour) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Time: $formattedTime",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }else{
                        OutlinedButton(
                            onClick = { onHourSelected(idTimestampMap[hour]!!, hour) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Time: $formattedTime",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = Color.Black
                            )
                        }

                    }

                }
            }

        }
    }
}