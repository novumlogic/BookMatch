package com.novumlogic.bookmatch.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.novumlogic.bookmatch.R

@Composable
fun LikeDislikeButton(
    liked: Boolean?,
    onLike: () -> Unit,
    onDislike: () -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .selectableGroup()
            .clip(CircleShape),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            modifier = Modifier
                .weight(1f)
                .selectable(
                    selected = liked == false,
                    onClick = onDislike
                ),
            painter = if (liked == false) painterResource(R.drawable.ic_dislike_fill) else painterResource(
                R.drawable.ic_dislike
            ),
            contentDescription = null,
            tint = Color.Unspecified,
        )

        Icon(
            modifier = Modifier
                .weight(1f)
                .selectable(
                    selected = liked == true,
                    onClick = onLike
                ),
            painter = if (liked == true) painterResource(R.drawable.ic_like_fill) else painterResource(
                R.drawable.ic_like
            ),
            contentDescription = null,
            tint = Color.Unspecified
        )

    }
}
