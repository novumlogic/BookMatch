package com.novumlogic.bookmatch.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.novumlogic.bookmatch.R


@Composable
fun RatingBarContainer(
    rating: Int?,
    onRatingChange: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(text = stringResource(R.string.label_your_rating))
        StarRatingBar(rating, onRatingChange)
    }
}

@Composable
fun StarRatingBar(rating: Int?, onRatingChanged: (Int?) -> Unit, modifier: Modifier = Modifier) {
    for (i in 1..5) {
        val isSelected = rating?.let { i <= it } ?: false
        Icon(
            painter = if (isSelected) painterResource(R.drawable.ic_star) else painterResource(R.drawable.ic_star_border),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.selectable(
                selected = isSelected,
                onClick = {
                    if (rating == i && i == 1) {
                        onRatingChanged(null)
                    } else {
                        onRatingChanged(i)
                    }
                }
            )
        )
    }

}
