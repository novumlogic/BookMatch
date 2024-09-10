package com.novumlogic.bookmatch.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novumlogic.bookmatch.R
import com.novumlogic.bookmatch.utils.convertToStartOfDayMillis
import com.novumlogic.bookmatch.utils.formatToDate
import com.novumlogic.bookmatch.utils.formatToTime
import com.novumlogic.bookmatch.utils.simpleVerticalScrollbar

@Composable
fun ExpandableRecommendationDateList(currentRecommendationTimestamp: Long, timestamps: Map<Long, Int>, onTimestampSelected: (Int,Long) -> Unit, onDismiss: ()-> Unit, modifier: Modifier = Modifier) {
    val startOfDayTimestamps = remember(timestamps) {
        timestamps.keys.map { it.convertToStartOfDayMillis() }.toSet().toList().reversed()
    }
    val listState = rememberLazyListState()

    val currentRecommendationTimestampStartOfDayMillis = currentRecommendationTimestamp.convertToStartOfDayMillis()
    val initialIndex = startOfDayTimestamps.indexOfFirst {
        it == currentRecommendationTimestampStartOfDayMillis
    }

    LaunchedEffect(key1 = initialIndex) {
        if(initialIndex >=0)
            listState.scrollToItem(initialIndex)
    }

    Column {

        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)){
            Text(text = "Choose from previous recommendations", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = { onDismiss() }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = null)
            }
        }

        HorizontalDivider()

        LazyColumn(
            modifier = modifier.simpleVerticalScrollbar(listState),
            state = listState
        ) {
            items(startOfDayTimestamps){
                val isInitiallyExpanded = it == currentRecommendationTimestampStartOfDayMillis
                ExpandableDateItem(currentRecommendationTimestamp, it, timestamps, onTimestampSelected = onTimestampSelected, isInitialExpanded = isInitiallyExpanded)
            }
        }
    }

}

@Composable
fun ExpandableDateItem(currentRecommendationTimestamp: Long, startOfDayMillis: Long, timestamps: Map<Long, Int>,  onTimestampSelected: (Int,Long) -> Unit, isInitialExpanded: Boolean = false, modifier: Modifier = Modifier) {

    var isExpanded by rememberSaveable {
        mutableStateOf(isInitialExpanded)
    }

    Column(modifier = modifier
        .fillMaxWidth()
        .padding(8.dp)

    ) {

        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .padding(16.dp)
            ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween){

            Text(text = startOfDayMillis.formatToDate(), fontWeight = FontWeight.Bold, fontSize = 20.sp , color = if(currentRecommendationTimestamp.convertToStartOfDayMillis() == startOfDayMillis) Color.Red else Color.Black)
            Icon(imageVector = if(!isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp, contentDescription = null)
        }

        if(isExpanded){
            RecommendationTimeline(currentRecommendationTimestamp, startOfDayMillis,timestamps, onTimestampSelected = onTimestampSelected)
        }
    }

}

@Composable
fun RecommendationTimeline(
    currentRecommendationTimestamp: Long,
    startOfDayMillis: Long,
    timestamps: Map<Long,Int>,
    onTimestampSelected: (Int,Long) -> Unit,
) {
    val list = remember(startOfDayMillis, timestamps) {
        timestamps.keys.filter { it in startOfDayMillis ..< startOfDayMillis + 86400000L }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp)

    ) {
        list.forEach { timestamp ->
            TimelineItem(
                currentRecommendationTimestamp,
                timestamp = timestamp,
                onTimestampSelected = {
                    onTimestampSelected(timestamps[it]!!,it)
                }
            )
        }
    }
}


@Composable
fun TimelineItem(
    currentRecommendationTimestamp: Long,
    timestamp: Long,
    onTimestampSelected: (Long)-> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTimestampSelected(timestamp) },
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            if(currentRecommendationTimestamp == timestamp){

                Spacer(
                    modifier = Modifier
                        .width(2.dp)
                        .height(15.dp)
                        .background(Color.Gray)
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color.Red, shape = CircleShape)
                )

                Spacer(
                    modifier = Modifier
                        .width(2.dp)
                        .height(15.dp)
                        .background(Color.Gray)
                )
            } else{
                Spacer(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .width(2.dp)
                        .height(48.dp)
                        .background(Color.Gray)
                )
            }


        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
        ) {
            Text(
                text = timestamp.formatToTime(),
            )
        }
    }
}


